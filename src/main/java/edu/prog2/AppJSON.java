package edu.prog2;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import edu.prog2.helpers.*;
import edu.prog2.model.*;


public class AppJSON {

    private static JSONObject jsonPasajero;
    private static JSONObject jsonReserva;
    private static JSONObject jsonReservaVuelo;
    private static JSONObject jsonAvion;
    private static JSONObject jsonSilla;
    private static JSONObject jsonVuelo;
    private static JSONObject jsonSillaEjecutiva;
    private static JSONObject jsonTrayecto;
    private static JSONArray jsonArray;

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        do {
            try {
                int opcion = leerOpcion();
                switch (opcion) {
                    case 1:
                        testPutTrayecto();
                        break;
                    case 2:
                        testPutAvion();
                        break;
                    case 3:
                        testPutPasajero();
                        break;
                    case 4:
                        testPutVuelo();
                        break;
                    case 5:
                        testPutSilla();
                        break;
                    case 6:
                        testPutSillaEjecutiva();
                        break;
                    case 7:
                        testPutReserva();
                        break;
                    case 8:
                        testPutReservaVuelo();
                        break;
                    case 9:
                        testAvionString();
                        break;
                    case 10:
                        testPutTrayectoString();
                        break;
                    case 11:
                        testPasajeroString();
                        break;
                    case 12:
                        JSONObjectAvion();
                        break;
                    case 13:
                        JSONObjectPasajero();
                        break;
                    case 14:
                        JSONObjectTrayecto();
                        break;
                    case 15:
                        JSONObjectVuelo();
                        break;
                    case 16:
                        JSONTrayectoGet();
                        break;
                    case 17:
                        JSONSillaEjecutivaGet();
                        break;
                    case 18:
                        JSONObjectHas();
                        break;
                    case 19:
                        JSONArray();
                        break;
                    case 20:
                        JSONArrayRecorrer();
                        break;
                    case 21:
                        instanceTests();
                        break;
                    case 22:
                        jsonArrayTest();
                        break;
                    case 0:
                        System.exit(0);
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }

    static int leerOpcion() {
        String opciones = "\nMenú de opciones:\n"
                + "  1 - JSONTrayecto                                   12 - JSONObjectAvion apartir de un objeto      \n"
                + "  2 - JSONAvion                                      13 - JSONObjectPasajero apartir de un objeto   \n"
                + "  3 - JSONPasajero                                   14 - JSONObjectTrayecto apartir de un objeto   \n"
                + "  4 - JSONVuelo                                      15 - JSONObjectVuelo apartir de un objeto      \n"
                + "  5 - JSONSilla                                      16 - JSONTrayectoGet con get()                 \n"
                + "  6 - JSONSillaEjecutiva                             17 - JSONSillaEjecutivaGet con get ()          \n"
                + "  7 - JSONReserva                                    18 - JSON has ()                               \n"
                + "  8 - JSONReservaVuelo                               19 - JSONArray                                 \n"
                + "  9 - JSONAvion  String argumento                    20 - JSONArray Recorrer                        \n"
                + " 10 - JSONTrayecto  String argumento                 21 - instanceTests                             \n"
                + " 11 - JSONPasajero  String argumento                 22 - JSONArrayTest                             \n"
                + "\nElija una opción (0 para salir) > ";
        int opcion = Keyboard.readInt(opciones);
        System.out.println();
        return opcion;
    }

    private static void jsonArrayTest() throws Exception {
       instanceTests();
        ArrayList<Object> list = new ArrayList<>();

        list.add(new Pasajero(jsonPasajero));
        list.add(new Avion(jsonAvion));
        list.add(new Silla(jsonSilla));
        list.add(new SillaEjecutiva(jsonSillaEjecutiva));
        list.add(new Trayecto(jsonTrayecto));
        list.add(new Vuelo(jsonVuelo));
        list.add(new Reserva(jsonReserva));
        list.add(new ReservaVuelo(jsonReservaVuelo));

    }

    private static void instanceTests() throws IOException {
       
        
        String strPasajero = UtilFiles.readText("./data/tests/test-pasajero.json");
        jsonPasajero = new JSONObject(strPasajero);
        Pasajero pasajero = new Pasajero(jsonPasajero);
        System.out.println(pasajero);

        String strAvion = UtilFiles.readText("./data/tests/test-avion.json");
        jsonAvion = new JSONObject(strAvion);
        Avion avion = new Avion(jsonAvion);
        System.out.println(avion);

        String strSilla = UtilFiles.readText("./data/tests/test-silla.json");
        jsonSilla = new JSONObject(strSilla);
        Silla silla = new Silla(jsonSilla);
        System.out.println(silla);

        String strSillaEjecutiva = UtilFiles.readText("./data/tests/test-silla-ejecutiva.json");
        jsonSillaEjecutiva = new JSONObject(strSillaEjecutiva);
        SillaEjecutiva sillaEjecutiva = new SillaEjecutiva(jsonSillaEjecutiva);
        System.out.println(sillaEjecutiva);

        String strTrayecto = UtilFiles.readText("./data/tests/test-trayecto.json");
        jsonTrayecto = new JSONObject(strTrayecto);
        Trayecto trayecto = new Trayecto(jsonTrayecto);
        System.out.println(trayecto);

        String strVuelo = UtilFiles.readText("./data/tests/test-vuelo.json");
        jsonVuelo = new JSONObject(strVuelo);
        Vuelo vuelo = new Vuelo(jsonVuelo);
        System.out.println(vuelo);

        String strReserva = UtilFiles.readText("./data/tests/test-reserva.json");
        jsonReserva = new JSONObject(strReserva);
        Reserva reserva = new Reserva(jsonReserva);
        System.out.println(reserva);

        String strReservaVuelo = UtilFiles.readText("./data/tests/test-vuelo-reserva.json");
        jsonReservaVuelo = new JSONObject(strReservaVuelo);
        ReservaVuelo reservaVuelo = new ReservaVuelo(jsonReservaVuelo);
        System.out.println(reservaVuelo);

        UtilFiles.writeText("Hola mundo\nAdios mundo", "./data/tests/prueba1.txt");
        UtilFiles.writeText(jsonReservaVuelo.toString(2), "./data/tests/prueba2.txt");
     
    }

    private static void JSONArrayRecorrer() {
        for (int i = 0; i < jsonArray.length(); i++) {
            System.out.println(jsonArray.get(i));
        }
    }

    private static void JSONArray() {
        jsonArray = new JSONArray();
        jsonArray.put(jsonTrayecto);
        jsonArray.put(jsonPasajero);
        jsonArray.put(jsonAvion);
        jsonArray.put(jsonSillaEjecutiva);
        jsonArray.put("json array");
        jsonArray.put(125015);
        jsonArray.put(jsonReserva);
    }

    private static void JSONObjectHas() {
        if (jsonSilla.has("licor")) {
            System.out.println("Es una silla ejecutiva");
        } else {
            System.out.println("Es una silla económica");
        }

        if (jsonSillaEjecutiva.has("menu")) {
            System.out.println("Es una silla ejecutiva");
        } else {
            System.out.println("Es una silla económica");
        }
        System.out.println("Existe = " + jsonTrayecto.has("duración"));
        System.out.println("Existe = " + jsonTrayecto.has("duracion"));
        System.out.println("Existe = " + jsonTrayecto.has("destino"));
        System.out.println("Existe = " + jsonSilla.has("fila"));
        System.out.println("Existe = " + jsonTrayecto.has("costo"));
        System.out.println("Existe = " + jsonReserva.has("costoo"));
        System.out.println("Existe = " + jsonPasajero.has("nombre"));
        System.out.println("Existe = " + jsonReservaVuelo.has("nombres"));
    }

    private static void JSONSillaEjecutivaGet() {
        System.out.println(jsonSillaEjecutiva.getEnum(Ubicacion.class, "ubicacion"));
    }

    private static void JSONTrayectoGet() {
        System.out.printf("Origen: %s%n", jsonTrayecto.getString("origen"));
        System.out.printf("Destino: %s%n", jsonTrayecto.getString("destino"));
        System.out.printf("Duración: %s%n", jsonTrayecto.getString("duracion"));
        System.out.printf("Costo: %.0f%n", jsonTrayecto.getDouble("costo"));

    }

    private static void JSONObjectVuelo() {
        Trayecto trayecto = new Trayecto("cartagena", "sanata marta", Duration.parse("PT25H"), 120000);
        Avion avion = new Avion("HK2015", "Airbus 330");
        Vuelo vuelo = new Vuelo(LocalDateTime.parse("2022-10-12T11:00"), trayecto, avion);
        JSONObject jsonVuelo = new JSONObject(vuelo);
        System.out.println(jsonVuelo.toString(2));
    }

    private static void JSONObjectTrayecto() {
        Trayecto trayecto = new Trayecto("cartagena", "sanata marta", Duration.parse("PT25H"), 120000);
        JSONObject jsonTrayecto = new JSONObject(trayecto);
        System.out.println(jsonTrayecto.toString(2));
    }

    private static void JSONObjectPasajero() {
        Pasajero pasajero = new Pasajero("P003", "nidia", "alzate lopez");
        JSONObject jsonPasajero = new JSONObject(pasajero);
        System.out.println(jsonPasajero.toString(2));
    }

    private static void JSONObjectAvion() {
        Avion avion = new Avion("HK2015", "Airbus 330");
        JSONObject jsonAvion = new JSONObject(avion);
        System.out.println(jsonAvion.toString(2));

    }

    private static void testPasajeroString() {
        System.out.println(
                new JSONObject("{ \"identifiacion\": \"P002\", \"nombre\": \"Andres\", \"apellido\": \"Martinez\"}")
                        .toString(2));
    }

    private static void testPutTrayectoString() {
        System.out.println(new JSONObject(
                "{ \"origen\": \"manizales\", \"destino\": \"sanata marta\", \"duracion\": \"PT55M\", \"costo\": \"250000\"}")
                .toString(2));
    }

    private static void testAvionString() {
        System.out.println(new JSONObject("{ \"matricula\": \"HK2010\", \"modelo\": \"Airbus 330\" }").toString(2));
    }

    private static void testPutReservaVuelo() {
        jsonReservaVuelo = new JSONObject();
        jsonReservaVuelo.put("reserva", jsonReserva);
        jsonReserva.put("pasajero", jsonPasajero);
        jsonReservaVuelo.put("vuelo", jsonVuelo);
        jsonReservaVuelo.put("trayecto", jsonTrayecto);
        jsonReservaVuelo.put("avion", jsonVuelo);
        jsonReservaVuelo.put("fila", jsonSillaEjecutiva);
        jsonReservaVuelo.put("checkin", "true");
        System.out.println(jsonReservaVuelo.toString(2));

    }

    private static void testPutReserva() {
        jsonReserva = new JSONObject();
        jsonReserva.put("fechaHora", "2022-09-27T16:07");
        jsonReserva.put("pasajero", jsonPasajero);
        jsonReserva.put("cancelado", "false");
        System.out.println(jsonReserva.toString(2));

    }

    private static void testPutSillaEjecutiva() {
        jsonSillaEjecutiva = new JSONObject();
        jsonSillaEjecutiva.put("Avion", jsonAvion);
        jsonSillaEjecutiva.put("fila", "1");
        jsonSillaEjecutiva.put("columna", "B");
        jsonSillaEjecutiva.put( "ubicacion", "VENTANA");
        jsonSillaEjecutiva.put("disponible", "false");
        jsonSillaEjecutiva.put("menu", "vegetariano");
        jsonSillaEjecutiva.put("licor", "vino");
        System.out.println(jsonSillaEjecutiva.toString(2));
    }

    private static void testPutSilla() {
        jsonSilla = new JSONObject();
        jsonSilla.put("Avion", jsonAvion);
        jsonSilla.put("fila", "1");
        jsonSilla.put("columna", "A");
        jsonSilla.put("ubicacion", "ventana");
        jsonSilla.put("disponible", "false");
        System.out.println(jsonSilla.toString(2));
    }

    private static void testPutVuelo() {// revisar no funciona
        jsonVuelo = new JSONObject();
        jsonVuelo.put("fecha Hora", "2022-10-12T11:00");
        jsonVuelo.put("trayecto", jsonTrayecto);
        jsonVuelo.put("avion", jsonAvion);
        System.out.println(jsonVuelo.toString(2));
    }

    private static void testPutPasajero() {
        jsonPasajero = new JSONObject();
        jsonPasajero.put("Identificacion", "P001");
        jsonPasajero.put("Nombre", "Natalia");
        jsonPasajero.put("Apellidos", "Buitrago Alzate");
        System.out.println(jsonPasajero.toString(2));
    }

    private static void testPutAvion() {
        jsonAvion = new JSONObject();
        jsonAvion.put("matricula", "HK003");
        jsonAvion.put("modelo", "Airbus 350");
        System.out.println(jsonAvion.toString(2));
    }

    private static void testPutTrayecto() {
        jsonTrayecto = new JSONObject();
        jsonTrayecto.put("origen", "Manizales");
        jsonTrayecto.put("destino", "Bogotá");
        jsonTrayecto.put("duracion", "PT45M");
        jsonTrayecto.put("costo", 120000);
        System.out.println(jsonTrayecto.toString(2));
    }
}