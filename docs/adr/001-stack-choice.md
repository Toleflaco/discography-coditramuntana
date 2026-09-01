# ADR-001: Elección del stack — Spring Boot 4 + Java 21

Status: Accepted
Date: 2026-08-27

## Contexto

Prueba técnica del proceso de selección de Coditramuntana. Alcance definido por
el PDF del enunciado: CRUD de Artist y LP con campos `name:string,
description:text`, modelos Artist / LP / Song / Author con asociaciones
específicas (Artist 1—N LP, LP 1—N Song, Song N—N Author), endpoint de reporte
agregado (LP, artista, número de canciones, autores CSV), filtro por nombre de
artista, seeds con al menos 5 artistas. Sin autenticación, sin integraciones
externas, sin concurrencia pesada. La BD requerida por el PDF es SQLite.

El PDF asume que el candidato viene del mundo Ruby on Rails o del mundo PHP
Laravel — lo dice explícitamente ("Rails developers must use Ruby on Rails",
"PHP developers must use Laravel") y usa sintaxis literal de generadores de
Rails (`name:string, description:text`) para describir los campos.

El correo posterior del equipo de People & Talent modula el enunciado en dos
puntos clave:

1. Autoriza libertad de stack: "puedes utilizar el lenguaje o tecnologías con
   las que te sientas más cómodo".
2. Explicita el eje del proyecto: "frontend muy básico, simplemente a modo de
   interfaz para poder probar la aplicación, y que centres principalmente tus
   esfuerzos en realizar un backend sólido y bien desarrollado".

El enunciado efectivo es, por tanto, PDF + modulación por correo. El foco
declarado por el cliente es **backend sólido**, con frontend testimonial.

## Alternativas consideradas

### Frameworks Rails-like (Ruby on Rails y Laravel)

Rails (Ruby, 2004) y Laravel (PHP, 2011) son frameworks web MVC con filosofía
compartida: convention over configuration, generadores de scaffolding, ORM
integrado (ActiveRecord y Eloquent respectivamente), sintaxis expresiva. Son
las dos alternativas nombradas explícitamente por el PDF.

Ambos son extremadamente productivos para el alcance del enunciado: un CRUD
con las asociaciones descritas se genera y funciona en minutos.

Descartados porque en Rails y Laravel muchas de las decisiones que la prueba
pide mostrar (separación DTO / entidad, jerarquía de excepciones custom,
respuestas de error normalizadas, validación en la frontera del sistema)
vienen tomadas por convención del framework y no son código visible que el
revisor pueda leer y evaluar como decisión del candidato. En Java + Spring
Boot cada una de esas elecciones es código explícito escrito por mí:
`ProblemDetail` RFC 7807, jerarquía polimórfica de excepciones de dominio,
DTOs de entrada con `@Valid`, guard clauses de paginación, `@EntityGraph` /
JOIN FETCH para prevenir N+1. Para una prueba técnica cuyo eje declarado por
el cliente es "backend sólido y bien desarrollado", un stack que hace visibles
las decisiones favorece la evaluación frente a un stack que las esconde bajo
convención.

### Node.js (Express o NestJS)

Node.js es la alternativa moderna equivalente que un revisor técnico esperaría
ver considerada. NestJS comparte incluso filosofía arquitectónica con Spring:
inyección de dependencias, decoradores tipo anotaciones, pipes de validación.

Descartado porque el tipado de TypeScript funciona solo en tiempo de
compilación y no bloquea en runtime, con lo que las validaciones en la
frontera del sistema (DTOs de entrada) son menos fiables que con Java tipado
+ Bean Validation. Para una prueba donde el eje del enunciado modulado por
correo es la solidez del backend, ese trade-off desfavorece a Node.

## Decisión

Backend implementado con **Spring Boot 4.1 sobre Java 21 (LTS)**. Persistencia
con Spring Data JPA + Hibernate + Flyway. Documentación de API con
springdoc-openapi. Tests con JUnit 5 + Mockito (estilo BDDMockito) + MockMvc
+ AssertJ. Cobertura con JaCoCo. CI con GitHub Actions. Build reproducible con
Maven Wrapper. Frontend testimonial con HTML + JavaScript vanilla + Pico.css
(classless), servido por Spring.

## Consecuencias

### Positivas

- **Decisiones explícitas y visibles.** DTOs, excepciones de dominio,
  `ProblemDetail`, guard clauses, validación en frontera — todo es código
  del candidato, no convención del framework, y por tanto evaluable por el
  revisor.
- **Tipado fuerte y validación en compilación.** Errores estructurales
  (paso de tipos, ausencia de campos requeridos, incompatibilidades de
  firma) se detectan antes de tests o de runtime.
- **Ecosistema maduro para el modelo del enunciado.** JPA / Hibernate cubre
  las relaciones descritas (1—N, N—N) sin fricción, con estrategias
  documentadas para prevenir N+1 (`@EntityGraph`, JOIN FETCH).
- **Java 21 LTS.** Estabilidad garantizada a medio plazo, features modernas
  (records, pattern matching, text blocks) que reducen boilerplate frente
  a Java 8 o 11.

### Negativas

- **Menor velocidad de scaffolding inicial.** Spring Boot requiere
  configurar dependencias, entidades, DTOs, repositorios, servicios y
  controladores como código explícito. Un CRUD equivalente en Rails o
  Laravel se genera con un comando y funciona en minutos. En este proyecto
  ese coste se pagó las primeras sesiones. El trade-off aceptado es que
  ese código, una vez escrito, es visible y evaluable por el revisor.
- **Tiempo de arranque de la aplicación.** Spring Boot 4 arranca la
  aplicación entera en ~3-5 segundos en local. Rails o Laravel arrancan
  más rápido en desarrollo, y Node arranca casi instantáneo. Impacto
  despreciable para este proyecto (no hay hot-reload continuo), pero
  perceptible en flujos con muchos ciclos arranque / parada.
- **Sesgo del revisor con background en Rails o Laravel.** El enunciado del
  PDF asume Rails o Laravel. Un revisor con esa referencia mental puede
  encontrar el código Spring más verboso o menos idiomático. La estructura
  por paquetes de dominio, las anotaciones Spring y la separación en capas
  pueden parecer sobreingeniería desde una mentalidad "en Rails esto son
  tres comandos". Riesgo mitigado con README y ADRs como éste, pero
  existe.

### Neutras

- **Build con Maven Wrapper.** No se requiere Maven instalado en la máquina
  del revisor. Alternativa considerada Gradle, descartada por menor
  familiaridad del ecosistema Spring y por consistencia con la
  documentación oficial de Spring Boot que usa Maven en los ejemplos.
- **Frontend fuera del stack Spring.** El enunciado modulado por correo
  reduce el frontend a testimonial. Vanilla HTML + JavaScript vanilla +
  Pico.css cumple la instrucción sin acoplar el proyecto a un framework
  frontend adicional que no aportaría al eje declarado (backend sólido).
