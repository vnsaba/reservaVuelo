package edu.prog2.services;

import java.util.Scanner;//leer desde el archivo de pasajeros cada uno de los token,

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import edu.prog2.model.Pasajero;
import edu.prog2.helpers.UtilFiles;

public class PasajerosService {

    private ArrayList<Pasajero> pasajeros;
    private String fileName;

    public PasajerosService() throws IOException {
        pasajeros = new ArrayList<>();
        fileName = UtilFiles.FILE_PATH + "pasajeros";

        if (UtilFiles.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (UtilFiles.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.println("Aún no se ha creado un archivo: " + fileName);
        }
    }

    public ArrayList<Pasajero> loadJSON() throws IOException {
        String data = UtilFiles.readText(fileName + ".json");
        pasajeros = new ArrayList<>();

        JSONArray jsonArrPasajero = new JSONArray(data);

        for (int i = 0; i < jsonArrPasajero.length(); i++) {
            JSONObject jsonObjPasajero = jsonArrPasajero.getJSONObject(i);
            pasajeros.add(new Pasajero(jsonObjPasajero));
        }

        return pasajeros;
    }

    private void loadCSV() throws IOException {
        String textPasajero = UtilFiles.readText(fileName + ".csv");
        try (Scanner sc = new Scanner(textPasajero).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String identificacion = sc.next();
                String nombres = sc.next();
                String apellidos = sc.next();
                pasajeros.add(new Pasajero(identificacion, nombres, apellidos));
                sc.nextLine();
            }
        }
    }

    public void listAll() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        for (Pasajero pasajero : pasajeros) {
            list.add(pasajero.toString());
        }
        int length = 42;
        String title = "LISTADO DE PASAJEROS";
        list.add(0, " ".repeat((length - title.length()) / 2) + title);
        list.add(1, "-".repeat(length));
        list.add(2, "IDENTi.   NOMBRE         APELLIDO");
        list.add(3, "-".repeat(length));
        list.add("-".repeat(length));
        UtilFiles.writeText(list, fileName + ".txt");
    }

    public JSONObject set(String identificacion, JSONObject json) throws IOException {
        Pasajero pasajero = new Pasajero(json);
        pasajero.setIdentificacion(identificacion);
        int index = pasajeros.indexOf(pasajero);
        pasajeros.set(index, pasajero);
        UtilFiles.writeData(pasajeros, fileName);
        return new JSONObject(pasajero);
    }

    public void remove(String identificacion) throws Exception {
        Pasajero pasajero = new Pasajero(identificacion, "", "");
        if (UtilFiles.exists(UtilFiles.FILE_PATH + "reservas", "pasajero", pasajero)) {
            throw new Exception(String.format(
                    "No se eliminó el pasajero con identificacion : %s, debido a que ha realizado una reserva",
                    identificacion));
        }
        if (!pasajeros.remove(pasajero)) {
            throw new Exception(String.format(
                    "No se ha econtrado  el pasajero con identificación %s", identificacion));
        }
        UtilFiles.writeData(pasajeros, fileName);
    }

    public JSONArray getJSON() throws IOException {
        return new JSONArray(UtilFiles.readText(fileName + ".json"));
    }

    public JSONObject getJSON(Pasajero pasajero) {
        int index = pasajeros.indexOf(pasajero);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONObject getJSON(int index) {
        return new JSONObject(pasajeros.get(index));
    }

    public boolean add(Pasajero pasajero) throws IOException {
        if (contains(pasajero)) {
            throw new ArrayStoreException(
                    String.format("No agregado, el pasajero %s ya existe%n", pasajero.getIdentificacion()));
        }
        boolean ok = pasajeros.add(pasajero);
        UtilFiles.writeData(pasajeros, fileName);
        return ok;
    }

    public boolean contains(Pasajero p) {

        for (Pasajero pasajero : pasajeros) {
            if (p.equals(pasajero)) {
                return true;
            }
        }
        return false;

    }

    public Pasajero get(int index) {
        return pasajeros.get(index);
    }

    public Pasajero get(Pasajero pasajero) {
        int index = pasajeros.indexOf(pasajero);
        return index > -1 ? pasajeros.get(index) : null;
    }

    public ArrayList<Pasajero> getList() {
        return pasajeros;
    }

}