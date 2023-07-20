package edu.prog2.controllers;

import static spark.Spark.*;

import org.json.JSONObject;

import edu.prog2.helpers.StandardResponse;
import edu.prog2.model.Pasajero;
import edu.prog2.services.PasajerosService;

public class PasajerosController {

  public PasajerosController(final PasajerosService pasajerosService) {

    path("/pasajeros", () -> {

      get("", (request, response) -> {
        try {
          return new StandardResponse(response, 201, "ok", pasajerosService.getJSON());
        } catch (Exception e) {
          e.printStackTrace();
          return new StandardResponse(response, 404, e);
        }

      });
      get("/:id", (request, response) -> {
        try {
          String id = request.params(":id");
          Pasajero p = new Pasajero(id, null, null);
          JSONObject json = new JSONObject(pasajerosService.get(p));
          return new StandardResponse(response, 201, "ok", json);
        } catch (Exception e) {
          e.printStackTrace();
          return new StandardResponse(response, 404, e);
        }
      });
      post("", (request, response) -> {
        try {
          Pasajero pasajero = new Pasajero(request.body());
          pasajerosService.add(pasajero);
          return new StandardResponse(response, 201, "ok");
        } catch (Exception exception) {
          response.status(404);
          return new StandardResponse(response, 404, exception);
        }
      });
      put("/:identificacion", (request, response) -> {
        try {
          String identificacion = request.params(":identificacion");
          JSONObject json = new JSONObject(request.body());
          json = pasajerosService.set(identificacion, json);
          return new StandardResponse(response, 201, "ok", json);
        } catch (Exception e) {
          return new StandardResponse(response, 404, e);
        }
      });
      delete("/:id", (request, response) -> {
        try {
          String id = request.params(":id");
          pasajerosService.remove(id);
          return new StandardResponse(response, 201, "ok");
        } catch (Exception e) {
          return new StandardResponse(response, 404, e);
        }
      });
    });

  }

}
