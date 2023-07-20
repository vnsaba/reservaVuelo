export default class Helpers {
  static toast;

  /**
   * Genera un número entero aleatorio en un rango determinado
   * @param {int} min El intervalo inferior
   * @param {int} max El intervalo superior
   * @returns {int} Un valor aleatorio entero en un rango determinado
   */
  static random = (min, max) => {
    min = Math.ceil(min); // aproximar al entero superior
    max = Math.floor(max); // aproximar al tenero inferior
    return Math.floor(Math.random() * (max - min + 1)) + min;
  };

  /**
   * Permite conocer el elemento seleccionado de un conjunto de radio buttons
   * @param {String} selector Un selector CSS que permite seleccionar el grupo de radio buttons
   * @returns {String} Retorna el atributo value del radio button seleccionado
   */
  static selectedRadioButton = (selector) => {
    const radio = document.querySelector(selector + ":checked");
    return radio ? radio.value : radio;
  };

  static getItems = (selector) => {
    const items = document.querySelectorAll(selector);
    return [...items].map((item) => {
      // operador rest >  desestructuración
      return { value: item.value, checked: item.checked };
    });
  };

  static selectedItemList = (selector) => {
    const list = document.querySelector(selector);
    const item = list.options[list.selectedIndex];
    return {
      selectedIndex: list.selectedIndex,
      value: item.value,
      text: item.text,
    };
  };


  static populateSelectList = (
    selector,
    items = [],
    value = "",
    text = "",
    firstOption = ""
  ) => {
    let list = document.querySelector(selector);
    list.options.length = 0;
    if (firstOption) {
      list.add(new Option(firstOption, ""));
    }
    items.forEach((item) => list.add(new Option(item[text], item[value])));
    return list;
  };

 

  static htmlSelectList = ({
    id = "",
    cssClass = "",
    items = [],
    value = "",
    text = "",
    firstOption = "",
    required = true,
    disabled = false,
  }) => {
    required = required ? "required" : "";
    disabled = disabled ? "disabled" : "";
    let list = "";

    items.forEach((item) => {
      if (firstOption === item[text]) {
        list += `<option value="${item[value]}" selected>${item[text]}</option>`;
      } else {
        list += `<option value="${item[value]}">${item[text]}</option>`;
      }
    });

    return `<select id ="${id}" class="${cssClass}" ${required} ${disabled}>${list}</select>`;
  };

  static loadPage = async (url, container) => {
    try {
      const element = document.querySelector(container);
      if (!element) {
        throw new Error(`Parece que el selector '${container}' no es válido`);
      }

      const response = await fetch(url);
      // console.log(response);
      if (response.ok) {
        const html = await response.text();
        element.innerHTML = html;
        return element; // para permitir encadenamiento
      } else {
        throw new Error(
          `${response.status} - ${response.statusText}, al intentar acceder al recurso '${response.url}'`
        );
      }
    } catch (e) {
      console.log(e);
    }
  };

  static fetchData = async (url, data = {}) => {
    if (!("headers" in data)) {
      data.headers = {
        "Content-Type": "application/json; charset=utf-8",
      };
    }

    if ("body" in data) {
      data.body = JSON.stringify(data.body);
    }

    const respuesta = await fetch(url, data);
    return await respuesta.json();
  };

  /**
   * Intenta cargar una plantilla de calificación
   * @param {String} url
   * @param {Object} options
   */
  static async fetchText(url, options = {}) {
    let response = await fetch(url, options);
    if (response.ok) {
      return await response.text();
    }
    // throw new Error(`${response.status} - ${response.statusText}:\n${url}`);
    console.log(`${response.status} - ${response.statusText}:\n${url}`);
  }

  /**
   * Aplica las reglas de validación definidas para un formulario HTML.
   * Incluso puede indicar un callback como segundo argumento para complementar la validación
   * @param {String} formSelector Una regla CSS para referenciar el formulario a validar
   */
  static okForm = (formSelector, callBack) => {
    let ok = true;
    let form = formSelector;
    if (typeof formSelector === "string") {
      form = document.querySelector(formSelector);
    }

    // si los datos del formulario no son válidos, forzar un submit para que se muestren los errores
    if (!form.checkValidity()) {
      let tmpSubmit = document.createElement("button");
      form.appendChild(tmpSubmit);
      tmpSubmit.click();
      form.removeChild(tmpSubmit);
      ok = false;
    }
    if (typeof callBack === "function") {
      ok = ok && callBack();
    }
    return ok;
  };

  static isNumeric(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
  }

  /**
   * Permite asignar elementos a una lista desplegable a partir de un array de objetos
   * @param {String} selector es el Selector CSS que permite llegar a la lista despelgable
   * @param {array} items el array de elementos utilizado para poblar el select
   * @param {String} value es la clave o id
   * @param {String} text es el nombre del atributo
   * @param {String} firstOption Recibe el 'placeholder' de la lista seleccionable
   *
   * @returns {list} retorna la lista con los elementos asignados
   */
  static populateSelectList = (
    selector,
    items = [],
    value = "",
    text = "",
    firstOption = ""
  ) => {
    let list = document.querySelector(selector);
    list.options.length = 0;
    if (firstOption) {
      list.add(new Option(firstOption, ""));
    }
    items.forEach((item) => list.add(new Option(item[text], item[value])));
    return list;
  };

  /**
   * Permite obtener de una lista seleccionable su índice, su valor y su texto
   * @param {String} selector Es el selector CSS que permite seleccionar la lista
   * @returns  retorna un objeto con el índice dado, valor y texto
   */
  static selectedItemList = (selector) => {
    const list = document.querySelector(selector);
    const item = list.options[list.selectedIndex];

    return {
      selectedIndex: list.selectedIndex,
      value: item.value,
      text: item.text,
    };
  };

  /**
   * @param {Toast} _toast
   */
  static set Toast(_toast) {
    Helpers.toast = _toast;
  }

  static download(filename, fileContent) {
    const element = document.createElement("a");
    element.setAttribute(
      "href",
      "data:text/plain;charset=utf-8, " + encodeURIComponent(fileContent)
    );
    element.setAttribute("download", filename);
    element.style.position = "absolute";
    element.style.left = "-9999px";
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  }

  /**
   * Intercambiar clases CSS para hacer algún efecto
   * @param {String} selector El elemento afectado
   * @param {*} class1 Una clase CSS
   * @param {*} class2 Otra clase CSS
   */
  static toggle(selector, class1, class2) {
    const element = document.querySelector(selector);
    if (element.classList.contains(class1)) {
      element.classList.replace(class1, class2);
    } else {
      element.classList.replace(class2, class1);
    }
  }

  /**
   *
   * @param {*} element Un selector o la referencia del elemento donde se ubica el aviso
   * @param {String} message1 El mensaje para el usuario
   * @param {String} type La clase CSS que se aplica al aviso
   * @param {String} message2 El mensaje para consola
   */
  static async notice(element, message1, type = "success", message2 = "") {
    if (message2) {
      console.warn(message2);
    }

    if (typeof element === "string") {
      element = document.querySelector(element);
    }

    element.insertAdjacentHTML(
      "afterend",
      `<div id="alert" class="pure-u-1 alert ${type}">${message1}</div>`
    );

    return new Promise((resolve, reject) => {
      setTimeout(() => {
        document.querySelector("#alert").style.display = "none";
        resolve();
      }, 3000);
    });
  }

  /**
   * Oculta o muestra una colección de elementos. Ejemplo:
   * Helpers.collapse("#form > ul li", true, 1, 5);
   * @param {*} selector Patrón para buscar la selección de nodos
   * @param {*} yes Si true, colapsa, si false vuelve a mostrar
   * @param {*} from desde qué numero de nodo aplicar el cambio
   * @param {*} to hasta que nodo aplicar el cambio
   */
  static collapse(selector, yes, from, to) {
    const display = yes ? "none" : "block";
    to++;
    const items = document.querySelectorAll(selector);
    for (let i = from; i < to; i++) {
      items[i].style.display = display;
    }
  }

  static format(number, decimals = 0) {
    // https://www.w3schools.com/jsref/jsref_tolocalestring_number.asp
    let format = {
      style: "decimal",
      decimals,
    };
    return number.toLocaleString("es-CO", format);
  }
}

// ---------------------------------------------------

/**
 * Cambia las ocurrencias de $# por los strings indicados como argumento. Ejemplo:
 * let z = 'Probando $0 de $1 con $2'.translate('strings', 'JavaScript', 'expresiones regulares')
 * retorna 'Probando strings de JavaScript con expresiones regulares'
 *
 * @param  {...any} texts los strings que se usan para hacer el reemplazo
 * @returns El string original con los reemplazos realizados
 */
String.prototype.translate = function (...texts) {
  let str = this;
  const regex = /\$(\d+)/gi; // no requiere comprobación de mayúsculas pero se deja como ejemplo
  return str.replace(regex, (item, index) => texts[index]);
};
