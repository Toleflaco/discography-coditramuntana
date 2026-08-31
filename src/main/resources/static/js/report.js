// Página report.html — solo tabla read-only con paginación.

const tbody = document.getElementById('report-tbody');
const pageInfo = document.getElementById('page-info');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');

let currentPage = 0;
const PAGE_SIZE = 10;

async function loadReport() {
    try {
        const page = await ReportApi.discography(currentPage, PAGE_SIZE);
        renderTable(page.content);
        renderPagination(page.page);
    } catch (err) {
        showApiError(err);
    }
}

function renderTable(rows) {
    tbody.innerHTML = '';
    if (rows.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4"><em>No hay datos.</em></td></tr>';
        return;
    }
    rows.forEach(row => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${escapeHtml(row.lpName)}</td>
            <td>${escapeHtml(row.artistName)}</td>
            <td>${row.songCount}</td>
            <td>${escapeHtml(row.authorsCsv)}</td>
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

prevBtn.addEventListener('click', () => { if (currentPage > 0) { currentPage--; loadReport(); } });
nextBtn.addEventListener('click', () => { currentPage++; loadReport(); });

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

loadReport();
