import Helpers from "./helpers.js";

export default class Aviones {
  static #action;

  static async init() {
    await Aviones.#loadAll();
    document.querySelector("#aviones-aceptar").addEventListener("click", Aviones.#crud);
    document.querySelector("#aviones-cerrar").addEventListener("click", Aviones.#close);


    Aviones.#populateSeats('#ejecutivas', 4, 40, 4);
    Aviones.#populateSeats('#economicas', 6, 240, 6);

    return this;
  }

  static #populateSeats(
    selector,
    min,
    max,
    incremento,
  ) {
    let list = document.querySelector(selector);
    list.options.length = 0;
    for (let i = min; i <= max; i += incremento) {
      list.add(new Option(i, i))
    }

    return list;
  };

  static async #loadAll() {
    const infoTable = document.querySelector("#info-aviones");
    try {
      const response = await Helpers.fetchData("http://localhost:4567/sillas/total");
      if (response.message === "ok") {
        let rows = "";

        response.data.forEach((avion) => {
          rows += `
        <tr>
            <td>${avion.matricula}</td>
            <td>${avion.modelo}</td>
            <td class="center-table">${avion.totalSillas.ejecutivas}</td>
            <td class="center-table">${avion.totalSillas.economicas}</td>
        </tr>`;
        });

        const htmlTable = `
    <table class="pure-table pure-table-striped">
        <thead>
            <tr>
                <th>MATRICULA</th>
                <th>MODELO</th>
                <th class="center-table">EJECUTIVAS</th>
                <th class="center-table">ECONOMICAS</th>

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
        "No hay acceso al recurso de aviones",
        "danger",
        e
      );
    }
  }

  static async #crud() {
    switch (Aviones.#action) {
      case "create":
        await Aviones.create();
        break;
      case "retrieve":
        await Aviones.retrieve();
        break;
      case "update":
        await Aviones.update();
        break;
      case "delete":
        await Aviones.delete();
    }
  }

  static async create() {
    if (!Helpers.okForm("#form-aviones")) {
      return;
    }
    let matricula = document.querySelector("#matricula").value.trim()

    try {
      let response = await Helpers.fetchData(
        `http://localhost:4567/aviones`,
        {
          method: "POST",
          body: {
            matricula,
            modelo: document.querySelector("#modelo").value.trim(),
          },
        }
      );

      if (response.message === "ok") {

        await Aviones.#createSeats(matricula);
        await Aviones.#loadAll();
        document
          .querySelector('ul[id^="list-crud"]')
          .classList.remove("disabled");
        await Helpers.notice("#form-aviones #state", "Registro creado");
        Helpers.toggle("#frm-edit-aviones", "show", "hide");
      } else {
        Helpers.notice(
          "#form-aviones #state",
          response.message,
          "danger",
          response
        );
      }

    } catch (e) {
      Helpers.notice(
        "#form-aviones #state",
        "Sin acceso a la creación de registros",
        "danger",
        e
      );
    }
  }

  static async #createSeats(matricula) {

    try {
      let response = await Helpers.fetchData(
        `http://localhost:4567/sillas`,
        {
          method: "POST",
          body: {
            avion: matricula,
            ejecutivas: document.querySelector("#ejecutivas").value.trim(),
            economicas: document.querySelector("#economicas").value.trim(),
          },
        }
      );

      if (response.message === "ok") {

        await Helpers.notice("#form-aviones #state", "Registro creado");
        Helpers.toggle("#frm-edit-aviones", "show", "hide");
      } else {
        Helpers.notice(
          "#form-aviones #state",
          response.message,
          "danger",
          response
        );
      }
    } catch (e) {
      Helpers.notice(
        "Sin acceso a la creación de registros",
        "danger",
        e
      );
    }
  }

  static async retrieve() {
    const matricula = document.querySelector('#matricula').value.trim()
    try {
      let response = await Helpers.fetchData(`http://localhost:4567/sillas/avion/${matricula}`)

      if (response.message === 'ok') {
        document.querySelector('#modelo').value = response.data.modelo
        document.querySelector('#ejecutivas').value = response.data.totalSillas.ejecutivas
        document.querySelector('#economicas').value = response.data.totalSillas.economicas
        Helpers.collapse("#form-aviones > ul li", false, 1, 1);
      } else {
        Helpers.collapse("#form-aviones > ul li", true, 1, 2);
        await Helpers.notice(
          '#form-aviones #state', response.message, 'warning', response
        )
      }
    } catch (e) {
      await Helpers.notice(
        '#form-aviones #state', 'Error al intentar buscar', 'danger', e
      )
    }

  }

  static async update() {
    Helpers.collapse("#form-aviones > ul li", true, 2, 3);
    if (!Helpers.okForm('#form-aviones')) {
      return
    }
    Helpers.collapse("#form-aviones > ul li", true, 2, 3);

    try {
      Helpers.collapse("#form-aviones > ul li", true, 2, 3);

      const matricula = document.querySelector('#matricula').value.trim()

      let response = await Helpers.fetchData(
        `http://localhost:4567/aviones/${matricula}`, {
        method: 'PUT',
        body: {
          matricula,
          modelo: document.querySelector('#modelo').value.trim(),
        }
      })
      if (response.message === 'ok') {
        await Aviones.#loadAll()
        document.querySelector('ul[id^="list-crud"]').classList.remove('disabled')
        await Helpers.notice('#form-aviones #state', 'Registro actualizado')
        Helpers.toggle('#frm-edit-aviones', 'show', 'hide')
      } else {
        Helpers.notice('#form-aviones #state', response.message, 'danger', response)
      }

    } catch (exception) {
      Helpers.notice(
        '#form-aviones #state',
        'Sin acceso a la actualización de registros de aviones',
        'danger',
        exception
      )
    }
  }

  static async delete() {
    const matricula = document.querySelector('#matricula').value.trim()
      try {
        const url = `http://localhost:4567/aviones/${matricula}`
        const response = await Helpers.fetchData(url, { method: 'DELETE' })
        if (response.message === 'ok') {
          Helpers.toggle('#frm-edit-aviones', 'show', 'hide')
          await Helpers.notice('#form-aviones #state', 'vuelo eliminado')
          await Aviones.#loadAll()
        } else {
          Helpers.notice(
            '#form-aviones #state', response.message, 'danger', response
          )
          Helpers.collapse("#form-aviones > ul li", true, 1, 2);
        }
      } catch (e) {
        Helpers.notice(
          '#form-aviones #state', 'Error al intentar eliminar', 'danger', e
        )
      }
    
  }
  static async #close() {
    document.querySelector('ul[id^="list-crud"]').classList.remove("disabled");
    Helpers.toggle("#frm-edit-aviones", "show", "hide");
    Helpers.collapse("#form-aviones > ul li", false, 1, 2);
  }

  static action(_action) {
    if ("retrieve|delete".includes(_action)) {
      Helpers.collapse("#form-aviones > ul li", true, 1, 2);
    }
    const matricula = document.querySelector('#matricula').value.trim()
    if (_action === 'update' && matricula === '') {
      Helpers.notice('#list-crud-aviones', 'Busque primero el avion  a actualizar')
      return
    }
    if (_action === 'update') {
      Helpers.collapse("#form-aviones > ul li", true, 2, 2);
    }
    Helpers.toggle("#frm-edit-aviones", "hide", "show");
    document.querySelector('ul[id^="list-crud"]').classList.add("disabled");
    Aviones.#action = _action;
  }
}
