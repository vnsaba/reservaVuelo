package edu.prog2.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import edu.prog2.helpers.UtilFiles;
import edu.prog2.model.Pasajero;
import edu.prog2.model.Reserva;
import edu.prog2.model.ReservaVuelo;

public class ReservasService {
    private PasajerosService pasajero;
    private ArrayList<Reserva> reservas;
    private String fileName;

    public ReservasService(PasajerosService pasajero) throws IOException {
        this.pasajero = pasajero;
        reservas = new ArrayList<>();
        fileName = UtilFiles.FILE_PATH + "reservas";
        if (UtilFiles.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (UtilFiles.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.println("Aún no se ha creado un archivo: " + fileName);
        }
    }

    public void listAll(ReservasVuelosService reservaVuelo) throws Exception {
        ArrayList<String> listReserva = new ArrayList<>();
        int length = 98;
        for (Reserva rs : reservas) {
            listReserva.add(rs.toString());
            ArrayList<ReservaVuelo> reservaV = reservaVuelo.getList();
            for (ReservaVuelo reV : reservaV) {
                if (rs.equals(reV.getReserva())) {
                    listReserva.add("Vuelos:");
                    listReserva.add(reV.toString());
                    listReserva.add(" ".repeat(length));
                }
            }
        }
        String title = "LISTADO DE RESERVAS";
        listReserva.add(0, " ".repeat((length - title.length()) / 2) + title);
        listReserva.add(1, "-".repeat(length));
        listReserva.add("-".repeat(length));
        UtilFiles.writeText(listReserva, fileName + ".txt");
    }

    public ArrayList<Reserva> loadJSON() throws IOException {
        reservas = new ArrayList<>();
        String data = UtilFiles.readText(fileName + ".json");
        JSONArray jsonArrReserva = new JSONArray(data);
        for (int i = 0; i < jsonArrReserva.length(); i++) {
            JSONObject jsonObjecVuelo = jsonArrReserva.getJSONObject(i);
            reservas.add(new Reserva(jsonObjecVuelo));
        }
        return reservas;
    }

    private void loadCSV() throws IOException {

        String textReserva = UtilFiles.readText(fileName + ".csv");
        try (Scanner sc = new Scanner(textReserva).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                LocalDateTime fechaHora = LocalDateTime.parse(sc.next());
                Pasajero pasajeros = pasajero.get(new Pasajero(sc.next(), "", ""));
                Reserva reserva = new Reserva(fechaHora, pasajeros);
                reserva.setCancelado(sc.nextBoolean());
                reservas.add(reserva);
                sc.nextLine();
            }
        }
    }

    public void add(JSONObject json) throws IOException {
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
        Pasajero pasajeros = new Pasajero(json.getString("pasajero"), "", "");
        pasajeros = pasajero.get(pasajeros);
        add(new Reserva(fechaHora, pasajeros));
    }

    public JSONObject get(String paramsReservaVuelo) throws Exception {
        JSONObject json = UtilFiles.paramsToJson(paramsReservaVuelo);
        Pasajero pasajeros = pasajero.get(new Pasajero(json.getString("pasajero"), "", ""));
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
        Reserva reserva = get(new Reserva(fechaHora, pasajeros));
        if (reserva == null) {
            throw new Exception("no se ha encontrado la reserva");
        }
        return getJSON(reserva);
    }

    public JSONObject set(JSONObject json) throws IOException {

        Pasajero pasajero = getPasajero().get(new Pasajero(json.getString("pasajero"), "",
                ""));
        LocalDateTime fecha = LocalDateTime.parse(json.getString("fechaHora"));
        Reserva reserva = new Reserva(fecha, pasajero);
        reserva = get(reserva);
        reserva.setCancelado(json.getBoolean("cancelado"));
        UtilFiles.writeData(reservas, fileName);
        return new JSONObject(reserva);
    }

    public void remove(String params) throws Exception {

        JSONObject json = get(params);
        LocalDateTime fecha = LocalDateTime.parse(json.getString("fechaHora"));
        Pasajero pasajero = new Pasajero(json.getJSONObject("pasajero"));
        Reserva reserva = new Reserva(fecha, pasajero);

        if (UtilFiles.exists(UtilFiles.FILE_PATH + "vuelos-de-reservas", "reserva", reserva)) {
            throw new Exception(String.format(
                    "No se eliminó la reserva = con fecha-hora: %s y  pasajero: %s, debido a que se encuentra en vuelos reservados.",
                    reserva.strFechaHora(), reserva.getPasajero()));
        }
        if (!reservas.remove(reserva)) {
            throw new Exception(String.format(
                    "No se ha econtrado  econtrado la reserva : con fecha-hora : %s y pasajero : %s ",
                    reserva.strFechaHora(), reserva.getPasajero().toString()));
        }
        UtilFiles.writeData(reservas, fileName);
    }

    public void update() throws IOException {
        reservas = new ArrayList<>();
        loadCSV();
        UtilFiles.writeJSON(reservas, fileName + ".json");
    }

    public PasajerosService getPasajero() {
        return pasajero;
    }

    public JSONArray getJSON() throws IOException {
        return new JSONArray(UtilFiles.readText(fileName + ".json"));
    }

    public JSONObject getJSON(Reserva avion) {
        int index = reservas.indexOf(avion);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONObject getJSON(int index) {
        return new JSONObject(reservas.get(index));
    }

    public boolean add(Reserva reserva) throws IOException {
        if (contains(reserva)) {
            throw new ArrayStoreException(String.format("la reserva %s %s ya existe", reserva.getFechaHora(),
                    reserva.getPasajero()));
        }
        boolean ok = reservas.add(reserva);
        UtilFiles.writeData(reservas, fileName);
        return ok;
    }

    public boolean contains(Reserva r) {
        return reservas.contains(r);
    }

    public Reserva get(int index) {
        return reservas.get(index);
    }

    public Reserva get(Reserva reserva) {
        int index = reservas.indexOf(reserva);
        return index > -1 ? reservas.get(index) : null;
    }

    public ArrayList<Reserva> getList() {
        return reservas;
    }

}
