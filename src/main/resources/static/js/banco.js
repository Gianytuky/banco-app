/* Utilidades compartidas por las paginas (sin librerias externas) */

function fmtMoney(n) {
    return Number(n).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

async function apiGet(url) {
    const r = await fetch(url, { headers: { 'Accept': 'application/json' } });
    let data = {};
    try { data = await r.json(); } catch (e) { /* respuesta sin cuerpo */ }
    if (!r.ok) {
        throw new Error(data.mensaje || 'Error en la consulta (' + r.status + ')');
    }
    return data;
}

/* Muestra un mensaje Bootstrap (alert) dentro de un contenedor */
function mostrarAviso(el, tipo, texto) {
    el.className = 'alert alert-' + tipo + ' mt-3 mb-0 py-2';
    el.textContent = texto;
}

/* Rellena un panel de informacion con lineas "Etiqueta: valor" (usa textContent: sin riesgo de XSS) */
function mostrarInfo(panel, tipo, lineas) {
    panel.className = 'alert alert-' + tipo + ' mt-3 mb-0';
    panel.replaceChildren();
    lineas.forEach(function (par) {
        const d = document.createElement('div');
        if (par[0]) {
            const b = document.createElement('strong');
            b.textContent = par[0] + ': ';
            d.appendChild(b);
        }
        d.appendChild(document.createTextNode(par[1]));
        panel.appendChild(d);
    });
}

/* Busca una cuenta por su numero y muestra titular(es), tipo, saldo y estado */
async function buscarCuenta(inputId, panelId) {
    const nro = document.getElementById(inputId).value.trim();
    const panel = document.getElementById(panelId);
    if (!nro) {
        mostrarInfo(panel, 'warning', [['', 'Ingrese el número de cuenta']]);
        return null;
    }
    try {
        const c = await apiGet('/api/cuentas/' + encodeURIComponent(nro));
        mostrarInfo(panel, c.estado === 'ACTIVA' ? 'info' : 'warning', [
            ['Titular(es)', c.titulares],
            ['Tipo', c.tipo + ' (' + c.moneda + ')'],
            ['Saldo actual', c.simbolo + ' ' + fmtMoney(c.saldo)],
            ['Estado', c.estado]
        ]);
        return c;
    } catch (e) {
        mostrarInfo(panel, 'danger', [['', e.message]]);
        return null;
    }
}

/* Conecta un boton "Buscar" con un campo; Enter en el campo tambien dispara la busqueda */
function enlazarBusqueda(inputId, botonId, accion) {
    const input = document.getElementById(inputId);
    const boton = document.getElementById(botonId);
    if (!input || !boton) return;
    boton.addEventListener('click', accion);
    input.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') { e.preventDefault(); accion(); }
    });
}

/* Paginas de operaciones (deposito, retiro, transferencia) */
document.addEventListener('DOMContentLoaded', function () {
    const nro = document.getElementById('nro');
    if (!nro || !document.getElementById('infoCuenta')) return;

    const simbolo = document.getElementById('simbolo');
    async function buscar() {
        const c = await buscarCuenta('nro', 'infoCuenta');
        if (simbolo) simbolo.textContent = c ? c.simbolo : 'S/';
    }
    enlazarBusqueda('nro', 'btnBuscarCuenta', buscar);
    if (nro.value.trim()) buscar();

    // Transferencia: cuenta destino
    const destino = document.getElementById('destino');
    if (destino && document.getElementById('infoDestino')) {
        const buscarDestino = function () { return buscarCuenta('destino', 'infoDestino'); };
        enlazarBusqueda('destino', 'btnBuscarDestino', buscarDestino);
        if (destino.value.trim()) buscarDestino();
    }
});
