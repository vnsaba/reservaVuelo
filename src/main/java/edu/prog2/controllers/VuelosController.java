package edu.prog2.controllers;

import edu.prog2.helpers.StandardResponse;
import edu.prog2.services.VuelosService;
import static spark.Spark.*;

import org.json.JSONObject;

public class VuelosController {
    public VuelosController(final VuelosService vuelosService) {
        path("/vuelos", () -> {

            get("", (request, response) -> {
                try {
                    return new StandardResponse(response, 201, "ok", vuelosService.getJSON());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new StandardResponse(response, 404, e);
                }

            });
            get("/select/:select", (request, response) -> {
                try {
                    String params = request.params(":select");
                    return new StandardResponse(response, 201, "ok", vuelosService.getVuelos(params));
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            get("/:vuelo", (request, response) -> {
                try {
                    String params = request.params(":vuelo");
                    JSONObject json = vuelosService.get(params);
                    return new StandardResponse(response, 200, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            post("", (request, response) -> {
                try {
                    JSONObject json = new JSONObject(request.body());
                    vuelosService.add(json);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception exception) {
                    return new StandardResponse(response, 404, exception);
                }
            });
            put("", (request, response) -> {
                try {
                    JSONObject json = new JSONObject(request.body());
                    json = vuelosService.set(json);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            delete("/:vuelo", (request, response) -> {
                try {
                    String params = request.params(":vuelo");
                    vuelosService.remove(params);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
        });
    }
}
