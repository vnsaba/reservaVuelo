package edu.prog2.controllers;

import edu.prog2.helpers.StandardResponse;
import edu.prog2.model.Avion;
import edu.prog2.services.AvionesService;
import static spark.Spark.*;
import org.json.JSONObject;

public class AvionesController {

    public AvionesController(final AvionesService avionesService) {
        path("/aviones", () -> {

            get("", (request, response) -> {
                try {
                    return new StandardResponse(response, 201, "ok", avionesService.getJSON());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new StandardResponse(response, 404, e);
                }

            });
            get("/:matricula", (request, response) -> {
                try {
                    String matricula = request.params(":matricula");
                    Avion a = new Avion(matricula, null);
                    JSONObject json = new JSONObject(avionesService.get(a));
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new StandardResponse(response, 404, e);
                }
            });
            post("", (request, response) -> {
                try {
                    Avion avion = new Avion(request.body());
                    avionesService.add(avion);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception exception) {
                    return new StandardResponse(response, 404, exception);
                }
            });
            put("/:matricula", (request, response) -> {
                try {
                    String matricula = request.params(":matricula");
                    JSONObject json = new JSONObject(request.body());
                    json = avionesService.set(matricula, json);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            delete("/:matricula", (request, response) -> {
                try {
                    String matricula = request.params(":matricula");
                    avionesService.remove(matricula);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }

            });
        });
    }
}