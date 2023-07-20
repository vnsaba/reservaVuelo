import Helpers from "./helpers.js";
import { DateTime, Duration } from "./luxon.min.js";

export default class Reservas {
    static #action;
    static #count

    static async init() {
        await Reservas.#loadAll();//se carga la lista de reservas 
        document.querySelector("#reservas-aceptar").addEventListener("click", Reservas.#crud);
        document.querySelector("#reservas-cerrar").addEventListener("click", Reservas.#close);
        document.querySelector('#ida-regreso').addEventListener('change', Reservas.#regreso)
        document.querySelector('#total-pasajeros').addEventListener('change', Reservas.#entradasPasajeros)
        Helpers.collapse('#form-reservas > ul li', true, 2, 3)//como se va a colapsar se envia un true, para el boton d eidea y regreso , para que solo muestre la ida
        Reservas.#count = 1 //aparezca predeterminada un pasajero
        Reservas.#ingresoPasajeros(Reservas.#count)
        await Reservas.#cargarVuelos()
        await Reservas.#cargarPasajero()
        return this;
    }

    /**
     * Crea una tabla con todos los registros del archivo
     */
    static async #loadAll() {
        const infoTable = document.querySelector("#info-reservas");
        try {
            const response = await Helpers.fetchData(
                "http://localhost:4567/vuelos-reservas"
            );
            if (response.message === "ok") {
                let rows = "";

                response.data.forEach((vr) => {
                    rows += `
            <div>
                <div>${vr.reserva.pasajero.identificacion} - ${vr.reserva.pasajero.nombres} ${vr.reserva.pasajero.apellidos} 
                ${vr.reserva.cancelado ? "-reserva cancelada" : ""}
                </div>
                <table class="pure-table pure-table-striped">
                
                <tbody>
                <tr>
                    <td>${DateTime.fromISO(vr.vuelo.fechaHora).toFormat(
                        "yyyy-MM-dd hh:mm a")}</td>
                        <td>${vr.vuelo.trayecto.origen} - ${vr.vuelo.trayecto.destino}</td>
                        <td>${vr.vuelo.avion.matricula}</td>
                        <td>${vr.silla.posicion}</td>
                        <td>${Helpers.format(vr.vuelo.trayecto.costo)}</td>
                        </tr>
                </tbody>
                </table> <br>
            </div>`;
                });

                const htmlTable = rows
                infoTable.innerHTML = htmlTable;
            } else {
                Helpers.notice(
                    infoTable,
                    "El servidor no pudo responder a la petición",
                    "danger",
                    response
                );
            }
        } catch (e) {
            Helpers.notice(
                infoTable,
                "No hay acceso al recurso de reservas",
                "danger",
                e
            );
        }
    }

    static async #crud() {
        switch (Reservas.#action) {
            case "create":
                await Reservas.create();
                break;
            case "retrieve":
                await Reservas.retrieve();
                break;
            case "update":
                await Reservas.update();
                break;
            case "delete":
                await Reservas.delete();
        }
    }

    /**
     * Envía al servidor una solicitud para crear un registro con los datos del formulario
     */
    static async create() {

    }


    /**
     * Enviar al servidor una solicitud de búsqueda de registro y si lo encuentra, mostrar los datos
     */
    static async retrieve() {


    }

    /**
     * Envia al servidor una solicitud de actualización con los datos del formulario.
     * Por razones de integridad referencial, no se permite cambiar la identificación
     */
    static async update() {
        Helpers.notice(
            "#form-reservas > ul #state",
            "reservas.update() pendiente de implementar"
        );
    }

    /**
     * Envía al servidor una solicitud de eliminación de eliminación de un registro
     */
    static async delete() {
        Helpers.notice(
            "#form-reservas > ul #state",
            "reservas.delete() pendiente de implementar"
        );
    }

    static async #close() {
        // habilitar las opciones del CRUD
        document.querySelector('ul[id^="list-crud"]').classList.remove("disabled");
        // oculta el formulario de entrada de datos
        Helpers.toggle("#frm-edit-reservas", "show", "hide");
        // por sospecha quita el posible colapsado de elementos de lista
        Helpers.collapse("#form-reservas > ul li", false, 1, 2);
    }

    static action(_action) {
        if ("retrieve|delete".includes(_action)) {
            // para retrieve y delete sólo mostrar la entrada de ID
            Helpers.collapse("#form-reservas > ul li", true, 1, 2);
        }
        // mostrar el formulario de entrada de datos
        Helpers.toggle("#frm-edit-reservas", "hide", "show");
        // deshabilitar las opciones del CRUD
        document.querySelector('ul[id^="list-crud"]').classList.add("disabled");
        Reservas.#action = _action;
    }

    static #regreso(e) {
        Helpers.collapse('#form-reservas > ul li', !e.target.checked, 2, 2)
        const regresos = document.querySelectorAll('#form-reservas > ul> li div[id ^="divregreso"]')
        regresos.forEach(div => div.hidden = e.target.checked ? false : true)
    }
    static #entradasPasajeros(e) {
        const contador = e.target.value //lee cuantos pasajeros van a entrar
        console.log(contador.data)
        if (contador > Reservas.#count) {
            const total = contador - Reservas.#count
            for (let i = 0; i < total; i++) {//si se añade 4 pasajeros el añada 4 casillas para gregar esos pasajeros 
                Reservas.#ingresoPasajeros(contador)//esta funcion hace eso 
            }
            Reservas.#count = contador
        }
        if (contador < Reservas.#count) {
            let lista = document.querySelectorAll('#form-reservas > ul > li')
            const total = Reservas.#count - contador
            for (let i = 0; i < total; i++) {
                lista[lista.length - 4].remove();//remover lo que ya estaba

            }
            Reservas.#count -= total
        }
    }
    static #ingresoPasajeros(contador) {
        const regreso = document.querySelector('#ida-regreso').checked //verifica que este checkiado
        const htmlRegreso = regreso ? "" : 'hidden'
        const html = `<li id="item-${contador}">
    <div class="field-divided3">
        <div class="">
            Pasajeros
            <select id="pasajero-${contador}" name="pasajero-${contador}" class="field-select">
            <option value="y">Elija el pasajero</option>
            </select>
        </div>
        &nbsp;&nbsp;
        <div class="">
            Ida
            <select id="ida-${contador}" name="ida-${contador}" class="field-select">
            <option value="y">01A</option>
            </select>
        </div>
        &nbsp;&nbsp;
        <div id="divregreso-${contador}" class="" ${htmlRegreso}>
            Regreso
            <select id="regreso-${contador}" name="regreso-${contador}" class="field-select">
                <option value="y">02B</option>
            </select>
        </div>
    </div>
</li>`

        //añadir evento a la casillas de sillas ida y regreso
        document.querySelector('#executive-options').insertAdjacentHTML('beforebegin', html)// se injecta el html
        document.querySelector(`#ida-${contador}`).addEventListener('mouseover', Reservas.#sillas)// es la silla  de ida 
        document.querySelector(`#ida-${contador}`).addEventListener('mouseout', Reservas.#sillas) //añadir los eventos a los botones
        document.querySelector(`#regreso-${contador}`).addEventListener('mouseover', Reservas.#sillas)
        document.querySelector(`#regreso-${contador}`).addEventListener('mouseout', Reservas.#sillas)

        //falta completar
    }
    static #sillas(e) {
        //falta verificar el tipo de la silla
        if (e.type === 'mouseover') {
            document.querySelector('#opciones-menu').disabled = false //disabled es desactivado y al decirle false es que no lo esta
            document.querySelector('#opciones-licor').disabled = false
        }
        if (e.type === 'mouseout') {
            document.querySelector('#opciones-menu').disabled = true
            document.querySelector('#opciones-licor').disabled = true
        }
    }
    //``  cuando se va a poner algo de valor
    static async #cargarVuelos() {
        try {
            const vuelos = await Helpers.fetchData(`http://localhost:4567/vuelos`)//espera q ue todo cargue para poder seguir await
            //recorrer el vuelo para mostrarlo
            vuelos.data.forEach(vuelo => {
                const fechaHora = DateTime.fromISO(vuelo.fechaHora).toFormat("yyyy-MM-dd hh:mm a")
                const costo = Helpers.format(vuelo.trayecto.costo)
                vuelo.formato = `${vuelo.trayecto.origen}-${vuelo.trayecto.destino}, ${fechaHora}, $${costo}`
            })//hasta aca  bien

            const vuelosIda = Helpers.populateSelectList("#vuelo-ida", vuelos.data, "formato", "formato")//llenar el select de ida de  vuelos
            vuelosIda.addEventListener('change', async (e) => {
                await Reservas.#vueloRegreso()
                await Reservas.#sillaIda()
            });
        } catch (error) {
            console.log(error)
        }
    }
    static async #vueloRegreso() {

        //SEGUIR REVISANDO
        const indice = document.querySelector('#vuelo-ida').selectedIndex
        const vuelos = await Helpers.fetchData(`http://localhost:4567/vuelos`)//espera q ue todo cargue para poder seguir await
        const vuelo = vuelos.data[indice]//se obtiene el vuelo

        const url = `http://localhost:4567/vuelos/vueloIda/origen=${vuelo.trayecto.destino}&destino=${vuelo.trayecto.origen}`
        const vuelosRegreso = await Helpers.fetchData(url)
        console.log(vuelosRegreso)
      
        
        vuelosRegreso.data.forEach(vuelo => {
            const fechaHora = DateTime.fromISO(vuelo.fechaHora).toFormat("yyyy-MM-dd hh:mm a")
            const costo = Helpers.format(vuelo.trayecto.costo)
            vuelo.formato = `${vuelo.trayecto.origen}-${vuelo.trayecto.destino}, ${fechaHora}, $${costo}`
        })


        // const fechaHora = DateTime.fromISO(vuelo.fechaHora).toFormat("yyyy-MM-dd hh:mm a")
        // const costo = Helpers.format(vuelo.trayecto.costo)
        // vueloRegreso.data.formato = `${vuelos.trayecto.origen}-${vuelo.trayecto.destino}, ${fechaHora}, $${costo}`
        const vueloRegresos = Helpers.populateSelectList("#vuelo-regreso", vuelosRegreso.data, "formato", "formato")
        vueloRegresos.addEventListener("change", async () => {
            await Reservas.#sillasRegreso()
        })
    }
    //crear una funcion para cargar las sillas segun ese vuelo, resiva el avion del vuelo y busque las isllas de ese vuelo 

    static async #sillasRegreso() {
        const indice = document.querySelector('#vuelo-regreso').selectedIndex
        const vuelos = await Helpers.fetchData(`http://localhost:4567/vuelos`)//espera q ue todo cargue para poder seguir await
        const vuelo = vuelos.data[indice]//se obtiene el vuelo

        //
    }

    static async #sillaIda() {//implementar, RETORNAR UN JSON CON TODAS ESAS SILLAS DISPONIBLES EN ESE VUELO
        const indice = document.querySelector('#vuelo-ida').selectedIndex
        const vuelos = await Helpers.fetchData(`http://localhost:4567/vuelos`)//espera q ue todo cargue para poder seguir await
        const vuelo = vuelos.data[indice]//se obtiene el vuelo

        //
    }
    static async #cargarPasajero() {
        try {
            const pasajeros = await Helpers.fetchData(`http://localhost:4567/pasajeros`)//espera q ue todo cargue para poder seguir await
            //recorrer el vuelo para mostrarlo
            pasajeros.data.forEach(pasajero => {
                pasajero.formato = `${pasajero.identificacion}-${pasajero.nombres}, ${pasajero.apellidos}`
            })
            for (let i = 0; i < count; i++) {
                const pasajeroLista = Helpers.populateSelectList( '#pasajero-{i}', pasajeros.data, "formato", "formato")
                //segun el pasajero que se escoja se añaden  mas partes                
            }
            
        } catch (error) {
            console.log(error)
        }
    }

}
