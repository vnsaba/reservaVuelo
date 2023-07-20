package edu.prog2.controllers;

import edu.prog2.services.ReservasService;

import static spark.Spark.*;
import org.json.JSONObject;
import edu.prog2.helpers.StandardResponse;


public class ReservasController {
    public ReservasController(final ReservasService reservasService) {
        path("/reservas", () -> {
            get("", (request, response) -> {
                try {
                    return new StandardResponse(response, 201, "ok", reservasService.getJSON());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new StandardResponse(response, 404, e);
                }
            });
            get("/:reserva", (request, response) -> {
                try {
                    String params = request.params(":reserva");
                    JSONObject json = reservasService.get(params);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            post("", (request, response) -> {
                try {
                    JSONObject json = new JSONObject(request.body());
                    reservasService.add(json);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception exception) {
                    return new StandardResponse(response, 404, exception);
                }
            });

            put("", (request, response) -> {
                try {
                    JSONObject json = new JSONObject(request.body());
                    json = reservasService.set(json);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            delete("/:reserva", (request, response) -> {
                try {
                    String params = request.params(":reserva");
                    reservasService.remove(params);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
        });
    }
}