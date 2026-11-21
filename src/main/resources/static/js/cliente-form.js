/* Pagina cliente-form.html: boton "Consultar DNI" (API externa via nuestro servidor) */
document.addEventListener('DOMContentLoaded', function () {
    const btn = document.getElementById('btnDni');
    if (!btn) return;
    const dni = document.getElementById('dni');
    const nombre = document.getElementById('nombre');
    const aviso = document.getElementById('avisoDni');

    async function consultar() {
        const valor = dni.value.trim();
        if (!/^\d{8}$/.test(valor)) {
            mostrarAviso(aviso, 'warning', 'Ingrese un DNI de 8 dígitos');
            return;
        }
        const textoOriginal = btn.innerHTML;
        btn.disabled = true;
        btn.textContent = 'Consultando...';
        try {
            const d = await apiGet('/api/dni/' + valor);
            nombre.value = d.nombreCompleto;
            nombre.readOnly = true;
            mostrarAviso(aviso, 'success', 'Datos obtenidos de la API de DNI: ' + d.nombreCompleto);
        } catch (e) {
            nombre.readOnly = false;
            nombre.focus();
            mostrarAviso(aviso, 'warning', e.message + ' — puede ingresar el nombre manualmente.');
        } finally {
            btn.disabled = false;
            btn.innerHTML = textoOriginal;
        }
    }

    btn.addEventListener('click', consultar);
    dni.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') { e.preventDefault(); consultar(); }
    });
});
