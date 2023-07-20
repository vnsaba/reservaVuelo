import Helpers from "./helpers.js";
import { DateTime, Duration } from "./luxon.min.js";

export default class Trayectos {
  static #action;

  static async init() {
    await Trayectos.#loadAll();
    document.querySelector("#trayectos-aceptar").addEventListener("click", Trayectos.#crud);
    document.querySelector("#trayectos-cerrar").addEventListener("click", Trayectos.#close);
    let airports = await Helpers.fetchData("./data/aeropuertos.json");
    airports = airports.map((item) => ({ airport: item }));

    const selectOrigen = Helpers.populateSelectList(
      "#origen",
      airports,
      "airport",
      "airport"
    );
    selectOrigen.addEventListener("change", (e) => {
      const airports2 = airports.filter((el) => el.airport != e.target.value);
      Helpers.populateSelectList("#destino", airports2, "airport", "airport");
    });
    selectOrigen.dispatchEvent(new Event("change"));
    return this;
  }

  static async #loadAll() {
    const infoTable = document.querySelector("#info-trayectos");
    try {
      const response = await Helpers.fetchData(
        "http://localhost:4567/trayectos"
      );
      if (response.message === "ok") {
        let rows = "";

        response.data.forEach((trayecto) => {
          rows += `
        <tr>
            <td>${trayecto.origen}</td>
            <td>${trayecto.destino}</td>
            <td class="center-table">${Duration.fromISO(trayecto.duracion).toFormat("hh:mm")}</td>
            <td class="center-table">${Helpers.format(trayecto.costo)}</td>
        </tr>`;
        });

        const htmlTable = `
    <table class="pure-table pure-table-striped">
        <thead>
            <tr>
                <th>ORIGEN</th>
                <th>DESTINO</th>
                <th class="center-table">DURACION</th>
                <th class="center-table">COSTO</th>
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
        "No hay acceso al recurso de trayectos",
        "danger",
        e
      );
    }
  }

  static async #crud() {
    switch (Trayectos.#action) {
      case "create":
        await Trayectos.create();
        break;
      case "retrieve":
        await Trayectos.retrieve();
        break;
      case "update":
        await Trayectos.update();
        break;
      case "delete":
        await Trayectos.delete();
    }
  }

  static async create() {
    if (!Helpers.okForm("#form-trayectos")) {
      return;
    }
    let duracion = document.querySelector("#duracion").value;
    duracion = DateTime.fromISO(duracion).toFormat("'PT'HH'H'mm'M'");

    try {
      let response = await Helpers.fetchData(
        `http://localhost:4567/trayectos`,
        {
          method: "POST",
          body: {
            origen: document.querySelector("#origen").value.trim(),
            destino: document.querySelector("#destino").value.trim(),
            duracion,
            costo: document.querySelector("#costo").value,
          },
        }
      );

      if (response.message === "ok") {
        await Trayectos.#loadAll();
        document
          .querySelector('ul[id^="list-crud"]')
          .classList.remove("disabled");
        await Helpers.notice("#form-trayectos #state", "Registro creado");
        Helpers.toggle("#frm-edit-trayectos", "show", "hide");
      } else {
        Helpers.notice(
          "#form-trayectos #state",
          response.message,
          "danger",
          response
        );
      }
    } catch (e) {
      Helpers.notice(
        "#form-trayectos #state",
        "Sin acceso a la creación de registros",
        "danger",
        e
      );
    }
  }

  static async retrieve() {

    const origen = document.querySelector('#origen').value.trim()
    const destino = document.querySelector('#destino').value.trim()
    try {
      let response = await Helpers.fetchData(
        `http://localhost:4567/trayectos/origen=${origen}&destino=${destino}`
      )

      if (response.message === 'ok') {
        const duracion = Duration.fromISO(response.data.duracion);
        document.querySelector('#duracion').value = duracion.toFormat('hh:mm')
        document.querySelector('#costo').value = response.data.costo
        Helpers.collapse("#form-trayectos > ul li", false, 1, 2);
      } else {
        Helpers.collapse("#form-trayectos > ul li", true, 1, 2);
        await Helpers.notice(
          '#form-trayectos #state', response.message, 'warning', response
        )
      }
    } catch (e) {
      await Helpers.notice(
        '#form-trayectos #state', 'Error al intentar buscar', 'danger', e
      )
    }
  }

  static async update() {
    if (!Helpers.okForm('#form-trayectos')) {
      return
    }

    let duracion = document.querySelector('#duracion').value
    duracion = DateTime.fromISO(duracion).toFormat("'PT'HH'H'mm'M'")

    try {
      let response = await Helpers.fetchData(`http://localhost:4567/trayectos`, {
        method: 'PUT',
        body: {
          origen: document.querySelector('#origen').value.trim(),
          destino: document.querySelector('#destino').value.trim(),
          duracion,
          costo: document.querySelector('#costo').value
        }
      })

      if (response.message === 'ok') {
        await Trayectos.#loadAll()
        document.querySelector('ul[id^="list-crud"]').classList.remove('disabled')
        await Helpers.notice('#form-trayectos #state', 'Registro actualizado')
        Helpers.toggle('#frm-edit-trayectos', 'show', 'hide')
      } else {
        Helpers.notice('#form-trayectos #state', response.message, 'danger', response)
      }
    } catch (exception) {
      Helpers.notice(
        '#form-trayectos #state',
        'Sin acceso a la creación de registros',
        'danger',
        exception)
    }
  }


  static async delete() {
    const origen = document.querySelector('#origen').value.trim()
    const destino = document.querySelector('#destino').value.trim()

    try {
      const url = `http://localhost:4567/trayectos/origen=${origen}&destino=${destino}`
      const response = await Helpers.fetchData(url, {
        method: 'DELETE'
      })

      if (response.message === 'ok') {
        Helpers.toggle('#frm-edit-trayectos', 'show', 'hide')
        await Helpers.notice('#form-trayectos #state', 'trayecto eliminado')
        await Trayectos.#loadAll()
      } else {
        Helpers.notice(
          '#form-trayectos #state', response.message, 'danger', response
        )
        Helpers.collapse("#form-trayectos > ul li", true, 1, 2);
      }
    } catch (e) {
      Helpers.notice(
        '#form-trayectos #state', 'Error al intentar eliminar', 'danger', e
      )
    }
  }

  static async #close() {
    document.querySelector('ul[id^="list-crud"]').classList.remove("disabled");
    Helpers.toggle("#frm-edit-trayectos", "show", "hide");
    Helpers.collapse("#form-trayectos > ul li", false, 1, 2);
  }

  static action(_action) {
    if ("retrieve|delete".includes(_action)) {
      Helpers.collapse("#form-trayectos > ul li", true, 2, 3);
    }
    Helpers.toggle("#frm-edit-trayectos", "hide", "show");
    document.querySelector('ul[id^="list-crud"]').classList.add("disabled");
    Trayectos.#action = _action;
  }
}
