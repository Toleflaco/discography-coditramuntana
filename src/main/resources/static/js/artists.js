// Página artists.html
// Modo: create (form vacío) | update (form con datos + hidden id)

const form = document.getElementById('artist-form');
const formTitle = document.getElementById('form-title');
const submitBtn = document.getElementById('submit-btn');
const cancelBtn = document.getElementById('cancel-btn');
const idInput = document.getElementById('artist-id');
const nameInput = document.getElementById('artist-name');
const descInput = document.getElementById('artist-description');
const tbody = document.getElementById('artists-tbody');
const pageInfo = document.getElementById('page-info');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');

let currentPage = 0;
const PAGE_SIZE = 10;

// -------------------- LISTADO --------------------

async function loadArtists() {
    try {
        const page = await ArtistApi.findAll(currentPage, PAGE_SIZE);
        renderTable(page.content);
        renderPagination(page.page);
    } catch (err) {
        showApiError(err);
    }
}

function renderTable(artists) {
    tbody.innerHTML = '';
    if (artists.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4"><em>No hay artistas.</em></td></tr>';
        return;
    }
    artists.forEach(artist => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${escapeHtml(artist.name)}</td>
            <td>${escapeHtml(artist.description)}</td>
            <td>—</td>
            <td>
                <a href="/artist-detail.html?id=${artist.id}">Ver</a>
                <button type="button" data-action="edit" data-id="${artist.id}">Editar</button>
                <button type="button" data-action="delete" data-id="${artist.id}" class="secondary">Borrar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function renderPagination(pageMeta) {
    const total = pageMeta.totalPages;
    const num = pageMeta.number;
    pageInfo.textContent = total === 0 ? 'Sin resultados' : `Página ${num + 1} de ${total}`;
    prevBtn.disabled = num === 0;
    nextBtn.disabled = num + 1 >= total;
}

// Evento sobre la tabla (delegación) para editar/borrar.
tbody.addEventListener('click', async (e) => {
    const btn = e.target.closest('button[data-action]');
    if (!btn) return;
    const id = Number(btn.dataset.id);
    const action = btn.dataset.action;
    if (action === 'edit') await enterEditMode(id);
    if (action === 'delete') await deleteArtist(id);
});

prevBtn.addEventListener('click', () => { if (currentPage > 0) { currentPage--; loadArtists(); } });
nextBtn.addEventListener('click', () => { currentPage++; loadArtists(); });

// -------------------- FORMULARIO --------------------

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearFieldErrors(form);
    const data = { name: nameInput.value, description: descInput.value };
    const id = idInput.value;
    try {
        if (id) {
            await ArtistApi.update(Number(id), data);
        } else {
            await ArtistApi.create(data);
        }
        resetForm();
        loadArtists();
    } catch (err) {
        if (err.fieldErrors) {
            showFieldErrors(form, err);
        } else {
            showApiError(err);
        }
    }
});

cancelBtn.addEventListener('click', resetForm);

async function enterEditMode(id) {
    try {
        const artist = await ArtistApi.findById(id);
        idInput.value = artist.id;
        nameInput.value = artist.name;
        descInput.value = artist.description;
        formTitle.textContent = `Editar: ${artist.name}`;
        submitBtn.textContent = 'Actualizar';
        cancelBtn.hidden = false;
        window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err) {
        showApiError(err);
    }
}

function resetForm() {
    form.reset();
    idInput.value = '';
    formTitle.textContent = 'Nuevo artista';
    submitBtn.textContent = 'Crear';
    cancelBtn.hidden = true;
    clearFieldErrors(form);
}

async function deleteArtist(id) {
    if (!confirm('¿Borrar este artista?')) return;
    try {
        await ArtistApi.delete(id);
        loadArtists();
    } catch (err) {
        showApiError(err);
    }
}

// -------------------- UTIL --------------------

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

// -------------------- ARRANQUE --------------------

loadArtists();
