package edu.prog2.controllers;

import edu.prog2.services.TrayectosService;
import static spark.Spark.*;
import org.json.JSONObject;
import edu.prog2.helpers.StandardResponse;
import edu.prog2.model.Trayecto;

public class TrayectosController {
  public TrayectosController(final TrayectosService trayectosService) {

    path("/trayectos", () -> {

      get("", (request, response) -> {
        try {
          return new StandardResponse(response, 201, "ok", trayectosService.getJSON());
        } catch (Exception e) {
          e.printStackTrace();
          return new StandardResponse(response, 404, e);
        }

      });
      get("/:trayecto", (request, response) -> {
        try {
          String params = request.params(":trayecto");
          JSONObject json = trayectosService.get(params);
          if (json == null) {
            return new StandardResponse(response, 404, "No se encontró el registro", json);
          } else {
            return new StandardResponse(response, 201, "ok", json);
          }
        } catch (Exception e) {
          return new StandardResponse(response, 404, e);
        }
      });

      post("", (request, response) -> {
        try {
          Trayecto trayectos = new Trayecto(request.body());
          trayectosService.add(trayectos);
          ;
          return new StandardResponse(response, 201, "ok");
        } catch (Exception exception) {
          return new StandardResponse(response, 404, exception);
        }
      });

      put("", (request, response) -> {
        try {
          JSONObject json = new JSONObject(request.body());
          json = trayectosService.set(json);
          return new StandardResponse(response, 201, "ok", json);
        } catch (Exception e) {
          return new StandardResponse(response, 404, e);
        }
      });
      delete("/:trayecto", (request, response) -> {
        try {
          String params = request.params(":trayecto");
          trayectosService.remove(params);
          return new StandardResponse(response, 201, "ok");
        } catch (Exception e) {
          return new StandardResponse(response, 404, e);
        }
      });
    });

  }
}
