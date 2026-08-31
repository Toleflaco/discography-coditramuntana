// Helpers de UI reusables entre páginas.

// Muestra los fieldErrors de un ProblemDetail en los <small class="field-error">
// del formulario indicado.
function showFieldErrors(form, problemDetail) {
    clearFieldErrors(form);
    if (!problemDetail.fieldErrors) return;
    Object.entries(problemDetail.fieldErrors).forEach(([field, message]) => {
        const errorEl = form.querySelector(`.field-error[data-field="${field}"]`);
        if (errorEl) errorEl.textContent = message;
    });
}

// Limpia todos los mensajes de error del formulario.
function clearFieldErrors(form) {
    form.querySelectorAll('.field-error').forEach(el => el.textContent = '');
}

// Muestra un error genérico (para 404, 409, 500) con alert.
// Los 400 con fieldErrors se manejan con showFieldErrors.
function showApiError(problemDetail) {
    const title = problemDetail.title || 'Error';
    const detail = problemDetail.detail || 'Ocurrió un error inesperado.';
    alert(`${title}\n\n${detail}`);
}
