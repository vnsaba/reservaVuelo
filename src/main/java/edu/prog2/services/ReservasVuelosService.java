package edu.prog2.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import edu.prog2.helpers.UtilFiles;
import edu.prog2.model.*;

public class ReservasVuelosService {

    private ArrayList<ReservaVuelo> reservaVuelos;
    private ReservasService reservas;
    private VuelosService vuelos;
    private SillasService sillas;
    private String fileName;
    static PasajerosService pasajeros;
    static TrayectosService trayectos;

    public ReservasVuelosService(ReservasService reservas, VuelosService vuelo, SillasService silla)
            throws IOException {
        this.sillas = silla;
        this.reservas = reservas;
        this.vuelos = vuelo;
        reservaVuelos = new ArrayList<>();
        fileName = UtilFiles.FILE_PATH + "vuelos-de-reservas";
        if (UtilFiles.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (UtilFiles.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.println("Aún no se ha creado un archivo: " + fileName);
        }
    }

    public void loadCSV() throws IOException {
        String linea;
        LocalDateTime fecha;
        String identificacion;
        LocalDateTime fechaHoraVuelo;
        String origen;
        String destino;
        String matricula;
        int fila;
        char columna;
        boolean disponible;
        boolean checkin;
        try (BufferedReader archivo = Files.newBufferedReader(Paths.get(fileName + ".csv"))) {

            while ((linea = archivo.readLine()) != null) {
                String data[] = linea.split(";");
                fecha = LocalDateTime.parse(data[0]);
                identificacion = data[1];
                fechaHoraVuelo = LocalDateTime.parse(data[2]);
                origen = data[3];
                destino = data[4];
                matricula = data[5];
                fila = Integer.parseInt(data[6]);
                columna = data[7].charAt(0);
                disponible = Boolean.parseBoolean(data[8]);
                checkin = Boolean.parseBoolean(data[9]);
                Vuelo vuelo = vuelos
                        .get(new Vuelo(fechaHoraVuelo, new Trayecto(origen, destino), new Avion(matricula, "")));
                Avion avion = vuelo.getAvion();
                Reserva reserva = reservas.get(new Reserva(fecha, new Pasajero(identificacion, "", "")));

                if (data.length == 12) {
                    Menu menu = Menu.valueOf(data[10]);
                    Licor licor = Licor.valueOf(data[11]);
                    Silla sillaEjecutiva = new SillaEjecutiva(fila, columna, menu, licor, avion);
                    ReservaVuelo reservaVuelo = new ReservaVuelo(reserva, vuelo, sillaEjecutiva);
                    reservaVuelo.setCheckin(checkin);
                    reservaVuelo.getSilla().setDisponible(disponible);
                    reservaVuelos.add(reservaVuelo);

                } else {
                    Silla silla = sillas.get(new Silla(fila, columna, avion));
                    ReservaVuelo reservaVuelo = new ReservaVuelo(reserva, vuelo, silla);
                    reservaVuelo.setCheckin(checkin);
                    reservaVuelo.getSilla().setDisponible(disponible);
                    reservaVuelos.add(reservaVuelo);

                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void listAll() throws Exception {
        ArrayList<String> list = (ArrayList<String>) (ArrayList<?>) (reservaVuelos);
        int length = 96;
        String title = "LISTADO DE RESERVAS VUELOS";
        list.add(0, " ".repeat((length - title.length()) / 2) + title);
        list.add(1, "-".repeat(length));
        list.add(2, "-".repeat(length));
        list.add("-".repeat(length));
        UtilFiles.writeText(list, fileName + ".txt");
    }

    public JSONObject get(String paramsReservaVuelo) throws Exception {

        JSONObject json = UtilFiles.paramsToJson(paramsReservaVuelo);
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHoraReserva"));

        Pasajero pasajero = reservas.getPasajero().get(new Pasajero(json.getString("pasajero"), "", ""));

        Reserva reserva = reservas.get(new Reserva(fechaHora, pasajero));

        Avion avion = sillas.getAviones().get(new Avion(json.getString("avion"), ""));

        Trayecto trayecto = vuelos.getTrayectos().get(new Trayecto(
                json.getString("origen"), json.getString("destino"), Duration.ZERO, 0.0));

        LocalDateTime fechaHoraVuelo = LocalDateTime.parse(json.getString("fechaHoraVuelo"));

        Vuelo vuelo = vuelos.get(new Vuelo(fechaHoraVuelo, trayecto, avion));

        int fila = Integer.parseInt(json.getString("fila"));
        char columna = json.getString("columna").charAt(0);
        Silla silla = sillas.get(new Silla(fila, columna, avion));

        ReservaVuelo reservaVuelo = get(new ReservaVuelo(reserva, vuelo, silla));
        if (reservaVuelo == null) {
            throw new Exception("no se ha encontrado la reserva");
        }

        return getJSON(reservaVuelo);
    }

    public void add(JSONObject json) throws IOException {

        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHoraReserva"));

        Pasajero pasajero = reservas.getPasajero().get(new Pasajero(json.getString("pasajero"), "", ""));
        Reserva reserva = new Reserva(fechaHora, pasajero);
        reserva = reservas.get(reserva);

        LocalDateTime fechaHoraVuelo = LocalDateTime.parse(json.getString("fechaHoraVuelo"));

        Trayecto trayecto = vuelos.getTrayectos()
                .get(new Trayecto(json.getString("origen"), json.getString("destino"), Duration.ZERO, 0.0));

        Avion avion = vuelos.getAviones().get(new Avion(json.getString("avion"), ""));
        Vuelo vuelo = new Vuelo(fechaHoraVuelo, trayecto, avion);
        vuelo = vuelos.get(vuelo);

        int fila = json.getInt("fila");
        char columna = json.getString("columna").charAt(0);
        Silla silla = new Silla(fila, columna, avion);
        silla = sillas.get(silla);

        if (silla instanceof SillaEjecutiva) {
            Menu menu = Menu.valueOf(json.getString("menu"));
            Licor licor = Licor.valueOf(json.getString("licor"));
            SillaEjecutiva sillaEjecutiva = new SillaEjecutiva(fila, columna, menu, licor, avion);
            ReservaVuelo reservaVuelo = new ReservaVuelo(reserva, vuelo, sillaEjecutiva);
            reservaVuelo.getSilla().setDisponible(false);
            add(reservaVuelo);

        } else {
            silla.setDisponible(false);
            ReservaVuelo reservaVuelo = new ReservaVuelo(reserva, vuelo, silla);
            reservaVuelo.getSilla().setDisponible(false);
            add(reservaVuelo);

        }

    }

    public JSONObject set(String params, JSONObject body) throws Exception {
        JSONObject json = UtilFiles.paramsToJson(params);

        Pasajero pasajero = reservas.getPasajero().get(new Pasajero(json.getString("pasajero"), "", ""));
        LocalDateTime fechaHoraReserva = LocalDateTime.parse(json.getString("fechaHoraReserva"));
        Reserva reserva = getReserva().get(new Reserva(fechaHoraReserva, pasajero));
        LocalDateTime fechaHoraVuelo = LocalDateTime.parse(json.getString("fechaHoraVuelo"));
        Avion avion = vuelos.getAviones().get(new Avion(json.getString("avion"), ""));
        Trayecto trayecto = vuelos.getTrayectos()
                .get(new Trayecto(json.getString("origen"), json.getString("destino"), Duration.ZERO, 0.0));

        Vuelo vuelo = getVuelo().get(new Vuelo(fechaHoraVuelo, trayecto, avion));
        int fila = Integer.parseInt(json.getString("fila"));
        char columna = json.getString("columna").charAt(0);
        Silla silla = sillas.get(new Silla(fila, columna, avion));
        ReservaVuelo reservaVuelo = new ReservaVuelo(reserva, vuelo, silla);
        reservaVuelo = get(reservaVuelo);

        if (body.has("fechaHoraVuelo")) {
            LocalDateTime fechaHoraVuelonuevo = LocalDateTime.parse(body.getString("fechaHoraVuelo"));
            Trayecto nuevoTrayecto = vuelos.getTrayectos()
                    .get(new Trayecto(body.getString("origen"), body.getString("destino"), Duration.ZERO, 0.0));
            Avion nuevoAvion = vuelos.getAviones().get(new Avion(body.getString("avion"), ""));
            Vuelo nuevoVuelo = getVuelo()
                    .get(new Vuelo(fechaHoraVuelonuevo, nuevoTrayecto, nuevoAvion));

            if ((fechaHoraVuelo.equals(LocalDateTime.now())
                    || fechaHoraVuelonuevo.isBefore(vuelo.getFechaHora()) == true || nuevoVuelo.getCancelado() == true)
                    || sillasDisponiblesEnVuelo(nuevoVuelo) == null) {
                throw new Exception("no es posible realizar el cambio con el nuevo vuelo solicitado");
            }

            if (body.has("fila") || body.has("columna")) {
                Silla nuevaSilla = getSilla()
                        .get(new Silla(body.getInt("fila"), body.getString("columna").charAt(0), nuevoAvion));

                if (sillaDisponibleEnVuelo(nuevaSilla, nuevoVuelo)) {
                    if (nuevaSilla instanceof SillaEjecutiva) {
                        SillaEjecutiva sillaE = (SillaEjecutiva) nuevaSilla;
                        if (body.has("menu") || body.has("licor")) {
                            sillaE.setMenu(body.getEnum(Menu.class, "menu"));
                            sillaE.setLicor(body.getEnum(Licor.class, "licor"));
                        }
                        nuevaSilla = sillaE;
                    }
                    reservaVuelo.getSilla().setDisponible(true);
                    nuevaSilla.setDisponible(false);
                    reservaVuelo.setSilla(nuevaSilla);
                    reservaVuelo.setVuelo(nuevoVuelo);
                } else {
                    throw new Exception("la silla no se encuentra disponible");
                }

            } else {
                ArrayList<Silla> sd = sillasDisponiblesEnVuelo(nuevoVuelo); // cuantas sillas disponibles tiene el vuelo
                Silla sillaAntigua = reservaVuelo.getSilla();

                boolean ok = false;
                for (Silla s : sd) {
                    Silla sillaNueva = s;
                    if (s instanceof SillaEjecutiva && sillaDisponibleEnVuelo(s, nuevoVuelo)
                            && sillaAntigua instanceof SillaEjecutiva) {
                        SillaEjecutiva sillaE = (SillaEjecutiva) sillaNueva;
                        if (sillaAntigua instanceof SillaEjecutiva) {
                            SillaEjecutiva sE = (SillaEjecutiva) sillaAntigua;
                            sillaE.setMenu(sE.getMenu());
                            sillaE.setLicor(sE.getLicor());
                        }
                        sillaNueva = sillaE;
                        sillaNueva.setDisponible(false);
                        reservaVuelo.getSilla().setDisponible(true);
                        reservaVuelo.setSilla(sillaNueva);
                        reservaVuelo.setVuelo(nuevoVuelo);
                        reservaVuelo.setVuelo(nuevoVuelo);
                        ok = true;
                        break;
                    } else if (!(sillaNueva instanceof SillaEjecutiva) && !(sillaAntigua instanceof SillaEjecutiva)
                            && sillaDisponibleEnVuelo(sillaNueva, nuevoVuelo)) {
                        // cambiar vuelo en vueloReservado cuando la silla esté disponible
                        reservaVuelo.getSilla().setDisponible(true);
                        sillaNueva.setDisponible(false);
                        reservaVuelo.setSilla(sillaNueva);
                        reservaVuelo.setVuelo(nuevoVuelo);
                        ok = true;
                        break;
                    }

                }
                if (ok == false) {
                    throw new Exception("no ha sido poosible realizar el cambio de la silla solicitada");
                }
            }
        } else {

            if (body.has("columna") && body.has("fila")) {
                Silla nuevaSilla = getSilla()
                        .get(new Silla(body.getInt("fila"), body.getString("columna").charAt(0), avion));
                if (sillaDisponibleEnVuelo(nuevaSilla, vuelo)) {
                    if (nuevaSilla instanceof SillaEjecutiva) {
                        SillaEjecutiva sillaE = (SillaEjecutiva) nuevaSilla;
                        if (body.has("menu") || body.has("licor")) {
                            sillaE.setMenu(body.getEnum(Menu.class, "menu"));
                            sillaE.setLicor(body.getEnum(Licor.class, "licor"));
                        }
                        nuevaSilla = sillaE;
                    }
                    reservaVuelo.getSilla().setDisponible(true);
                    nuevaSilla.setDisponible(false);
                    reservaVuelo.setSilla(nuevaSilla);
                } else {
                    throw new Exception("la silla no se encuentra dispoibnle");
                }
            }
        }

        UtilFiles.writeData(reservaVuelos, fileName);
        return new JSONObject(reservaVuelo);
    }

    public ArrayList<Silla> sillasDisponiblesEnVuelo(Vuelo vuelo) {
        ArrayList<Silla> Sillasdisponible = new ArrayList<>();
        for (Silla silla : sillas.getList()) {
            if (silla.getAvion().equals(vuelo.getAvion()) && sillaDisponibleEnVuelo(silla, vuelo) == true) {
                Sillasdisponible.add(silla);
            }
        }
        return Sillasdisponible;
    }

    public boolean sillaDisponibleEnVuelo(Silla silla, Vuelo vuelo) {
        ArrayList<Silla> sillasReservadas = sillasReservadasEnVuelo(vuelo);
        for (Silla sillas : sillasReservadas) {
            if (silla.getAvion().equals(vuelo.getAvion()) && silla.equals(sillas)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Silla> sillasReservadasEnVuelo(Vuelo vuelo) {
        ArrayList<Silla> sillasReservadas = new ArrayList<>();
        for (ReservaVuelo silla : getList()) {
            Silla sillas = silla.getSilla();
            if (sillas.getAvion().equals(vuelo.getAvion())) {
                if (sillas.getDisponible() == false && sillas.getDisponible() == false) {
                    sillasReservadas.add(sillas);
                }
            }
        }
        return sillasReservadas;
    }

    public void remove(String params) throws Exception {
        int cont = 0;
        JSONObject json = get(params);
        String columna = json.getJSONObject("silla").getString("posicion");

        columna = columna.substring(columna.length() - 1, columna.length());
        json.getJSONObject("silla").put("columna", columna);

        Reserva reserva = new Reserva(json.getJSONObject("reserva"));
        Vuelo vuelo = new Vuelo(json.getJSONObject("vuelo"));
        Silla silla = new Silla(json.getJSONObject("silla"));

        ReservaVuelo reservaVuelo = new ReservaVuelo(reserva, vuelo, silla);
        for (Reserva r : reservas.getList()) {
            if (reserva.equals(r)) {
                cont++;
            }
        }

        if (!reservaVuelos.remove(reservaVuelo)) {
            throw new Exception(String.format("No se ha encontrado la reserva %s del vuelo  %s",
                    reservaVuelo.getReserva(), reservaVuelo.getVuelo()));
        }
        if (cont == 1) {
            reservas.getList().remove(reservaVuelo.getReserva());
        }
        UtilFiles.writeData(reservas.getList(), UtilFiles.FILE_PATH + "reservas");
        UtilFiles.writeData(reservaVuelos, fileName);

    }

    public void update() throws IOException {
        reservaVuelos = new ArrayList<>();
        loadCSV();
        UtilFiles.writeJSON(reservaVuelos, fileName + ".json");
    }

    public SillasService getSilla() {
        return sillas;
    }

    public ReservasService getReserva() {
        return reservas;
    }

    public VuelosService getVuelo() {
        return vuelos;
    }

    public JSONArray getJSON() throws IOException {
        return new JSONArray(UtilFiles.readText(fileName + ".json"));
    }

    public JSONObject getJSON(ReservaVuelo reservaVuelo) {
        int index = reservaVuelos.indexOf(reservaVuelo);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONObject getJSON(int index) {
        return new JSONObject(reservaVuelos.get(index));
    }

    public ArrayList<ReservaVuelo> loadJSON() throws IOException {
        reservaVuelos = new ArrayList<>();
        String data = UtilFiles.readText(fileName + ".json");
        JSONArray jsonArrReservaVuelo = new JSONArray(data);
        for (int i = 0; i < jsonArrReservaVuelo.length(); i++) {
            JSONObject jsonObjecVuelo = jsonArrReservaVuelo.getJSONObject(i);
            reservaVuelos.add(new ReservaVuelo(jsonObjecVuelo));
        }
        return reservaVuelos;
    }

    public boolean add(ReservaVuelo reservaVuelo) throws IOException {
        if (contains(reservaVuelo)) {
            throw new ArrayStoreException(
                    String.format("la reserva del vuelo %s %s %s ya existe :", reservaVuelo.getReserva().strFechaHora(),
                            reservaVuelo.getVuelo(), reservaVuelo.getSilla().getPosicion()));
        }
        boolean ok = reservaVuelos.add(reservaVuelo);
        UtilFiles.writeData(reservaVuelos, fileName);
        return ok;
    }

    public boolean contains(ReservaVuelo reservaVuelo) {
        return reservaVuelos.contains(reservaVuelo);
    }

    public ReservaVuelo get(int index) {
        return reservaVuelos.get(index);
    }

    public ReservaVuelo get(ReservaVuelo reservaVuelo) {
        int index = reservaVuelos.indexOf(reservaVuelo);
        return index > -1 ? reservaVuelos.get(index) : null;
    }

    public ArrayList<ReservaVuelo> getList() {
        return reservaVuelos;
    }

}
