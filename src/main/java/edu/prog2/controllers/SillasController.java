package edu.prog2.controllers;

import static spark.Spark.*;
import org.json.JSONObject;
import edu.prog2.helpers.StandardResponse;
import edu.prog2.services.SillasService;

public class SillasController {
    public SillasController(final SillasService sillasService) {
        path("/sillas", () -> {

            get("", (request, response) -> {
                try {
                    return new StandardResponse(response, 201, "ok", sillasService.getJSON());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new StandardResponse(response, 404, e);
                }

            });
            get("/total", (request, response) -> {
                try {
                    return new StandardResponse(response, 201, "ok", sillasService.aircraftWithNumberSeats());
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }

            });
            get("/select/:select", (request, response) -> {
                try {
                    String params = request.params(":select");
                    return new StandardResponse(response, 201, "ok", sillasService.getSillasAvion(params));
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);

                }
            });
            get("/:silla", (request, response) -> {
                try {
                    String params = request.params(":silla");
                    JSONObject json = sillasService.get(params);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            get("/avion/:avion", (request, response) -> {
                try {
                    String params = request.params(":avion");
                    JSONObject json = sillasService.numberOfSeats(params);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            post("", (request, response) -> {
                try {
                    JSONObject json = new JSONObject(request.body());
                    String avion = json.getString("avion");
                    int ejecutivas = json.getInt("ejecutivas");
                    int economicas = json.getInt("economicas");
                    sillasService.create(avion, ejecutivas, economicas);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception exception) {
                    return new StandardResponse(response, 404, exception);
                }
            });
            put("", (request, response) -> {
                try {
                    JSONObject json = new JSONObject(request.body());
                    json = sillasService.set(json);
                    return new StandardResponse(response, 201, "ok", json);
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
            delete("/:silla", (request, response) -> {
                try {
                    String params = request.params(":silla");
                    sillasService.remove(params);
                    return new StandardResponse(response, 201, "ok");
                } catch (Exception e) {
                    return new StandardResponse(response, 404, e);
                }
            });
        });
    }
}
