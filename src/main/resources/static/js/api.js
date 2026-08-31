// Wrappers de fetch sobre la API REST del proyecto.
// Todas las funciones devuelven una Promise con el body parseado,
// o lanzan un objeto ProblemDetail si la respuesta no es 2xx.

const API_BASE = '/api';

async function apiFetch(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
        headers: { 'Content-Type': 'application/json', ...options.headers },
        ...options
    });

    if (response.status === 204) {
        return null;
    }

    const body = await response.json();

    if (!response.ok) {
        throw body; // ProblemDetail RFC 7807
    }

    return body;
}

// Artists
const ArtistApi = {
    findAll: (page = 0, size = 10) => apiFetch(`/artists?page=${page}&size=${size}`),
    findById: (id) => apiFetch(`/artists/${id}`),
    create: (data) => apiFetch('/artists', { method: 'POST', body: JSON.stringify(data) }),
    update: (id, data) => apiFetch(`/artists/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id) => apiFetch(`/artists/${id}`, { method: 'DELETE' })
};

// LPs (para futuras páginas)
const LpApi = {
    findAll: (page = 0, size = 10, artistName = null) => {
        const filter = artistName ? `&artistName=${encodeURIComponent(artistName)}` : '';
        return apiFetch(`/lps?page=${page}&size=${size}${filter}`);
    },
    findById: (id) => apiFetch(`/lps/${id}`),
    create: (data) => apiFetch('/lps', { method: 'POST', body: JSON.stringify(data) }),
    update: (id, data) => apiFetch(`/lps/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id) => apiFetch(`/lps/${id}`, { method: 'DELETE' })
};

// Report
const ReportApi = {
    discography: (page = 0, size = 10) => apiFetch(`/reports/discography?page=${page}&size=${size}`)
};
