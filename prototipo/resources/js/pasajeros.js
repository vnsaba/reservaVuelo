import Helpers from "./helpers.js";

export default class Pasajeros {
  static #action;

  static async init() {
    await Pasajeros.#loadAll();
    document
      .querySelector("#pasajeros-aceptar")
      .addEventListener("click", Pasajeros.#crud);
    document
      .querySelector("#pasajeros-cerrar")
      .addEventListener("click", Pasajeros.#close);

    return this;
  }

  /**
   * Crea una tabla con todos los registros del archivo
   */
  static async #loadAll() {
    const infoTable = document.querySelector("#info-pasajeros");
    try {
      const response = await Helpers.fetchData(
        "http://localhost:4567/pasajeros"
      );
      if (response.message === "ok") {
        let rows = "";

        response.data.forEach((pasajero) => {
          rows += `
        <tr>
            <td>${pasajero.identificacion}</td>
            <td>${pasajero.nombres}</td>
            <td>${pasajero.apellidos}</td>
        </tr>`;
        });

        const htmlTable = `
    <table class="pure-table pure-table-striped">
        <thead>
            <tr>
                <th>ID</th>
                <th>NOMBRES</th>
                <th>APELLIDOS</th>
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
        "No hay acceso al recurso de pasajeros",
        "danger",
        e
      );
    }
  }

  static async #crud() {
    switch (Pasajeros.#action) {
      case "create":
        await Pasajeros.create();
        break;
      case "retrieve":
        await Pasajeros.retrieve();
        break;
      case "update":
        await Pasajeros.update();
        break;
      case "delete":
        await Pasajeros.delete();
    }
  }

  /**
   * Envía al servidor una solicitud para crear un registro con los datos del formulario
   */
  static async create() {
    if (!Helpers.okForm("#form-pasajeros")) {
      return;
    }

    try {
      let response = await Helpers.fetchData(
        `http://localhost:4567/pasajeros`,
        {
          method: "POST",
          body: {
            identificacion: document.querySelector("#identificacion").value.trim(),
            nombres: document.querySelector("#nombres").value.trim(),
            apellidos: document.querySelector("#apellidos").value,
          },
        }
      );

      if (response.message === "ok") {
        await Pasajeros.#loadAll();
        document
          .querySelector('ul[id^="list-crud"]')
          .classList.remove("disabled");
        await Helpers.notice("#form-pasajeros #state", "Registro creado");
        Helpers.toggle("#frm-edit-pasajeros", "show", "hide");
      } else {
        Helpers.notice(
          "#form-pasajeros #state",
          response.message,
          "danger",
          response
        );
      }
    } catch (e) {
      Helpers.notice(
        "#form-pasajeros #state",
        "Sin acceso a la creación de registros",
        "danger",
        e
      );
    }
  }


  /**
   * Enviar al servidor una solicitud de búsqueda de registro y si lo encuentra, mostrar los datos
   */
  static async retrieve() {
    const id = document.querySelector('#identificacion').value.trim()

    try {
      let response = await Helpers.fetchData(`http://localhost:4567/pasajeros/${id}`)

      if (response.message === 'ok') {
        document.querySelector('#nombres').value = response.data.nombres
        document.querySelector('#apellidos').value = response.data.apellidos
        Helpers.collapse("#form-pasajeros > ul li", false, 1, 2);
      } else {
        Helpers.collapse("#form-pasajeros > ul li", true, 1, 2);
        await Helpers.notice(
          '#form-pasajeros #state', response.message, 'warning', response
        )
      }
    } catch (e) {
      await Helpers.notice(
        '#form-pasajeros #state', 'Error al intentar buscar', 'danger', e
      )
    }

  }

  /**
   * Envia al servidor una solicitud de actualización con los datos del formulario.
   * Por razones de integridad referencial, no se permite cambiar la identificación
   */
  static async update() {

    if (!Helpers.okForm('#form-pasajeros')) {
      return
    }

    try {
      const identificacion = document.querySelector('#identificacion').value.trim()

      let response = await Helpers.fetchData(
        `http://localhost:4567/pasajeros/${identificacion}`, {
        method: 'PUT',
        body: {
          identificacion,
          nombres: document.querySelector('#nombres').value.trim(),
          apellidos: document.querySelector('#apellidos').value.trim()
        }
      })

      if (response.message === 'ok') {
        await Pasajeros.#loadAll()
        document.querySelector('ul[id^="list-crud"]').classList.remove('disabled')
        await Helpers.notice('#form-pasajeros #state', 'Registro actualizado')
        Helpers.toggle('#frm-edit-pasajeros', 'show', 'hide')
      } else {
        Helpers.notice('#form-pasajeros #state', response.message, 'danger', response)
      }

    } catch (exception) {
      Helpers.notice(
        '#form-pasajeros #state',
        'Sin acceso a la actualización de registros de pasajeros',
        'danger',
        exception
      )
    }


  }

  /**
   * Envía al servidor una solicitud de eliminación de eliminación de un registro
   */
  static async delete() {

    const identificacion = document.querySelector('#identificacion').value.trim()

    try {
        const url = `http://localhost:4567/pasajeros/${identificacion}`
        const response = await Helpers.fetchData(url, {
            method: 'DELETE'
        })
    
        if (response.message === 'ok') {
            Helpers.toggle('#frm-edit-pasajeros', 'show', 'hide')
            await Helpers.notice('#form-pasajeros #state', 'Registro eliminado')
            await Pasajeros.#loadAll()
        } else {
            Helpers.notice(
                '#form-pasajeros #state', response.message, 'danger', response
            )
            Helpers.collapse("#form-pasajeros > ul li", true, 1, 2);
        }
    } catch (e) {
        Helpers.notice(
           '#form-pasajeros #state', 'Error al intentar eliminar', 'danger', e
        )
    }


  }

  static async #close() {
    // habilitar las opciones del CRUD
    document.querySelector('ul[id^="list-crud"]').classList.remove("disabled");
    // oculta el formulario de entrada de datos
    Helpers.toggle("#frm-edit-pasajeros", "show", "hide");
    // por sospecha quita el posible colapsado de elementos de lista
    Helpers.collapse("#form-pasajeros > ul li", false, 1, 2);
  }

  static action(_action) {
    if ("retrieve|delete".includes(_action)) {
      // para retrieve y delete sólo mostrar la entrada de ID
      Helpers.collapse("#form-pasajeros > ul li", true, 1, 2);
    }


    
    // verificar si el pasajero a modificar ya ha sido buscado.
    const id = document.querySelector('#identificacion').value.tfrim()
    if (_action === 'update' && id === '') {
      Helpers.notice('#list-crud-pasajeros', 'Busque primero el pasajero a actualizar')
      return
    }

    
    
    // mostrar el formulario de entrada de datos
    Helpers.toggle("#frm-edit-pasajeros", "hide", "show");
    // deshabilitar las opciones del CRUD
    document.querySelector('ul[id^="list-crud"]').classList.add("disabled");
    Pasajeros.#action = _action;
  }
}
