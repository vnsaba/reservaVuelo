package edu.prog2;

import edu.prog2.controllers.*;
import edu.prog2.helpers.StandardResponse;
import edu.prog2.services.*;
import spark.Spark;

import static spark.Spark.*;

public class AppRest {

    public static void main(String[] args) throws Exception {

        enableCORS();

        PasajerosService pasajerosService = new PasajerosService();
        new PasajerosController(pasajerosService);

        TrayectosService trayectosService = new TrayectosService();
        new TrayectosController(trayectosService);

        AvionesService avionesService = new AvionesService();
        new AvionesController(avionesService);

        VuelosService vuelosService = new VuelosService(trayectosService, avionesService);
        new VuelosController(vuelosService);

        SillasService sillasService = new SillasService(avionesService);
        new SillasController(sillasService);

        ReservasService reservasService = new ReservasService(pasajerosService);
        new ReservasController(reservasService);

        ReservasVuelosService reservasVuelosService = new ReservasVuelosService(reservasService, vuelosService,
                sillasService);
        new ReservasVuelosController(reservasVuelosService);

        after("/pasajeros/:id", (request, response) -> {
            try {
                if (request.requestMethod().equals("PUT")) {
                    reservasService.update();
                    reservasVuelosService.update();
                }
            } catch (Exception e) {
                new StandardResponse(response, 404, e);
            }
        });

        after("/trayectos", (request, response) -> {
            try {
                if (request.requestMethod().equals("PUT")) {
                    vuelosService.update();
                    reservasVuelosService.update();
                }
            } catch (Exception e) {
                new StandardResponse(response, 404, e);
            }
        });

        after("/aviones/:matricula", (request, response) -> {
            try {
                if (request.requestMethod().equals("PUT")) {
                    vuelosService.update();
                    sillasService.update();
                    reservasVuelosService.update();
                }
            } catch (Exception e) {
                new StandardResponse(response, 404, e);
            }
        });

        after("/vuelos", (request, response) -> {
            try {
                if (request.requestMethod().equals("PUT")) {
                    reservasVuelosService.update();
                }
            } catch (Exception e) {
                new StandardResponse(response, 404, e);
            }
        });

        after("/reservas", (request, response) -> {
            try {
                if (request.requestMethod().equals("PUT")) {
                    reservasVuelosService.update();
                }
            } catch (Exception e) {
                new StandardResponse(response, 404, e);
            }
        });

        after("/sillas", (request, response) -> {
            try {
                if (request.requestMethod().equals("PUT")) {
                    reservasVuelosService.update();
                }
            } catch (Exception e) {
                new StandardResponse(response, 404, e);
            }
        });
    }

    private static void enableCORS() {
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
    
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
    
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
    
            return "OK";
        });
    
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }
    
}
