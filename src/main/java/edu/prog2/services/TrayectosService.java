package edu.prog2.services;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.UtilFiles;
import edu.prog2.model.Trayecto;

public class TrayectosService {

    private ArrayList<Trayecto> trayectos;
    private String fileName;

    public TrayectosService() throws IOException {
        trayectos = new ArrayList<>();
        fileName = UtilFiles.FILE_PATH + "trayectos";
        if (UtilFiles.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (UtilFiles.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.println("Aún no se ha creado un archivo: " + fileName);
        }
    }

    public ArrayList<Trayecto> loadJSON() throws IOException {
        trayectos = new ArrayList<>();
        String data = UtilFiles.readText(fileName + ".json");
        JSONArray jsonArrTrayecto = new JSONArray(data);
        for (int i = 0; i < jsonArrTrayecto.length(); i++) {
            JSONObject jsonObjecTrayecto = jsonArrTrayecto.getJSONObject(i);
            trayectos.add(new Trayecto(jsonObjecTrayecto));
        }
        return trayectos;
    }

    private void loadCSV() throws IOException {
        String text = UtilFiles.readText(fileName + ".csv");
        try (Scanner sc = new Scanner(text).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String origen = sc.next();
                String destino = sc.next();
                double costo = Double.valueOf(sc.next());
                Duration duracion = Duration.parse(sc.next());
                trayectos.add(new Trayecto(origen, destino, duracion, costo));
                sc.nextLine();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void listAll() throws Exception {
        ArrayList<String> listTrayecto = (ArrayList<String>) (ArrayList<?>) (trayectos);
        int length = 46;
        String title = "LISTADO DE TRAYECTOS";
        listTrayecto.add(0, " ".repeat((length - title.length()) / 2) + title);
        listTrayecto.add(1, "-".repeat(length));
        listTrayecto.add(2, "ORIGEN       DESTINO         COSTO   DURA.");
        listTrayecto.add(3, "-".repeat(length));
        listTrayecto.add("-".repeat(length));
        UtilFiles.writeText(listTrayecto, fileName + ".txt");
    }

    public JSONObject get(String paramsTrayecto) throws Exception {

        JSONObject json = UtilFiles.paramsToJson(paramsTrayecto);
        String origen = json.getString("origen");
        String destino = json.getString("destino");
        Trayecto trayecto = get(new Trayecto(origen, destino, Duration.ZERO, 0.0));

        if (trayecto == null) {
            throw new Exception(String.format("No se encontró el trayecto "));
        }

        return getJSON(trayecto);
    }

    public JSONObject set(JSONObject json) throws IOException {
        Trayecto trayecto = new Trayecto(
                json.getString("origen"), json.getString("destino"), Duration.ZERO, 0.0);
        trayecto = get(trayecto);
        trayecto.setCosto(json.getDouble("costo"));
        trayecto.setDuracion(Duration.parse(json.getString("duracion")));
        UtilFiles.writeData(trayectos, fileName);
        return new JSONObject(trayecto);
    }

    public void remove(String params) throws Exception {
        JSONObject json = get(params);
        Trayecto trayecto = new Trayecto(
                json.getString("origen"), json.getString("destino"), Duration.ZERO, 0.0);
        if (UtilFiles.exists(UtilFiles.FILE_PATH + "vuelos", "trayecto", trayecto)) {
            throw new Exception(String.format(
                    "No se eliminó el trayecto : con origen : %s y  destino : %s, debido a que se encuentra  en  vuelos.",
                    trayecto.getOrigen(), trayecto.getDestino()));
        } else if (!trayectos.remove(trayecto)) {
            throw new Exception(String.format("No se encontró el trayecto %s", trayecto));
        }

        UtilFiles.writeData(trayectos, fileName);
    }

    public void update() throws IOException {
        trayectos = new ArrayList<>();
        loadCSV();
        UtilFiles.writeJSON(trayectos, fileName + ".json");
    }

    public JSONArray getJSON() throws IOException {
        return new JSONArray(UtilFiles.readText(fileName + ".json"));
    }

    public JSONObject getJSON(Trayecto trayecto) {
        int index = trayectos.indexOf(trayecto);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONObject getJSON(int index) {
        return new JSONObject(trayectos.get(index));
    }

    public boolean add(Trayecto trayecto) throws IOException {
        if (contains(trayecto)) {
            throw new ArrayStoreException(String.format("El trayecto %s-%s ya existe",
                    trayecto.getOrigen(), trayecto.getDestino()));
        }
        boolean ok = trayectos.add(trayecto);
        UtilFiles.writeData(trayectos, fileName);
        return ok;
    }

    public Trayecto get(int index) {
        return trayectos.get(index);
    }

    public Trayecto get(Trayecto trayecto) {
        int index = trayectos.indexOf(trayecto);
        return index > -1 ? trayectos.get(index) : null;
    }

    public boolean contains(Trayecto p) {
        return trayectos.contains(p);
    }

    public ArrayList<Trayecto> getList() {
        return trayectos;
    }

}
