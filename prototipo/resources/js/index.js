// basado en https://purecss.io/layouts/side-menu/
import Helpers from "./helpers.js";
//Compruebe en la consola del navegador que no se presentan errores
// y si es así debe verificar que el archivo helpers.js se haya guardado en la carpeta correcta
let current; // referencia la clase activa: Pasajeros | Trayectos | ...

document.addEventListener("DOMContentLoaded", main);

function handleEvent(e) {
  console.log(`Pulsó clic sobre un <${e.target.tagName}>`);
}

function main() {
  document.addEventListener("click", handleEvent);

  /**
   * Gestor de eventos para todos los elementos de la página
   * @param {Event} e El evento ocurrido
   */
  function handleEvent(e) {
    let elements = getElements();

    if (e.target.tagName.toLowerCase() == "a") {
      // si se pulsa sobre un anchor
      handleMenu(e.target);
    }

    if (e.target.id === elements.menuLink.id) {
      // se pulsa sobre el icono del menú si está visible
      toggleAll();
      e.preventDefault();
    } else if (elements.menu.className.indexOf("active") !== -1) {
      // si el menú está colapsado se despliega
      toggleAll();
    }
  }

  /**
   * Gestión de las opciones según el enlace pulsado
   * @param {*} anchor
   */
  async function handleMenu(anchor) {
    const option = anchor.getAttribute("href");

    // verificar si la opción elegida es una opción CRUD
    let regex = new RegExp("create|retrieve|update|delete");
    const index = option.search(regex);

    // si es una opción CRUD y hay una instancia de pasajeros, trayectos, ...
    if (index > -1) {
      if (typeof current !== "undefined") {
        // extraer del nombre de la opción, el nombre de la acción y ejecutarla
        const action = option.substring(index);
        current.action(action);
      } else {
        console.error(
          `Error en ${option}. No hay un objeto asignado a current.`
        );
      }
    } else {
      switch (option) {
        case "#pasajeros":
          await Helpers.loadPage(
            "./resources/html/pasajeros.html",
            "#layout > main > section"
          );
          const { default: Pasajeros } = await import(`./pasajeros.js`);
          current = await Pasajeros.init();
          break;
        case "#aviones":
          await Helpers.loadPage(
            "./resources/html/aviones.html",
            "#layout > main > section"
          );
          const { default: Aviones } = await import(`./aviones.js`);
          current = await Aviones.init();
          break;
        case "#reservas":
          await Helpers.loadPage(
            "./resources/html/reservas.html",
            "#layout > main > section"
          );
          const { default: Reservas } = await import(`./reservas.js`);
          current = await Reservas.init();
          break;
        case "#vuelos":
          await Helpers.loadPage(
            "./resources/html/vuelos.html",
            "#layout > main > section"
          );
          const { default: Vuelos } = await import(`./vuelos.js`);
          current = await Vuelos.init();
          break;
        case "#trayectos":
          await Helpers.loadPage(
            "./resources/html/trayectos.html",
            "#layout > main > section"
          );
          const { default: Trayectos } = await import(`./trayectos.js`);
          current = await Trayectos.init();
          break;
        // ... continuar con otras opciones del menú ...

        case "#contacto":
        // ...
        default:
          console.warn(`opción "${option}" no implementada`);
      }
    }
  }

  /**
   * Busca los elementos cada vez porque se elimina la anterior
   * referencia de elementos en la navegación de la página
   * @returns Un objeto con los elementos
   */
  function getElements() {
    return {
      layout: document.querySelector("#layout"),
      menu: document.querySelector("#menu"),
      menuLink: document.querySelector("#menuLink"),
    };
  }

  /**
   * Colapsa o despliega un elemento
   * @param {*} element El elemento que conmuta a colapsado/desplegado
   * @param {*} className El nombre de la clase que provoca la conmutación
   */
  function toggleClass(element, className) {
    let classes = element.className.split(/\s+/);
    let length = classes.length;

    for (let i = 0; i < length; i++) {
      // si la clase se encuentra se quita
      if (classes[i] === className) {
        classes.splice(i, 1);
        break;
      }
    }

    if (length === classes.length) {
      // si la clase no se encuentra se agrega
      classes.push(className);
    }

    element.className = classes.join(" ");
  }

  /**
   * Colapsa o despliega elementos de la página
   */
  function toggleAll() {
    const active = "active";
    const elements = getElements();

    toggleClass(elements.layout, active);
    toggleClass(elements.menu, active);
    toggleClass(elements.menuLink, active);
  }
}
