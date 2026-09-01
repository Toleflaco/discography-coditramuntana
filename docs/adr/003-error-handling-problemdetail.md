# ADR-003: Política de errores REST con ProblemDetail RFC 7807 y jerarquía polimórfica de excepciones de dominio

Status: Accepted
Date: 2026-08-28

## Contexto

El PDF del enunciado no menciona política de errores REST. La decisión es
puramente de arquitectura interna, tomada en función de dos fuerzas del
proyecto y una del ecosistema.

Cuatro fuerzas condicionan la decisión:

1. **El enunciado no especifica formato de errores.** No hay instrucción del
   PDF que respetar o desafiar. La decisión es libre.

2. **Eje declarado por el cliente: backend sólido.** El correo del equipo de
   People & Talent pide "backend sólido y bien desarrollado". La robustez
   incluye la comunicación de errores al cliente: un backend cuyos errores
   son opacos o inconsistentes no cumple ese criterio. Los errores tienen
   que ser estructurados, distinguibles y accionables por el cliente.

3. **Comportamiento por defecto de Spring Boot insuficiente.** Sin handler
   custom, Spring escribe el stack trace en el log del servidor y devuelve
   al cliente un JSON con formato propio (`{timestamp, status, error, path}`)
   que no identifica qué recurso, qué campo falló, ni por qué. El cliente no
   tiene información accionable, y el formato es específico de Spring, no
   interoperable.

4. **Existe estándar IETF aplicable: RFC 7807 ("Problem Details for HTTP
   APIs").** Define un contrato estructurado con `type`, `title`, `status`,
   `detail`, `instance` y campos de extensión personalizados. Spring Boot 3+
   incluye soporte nativo (`ProblemDetail`, `ErrorResponseException`) sin
   necesidad de librerías externas.

## Alternativas consideradas

### Comportamiento por defecto de Spring Boot

No añadir handler custom y dejar el comportamiento por defecto de Spring
Boot. Descartado por ser incompatible con la fuerza 2 del Contexto (backend
sólido según el correo del cliente): el JSON de error por defecto no
identifica qué recurso, qué campo falló, ni por qué. Un backend cuya API
expone errores opacos no cumple el criterio del cliente.

### Formato de error custom propio del proyecto

Inventar un formato de error propio del proyecto (`{codigo, mensaje,
detalles}` o similar). Descartado por dos razones. Primero, interoperabilidad:
cualquier cliente REST reconoce ProblemDetail RFC 7807 sin documentación
adicional; un formato custom obliga a cada consumidor a aprender el contrato
específico del proyecto. Segundo, onboarding: quien entra nuevo al proyecto
tiene que aprender otro formato de errores propietario; con ProblemDetail
solo tiene que saber que es el estándar RFC.

### ProblemDetail RFC 7807 + jerarquía polimórfica de excepciones de dominio (elegida)

Combina el estándar IETF con una jerarquía de excepciones custom estructurada
en dos niveles: excepciones base genéricas (`ResourceNotFoundException`,
`ResourceConflictException`) que encapsulan los datos comunes (`resourceType`,
`resourceId`, mensaje formateado) y excepciones concretas de dominio
(`ArtistNotFoundException`, `LpNotFoundException`,
`ArtistNameAlreadyExistsException`, `ArtistHasLpsException`,
`LpAlreadyExistsForArtistException`) que heredan de las bases y solo aportan
la información específica del caso.

El `GlobalExceptionHandler` solo tiene métodos para las bases genéricas; las
excepciones de dominio se resuelven polimórficamente por herencia. Cuando un
service lanza `ArtistNotFoundException`, Spring busca el handler compatible
en la jerarquía de tipos y encuentra `handleResourceNotFound(ResourceNotFoundException)`.
Consecuencia arquitectónica: añadir una excepción de dominio nueva
(por ejemplo `SongNotFoundException` en el futuro) requiere crear la clase
heredando de la base y lanzarla; el handler no se toca.

## Decisión

### Jerarquía de excepciones custom en dos niveles

Excepciones base abstractas en `shared/error/`:

- `ResourceNotFoundException` (abstracta): encapsula `resourceType` y
  `resourceId`, formatea mensaje `"{Tipo} with id {id} not found"`.
- `ResourceConflictException`: encapsula mensaje de conflicto de negocio.

Excepciones concretas de dominio en el paquete de cada entidad:

- `artist/exception/ArtistNotFoundException` extends `ResourceNotFoundException`.
- `artist/exception/ArtistNameAlreadyExistsException` extends `ResourceConflictException`.
- `artist/exception/ArtistHasLpsException` extends `ResourceConflictException`.
- `lp/exception/LpNotFoundException` extends `ResourceNotFoundException`.
- `lp/exception/LpAlreadyExistsForArtistException` extends `ResourceConflictException`.

### GlobalExceptionHandler

`GlobalExceptionHandler` con `@RestControllerAdvice` centraliza el mapeo de
excepciones a `ProblemDetail` RFC 7807. Cuatro handlers, no uno por cada
excepción de dominio:

- `handleResourceNotFound(ResourceNotFoundException)` → HTTP 404. Extensiones:
  `resourceType`, `resourceId`.
- `handleResourceConflict(ResourceConflictException)` → HTTP 409.
- `handleValidation(MethodArgumentNotValidException)` → HTTP 400. Extensión:
  `fieldErrors` (mapa campo → mensaje).
- `handleTypeMismatch(MethodArgumentTypeMismatchException)` → HTTP 400.
  Mensaje formateado con nombre del parámetro, valor recibido y tipo esperado.

### Formato de respuesta

Todas las respuestas de error siguen `ProblemDetail` RFC 7807 con
`Content-Type: application/problem+json`. Los campos estándar (`type`,
`title`, `status`, `detail`, `instance`) se rellenan automáticamente por
Spring; las extensiones (`resourceType`, `resourceId`, `fieldErrors`) se
añaden por handler cuando aplican.

Ejemplo real de respuesta ante POST `/api/artists` con body inválido
`{"name":"","description":""}`:

```json
{
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields have invalid values",
  "instance": "/api/artists",
  "fieldErrors": {
    "name": "size must be between 1 and 100",
    "description": "must not be blank"
  }
}
```

## Consecuencias

### Positivas

- **Errores accionables por el cliente sin documentación adicional.** Un
  cliente que recibe un 404 con `resourceType: "Artist"` y `resourceId: 99999`
  sabe exactamente qué recurso no encontró; un cliente que recibe un 400 con
  `fieldErrors: {name, description}` sabe qué campos rechazar. Ambos casos
  sin necesidad de leer la documentación del API para interpretar la
  respuesta.

- **Extensibilidad del dominio sin tocar el handler.** Añadir excepciones de
  dominio futuras (`SongNotFoundException`, `AuthorNotFoundException`)
  requiere solo crear la clase heredando de la base y lanzarla desde el
  service. El `GlobalExceptionHandler` no se toca. Propiedad presente del
  código, verificable abriendo los ficheros.

- **Consistencia garantizada por centralización.** Todos los 404 tienen
  exactamente los mismos campos (`resourceType`, `resourceId`, `detail`,
  `instance`, `status`, `title`) porque vienen del mismo handler. No hay
  riesgo de que un endpoint devuelva `{tipo, id}` y otro `{resource,
  resourceId}` — la centralización lo previene por construcción.

### Negativas

- **Trade-off aceptado sin evaluar alternativa más simple.** La jerarquía
  polimórfica añade nueve clases al proyecto (dos bases + siete concretas).
  Existe alternativa más simple: `ProblemDetail` sin jerarquía, mapeando
  excepciones genéricas de Spring directamente en el handler. Esa alternativa
  no se evaluó explícitamente durante el diseño — se fue directo a jerarquía
  polimórfica por familiaridad con el patrón. El trade-off aceptado es más
  código a cambio de más granularidad semántica; en un proyecto de mayor
  volumen la relación coste/beneficio favorece la jerarquía, pero para un
  proyecto de este alcance la alternativa más simple sería defendible.

- **Cobertura incompleta de excepciones fuera de handler.** Los cuatro
  handlers cubren los casos actuales (recurso no encontrado, conflicto de
  negocio, validación Bean Validation, tipo de parámetro inválido). No
  cubren otras excepciones que pueden aparecer en runtime:
  `HttpMessageNotReadableException` (JSON malformado en body),
  `HttpRequestMethodNotSupportedException` (verbo HTTP no soportado),
  `NoResourceFoundException` (path no mapeado),
  `DataIntegrityViolationException` (constraint de BD violada). Cuando
  ocurre alguna de estas, Spring devuelve su respuesta por defecto, rompiendo
  la coherencia con el resto de la política. Es deuda técnica documentada.
