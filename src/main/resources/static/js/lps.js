// Página lps.html
// Modo: create (form vacío, select de artista habilitado) | update (form con datos + hidden id, select deshabilitado)

const form = document.getElementById('lp-form');
const formTitle = document.getElementById('form-title');
const submitBtn = document.getElementById('submit-btn');
const cancelBtn = document.getElementById('cancel-btn');
const idInput = document.getElementById('lp-id');
const artistSelect = document.getElementById('artist-id-select');
const nameInput = document.getElementById('lp-name');
const descInput = document.getElementById('lp-description');
const tbody = document.getElementById('lps-tbody');
const pageInfo = document.getElementById('page-info');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');
const filterForm = document.getElementById('filter-form');
const filterInput = document.getElementById('filter-artist');
const clearFilterBtn = document.getElementById('clear-filter-btn');

let currentPage = 0;
const PAGE_SIZE = 10;
let currentFilter = null;

// -------------------- SELECT DE ARTISTAS --------------------

async function loadArtistOptions() {
    try {
        const page = await ArtistApi.findAll(0, 100);
        page.content.forEach(artist => {
            const option = document.createElement('option');
            option.value = artist.id;
            option.textContent = artist.name;
            artistSelect.appendChild(option);
        });
    } catch (err) {
        showApiError(err);
    }
}

// -------------------- LISTADO --------------------

async function loadLps() {
    try {
        const page = await LpApi.findAll(currentPage, PAGE_SIZE, currentFilter);
        renderTable(page.content);
        renderPagination(page.page);
    } catch (err) {
        showApiError(err);
    }
}

function renderTable(lps) {
    tbody.innerHTML = '';
    if (lps.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4"><em>No hay LPs.</em></td></tr>';
        return;
    }
    lps.forEach(lp => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${escapeHtml(lp.name)}</td>
            <td>${escapeHtml(lp.description)}</td>
            <td>${escapeHtml(lp.artistName)}</td>
            <td>
                <button type="button" data-action="edit" data-id="${lp.id}">Editar</button>
                <button type="button" data-action="delete" data-id="${lp.id}" class="secondary">Borrar</button>
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
    if (action === 'delete') await deleteLp(id);
});

prevBtn.addEventListener('click', () => { if (currentPage > 0) { currentPage--; loadLps(); } });
nextBtn.addEventListener('click', () => { currentPage++; loadLps(); });

// -------------------- FILTRO --------------------

filterForm.addEventListener('submit', (e) => {
    e.preventDefault();
    currentFilter = filterInput.value.trim() || null;
    currentPage = 0;
    loadLps();
});

clearFilterBtn.addEventListener('click', () => {
    filterInput.value = '';
    currentFilter = null;
    currentPage = 0;
    loadLps();
});

// -------------------- FORMULARIO --------------------

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearFieldErrors(form);
    const id = idInput.value;
    try {
        if (id) {
            const data = { name: nameInput.value, description: descInput.value };
            await LpApi.update(Number(id), data);
        } else {
            const data = {
                name: nameInput.value,
                description: descInput.value,
                artistId: Number(artistSelect.value)
            };
            await LpApi.create(data);
        }
        resetForm();
        loadLps();
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
        const lp = await LpApi.findById(id);
        idInput.value = lp.id;
        nameInput.value = lp.name;
        descInput.value = lp.description;

        // La relación LP-Artist es inmutable: el select se muestra deshabilitado
        // con el artista actual para dar contexto, no para permitir cambiarlo.
        if (!artistSelect.querySelector(`option[value="${lp.artist.id}"]`)) {
            const option = document.createElement('option');
            option.value = lp.artist.id;
            option.textContent = lp.artist.name;
            artistSelect.appendChild(option);
        }
        artistSelect.value = lp.artist.id;
        artistSelect.disabled = true;

        formTitle.textContent = `Editar: ${lp.name}`;
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
    artistSelect.disabled = false;
    artistSelect.value = '';
    formTitle.textContent = 'Nuevo LP';
    submitBtn.textContent = 'Crear';
    cancelBtn.hidden = true;
    clearFieldErrors(form);
}

async function deleteLp(id) {
    if (!confirm('¿Borrar este LP?')) return;
    try {
        await LpApi.delete(id);
        loadLps();
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

loadArtistOptions();
loadLps();
