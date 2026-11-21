/* Pagina apertura-cuenta.html */
document.addEventListener('DOMContentLoaded', function () {
    const dni = document.getElementById('dni');
    const info = document.getElementById('infoCliente');
    const tipo = document.getElementById('tipCta');
    const bloque = document.getElementById('bloqueTitulares');
    const simbolo = document.getElementById('simbolo');
    const lista = document.getElementById('listaTitulares');
    const nuevo = document.getElementById('nuevoTitular');
    const aviso = document.getElementById('avisoTitular');

    async function buscarCliente() {
        const valor = dni.value.trim();
        if (!/^\d{8}$/.test(valor)) {
            mostrarInfo(info, 'warning', [['', 'Ingrese un DNI de 8 dígitos']]);
            return;
        }
        try {
            const c = await apiGet('/api/clientes/' + valor);
            mostrarInfo(info, 'success', [['Cliente', c.nombre], ['Código', c.cliCod], ['Correo', c.email]]);
        } catch (e) {
            mostrarInfo(info, 'danger', [['', e.message + '. Regístrelo primero en Clientes > Nuevo cliente.']]);
        }
    }

    function actualizarTipo() {
        const opt = tipo.options[tipo.selectedIndex];
        const mancomunada = !!opt && opt.dataset.mancomunada === 'true';
        bloque.classList.toggle('d-none', !mancomunada);
        simbolo.textContent = (opt && opt.dataset.simbolo) ? opt.dataset.simbolo : 'S/';
    }

    function yaAgregado(valor) {
        return Array.from(lista.querySelectorAll('input[name=titularesDni]')).some(function (i) { return i.value === valor; });
    }

    async function agregarTitular() {
        const valor = nuevo.value.trim();
        if (!/^\d{8}$/.test(valor)) { mostrarAviso(aviso, 'warning', 'Ingrese un DNI de 8 dígitos'); return; }
        if (valor === dni.value.trim()) { mostrarAviso(aviso, 'warning', 'Ese cliente ya es el titular principal'); return; }
        if (yaAgregado(valor)) { mostrarAviso(aviso, 'warning', 'Ese titular ya fue agregado'); return; }
        try {
            const c = await apiGet('/api/clientes/' + valor);
            const li = document.createElement('li');
            li.className = 'list-group-item d-flex justify-content-between align-items-center';
            const span = document.createElement('span');
            span.textContent = c.dni + ' — ' + c.nombre;
            const oculto = document.createElement('input');
            oculto.type = 'hidden';
            oculto.name = 'titularesDni';
            oculto.value = c.dni;
            const quitar = document.createElement('button');
            quitar.type = 'button';
            quitar.className = 'btn btn-sm btn-outline-danger btn-quitar';
            quitar.textContent = 'Quitar';
            li.append(span, oculto, quitar);
            lista.appendChild(li);
            nuevo.value = '';
            aviso.className = 'd-none';
        } catch (e) {
            mostrarAviso(aviso, 'danger', e.message);
        }
    }

    lista.addEventListener('click', function (e) {
        const b = e.target.closest('.btn-quitar');
        if (b) b.closest('li').remove();
    });

    enlazarBusqueda('dni', 'btnBuscarCliente', buscarCliente);
    enlazarBusqueda('nuevoTitular', 'btnAgregarTitular', agregarTitular);
    tipo.addEventListener('change', actualizarTipo);
    actualizarTipo();
    if (dni.value.trim()) buscarCliente();
});
