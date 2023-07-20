package edu.prog2.controllers;

import static spark.Spark.*;
import org.json.JSONObject;
import edu.prog2.helpers.StandardResponse;
import edu.prog2.services.ReservasVuelosService;

public class ReservasVuelosController {
    public ReservasVuelosController(final ReservasVuelosService reservasVuelosService) {

        path("/vuelos-reservas", () -> {

            get("", (request, response) -> {
                try {
                    response.status(201);
                    return new StandardResponse(response, 201, "ok", reservasVuelosService.getJSON());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new StandardResponse(response, 404, e);
                }
            });
            get("/:vuelo-reserva", (request, response) -> {
                try {
                    String params = request.params(":vuelo-reserva");
                    JSONObject json = reservasVuelosService.get(params);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            post("", (request, response) -> {
                try {
                    JSONObject json = new JSONObject(request.body());
                    reservasVuelosService.add(json);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception exception) {
                    return new StandardResponse(response, 404, exception);
                }
            });
            put("/:vuelo", (request, response) -> {
                try {
                    String params = request.params(":vuelo");
                    JSONObject jsonBody = new JSONObject(request.body());
                    JSONObject json = reservasVuelosService.set(params, jsonBody);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            delete("/:vuelos-reservas", (request, response) -> {
                try {
                    String params = request.params(":vuelos-reservas");
                    reservasVuelosService.remove(params);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
        });
    }
}
