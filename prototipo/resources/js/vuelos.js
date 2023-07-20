import Helpers from "./helpers.js";
import { DateTime, Duration } from "./luxon.min.js";

export default class Vuelos {
  static #action;
  static #trayectos;
  static #listTrayectos;
  static #aviones;
  static #listAviones;

  static async init() {
    await Vuelos.#loadAll();
    document.querySelector("#vuelos-aceptar").addEventListener("click", Vuelos.#crud);
    document.querySelector("#vuelos-cerrar").addEventListener("click", Vuelos.#close);

    this.#trayectos = await Helpers.fetchData("http://localhost:4567/trayectos");
    this.#trayectos.data.forEach(t => t.toString = `${t.origen} - ${t.destino}`);

    this.#listTrayectos = Helpers.populateSelectList(
      '#trayectos', this.#trayectos.data, 'toString', 'toString'
    );

    this.#aviones = await Helpers.fetchData("http://localhost:4567/aviones");
    this.#aviones.data.forEach(v => v.toString = ` ${v.matricula}-${v.modelo}`)
    this.#listAviones = Helpers.populateSelectList(
      '#aviones', this.#aviones.data, 'matricula', 'toString'
    )

    return this;
  }

  static async #loadAll() {
    const infoTable = document.querySelector("#info-vuelos");
    try {
      const response = await Helpers.fetchData("http://localhost:4567/vuelos");
      if (response.message === "ok") {
        let rows = "";

        response.data.forEach((vuelo) => {
          rows += `
        <tr>
            <td>${DateTime.fromISO(vuelo.fechaHora).toFormat(
            "yyyy-MM-dd hh:mm a"
          )}<span/></td>
            <td>${vuelo.trayecto.origen}</td>
            <td>${vuelo.trayecto.destino}</td>
            <td  class="center-table">  ${Duration.fromISO(
            vuelo.trayecto.duracion
          ).toFormat("hh:mm")}</td>
            <td  class="center-table">${Helpers.format(
            vuelo.trayecto.costo
          )}</td>
            <td>${vuelo.avion.matricula}</td>
            <td  class="center-table">${vuelo.cancelado2}</td>


        </tr>`;
        });
        const htmlTable = `
    <table class="pure-table pure-table-striped">
        <thead>
            <tr>
                <th >FECHA-HORA</th>
                <th>ORIGEN</th>
                <th>DESTINO</th>
                <th>DURACION</th>
                <th>COSTO</th>
                <th>AVION</th>
                <th>CANCELADO</th>
            </tr>
        </thead>
        <tbody>
            ${rows}
        </tbody>
    </table>`;

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
        "No hay acceso al recurso de vuelos",
        "danger",
        e
      );
    }
  }

  static async #crud() {
    switch (Vuelos.#action) {
      case "create":
        await Vuelos.create();
        break;
      case "retrieve":
        await Vuelos.retrieve();
        break;
      case "update":
        await Vuelos.update();
        break;
      case "delete":
        await Vuelos.delete();
    }
  }

  static async create() {
    if (!Helpers.okForm("#form-vuelos")) {
      return;
    }
    const trayecto = this.#trayectos.data[this.#listTrayectos.selectedIndex];
    try {
      let response = await Helpers.fetchData(
        `http://localhost:4567/vuelos`,
        {
          method: "POST",
          body: {
            fechaHora: document.querySelector('#fecha-hora').value,
            origen: trayecto.origen,
            destino: trayecto.destino,
            avion: this.#listAviones.value,
            cancelado: document.querySelector('#cancelado').checked
          },
        }
      );
      if (response.message === "ok") {
        await Vuelos.#loadAll();
        document.querySelector('ul[id^="list-crud"]').classList.remove("disabled");
        await Helpers.notice("#form-vuelos #state", "Registro creado");
        Helpers.toggle("#frm-edit-vuelos", "show", "hide");
      } else {
        Helpers.notice(
          "#form-vuelos #state",
          response.message,
          "danger",
          response
        );
      }
    } catch (e) {
      Helpers.notice(
        "#form-vuelos #state",
        "Sin acceso a la creación de registros",
        "danger",
        e
      );
    }
  }


  static async retrieve() {
    const fecha = document.querySelector('#fecha-hora').value.trim()
    const trayecto = this.#trayectos.data[this.#listTrayectos.selectedIndex];
    try {
      let response = await Helpers.fetchData(
        `http://localhost:4567/vuelos/fechaHora=${fecha}&origen=${trayecto.origen}&destino=${trayecto.destino}&avion=${this.#listAviones.value}`
      )

      if (response.message === 'ok') {

        document.querySelector('#cancelado').value = response.data.cancelado
        Helpers.collapse("#form-vuelos > ul li", false, 3, 4);
      } else {
        Helpers.collapse("#form-vuelos > ul li", true, 3, 4);
        await Helpers.notice(
          '#form-vuelos #state', response.message, 'warning', response
        )
      }
    } catch (e) {
      await Helpers.notice(
        '#form-vuelos #state', 'Error al intentar buscar', 'danger', e
      )
    }
  }


  static async update() {
    if (!Helpers.okForm('#form-vuelos')) {
      return
    }

    const trayecto = this.#trayectos.data[this.#listTrayectos.selectedIndex];

    let response = await Helpers.fetchData(`http://localhost:4567/vuelos`, {
      method: 'PUT',
      body: {
        fechaHora: document.querySelector('#fecha-hora').value,
        origen: trayecto.origen,
        destino: trayecto.destino,
        avion: this.#listAviones.value,
        cancelado: document.querySelector('#cancelado').checked
      }
    })

    if (response.message === 'ok') {
      await Vuelos.#loadAll()
      document.querySelector('ul[id^="list-crud"]').classList.remove('disabled')
      await Helpers.notice('#form-vuelos #state', 'Registro actualizado')
      Helpers.toggle('#frm-edit-vuelos', 'show', 'hide')
    } else {
      Helpers.notice('#form-vuelos #state', response.message, 'danger', response)
    }
  } catch(exception) {
    Helpers.notice(
      '#form-vuelos #state',
      'Sin acceso a la creación de registros',
      'danger',
      exception)

  }


  static async delete() {
    const fecha = document.querySelector('#fecha-hora').value.trim()
    const trayecto = this.#trayectos.data[this.#listTrayectos.selectedIndex];
    try {
      const url = `http://localhost:4567/vuelos/fechaHora=${fecha}&origen=${trayecto.origen}&destino=${trayecto.destino}&avion=${this.#listAviones.value}`
      const response = await Helpers.fetchData(url, {
        method: 'DELETE'
      })
      if (response.message === 'ok') {
        Helpers.toggle('#frm-edit-vuelos', 'show', 'hide')
        await Helpers.notice('#form-vuelos #state', 'vuelo eliminado')
        await Vuelos.#loadAll()
      } else {
        Helpers.notice(
          '#form-vuelos #state', response.message, 'danger', response
        )
        Helpers.collapse("#form-vuelos > ul li", true, 1, 2);
      }
    } catch (e) {
      Helpers.notice(
        '#form-vuelos #state', 'Error al intentar eliminar', 'danger', e
      )
    }
  }

  static async #close() {
    document.querySelector('ul[id^="list-crud"]').classList.remove("disabled");
    Helpers.toggle("#frm-edit-vuelos", "show", "hide");
    Helpers.collapse("#form-vuelos > ul li", false, 1, 2);
  }

  static action(_action) {
    if ("retrieve|delete".includes(_action)) {
      Helpers.collapse("#form-vuelos > ul li", true, 3, 3);
    }
    Helpers.toggle("#frm-edit-vuelos", "hide", "show");
    document.querySelector('ul[id^="list-crud"]').classList.add("disabled");
    Vuelos.#action = _action;
  }
}
