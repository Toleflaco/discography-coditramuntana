// Página artist-detail.html — lee ?id=N de la URL y carga:
// 1. Detalle del artista (nombre, descripción, lpCount).
// 2. Sus LPs (filtrando por artistName del propio artista).

const artistNameEl = document.getElementById('artist-name');
const artistDescEl = document.getElementById('artist-description');
const artistLpCountEl = document.getElementById('artist-lp-count');
const tbody = document.getElementById('lps-tbody');

const params = new URLSearchParams(window.location.search);
const artistId = params.get('id');

async function loadArtistDetail() {
    if (!artistId) {
        artistNameEl.textContent = 'ID de artista no especificado';
        return;
    }
    try {
        const artist = await ArtistApi.findById(Number(artistId));
        document.title = `${artist.name} · Discography`;
        artistNameEl.textContent = artist.name;
        artistDescEl.textContent = artist.description;
        artistLpCountEl.textContent = artist.lpCount;
        await loadArtistLps(artist.name);
    } catch (err) {
        showApiError(err);
        artistNameEl.textContent = 'Artista no encontrado';
    }
}

async function loadArtistLps(artistName) {
    try {
        const page = await LpApi.findAll(0, 100, artistName);
        renderLps(page.content);
    } catch (err) {
        showApiError(err);
    }
}

function renderLps(lps) {
    tbody.innerHTML = '';
    if (lps.length === 0) {
        tbody.innerHTML = '<tr><td colspan="2"><em>No hay LPs.</em></td></tr>';
        return;
    }
    lps.forEach(lp => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${escapeHtml(lp.name)}</td>
            <td>${escapeHtml(lp.description)}</td>
        `;
        tbody.appendChild(tr);
    });
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

loadArtistDetail();
