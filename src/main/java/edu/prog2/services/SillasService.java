package edu.prog2.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import edu.prog2.helpers.UtilFiles;
import edu.prog2.model.*;

public class SillasService {

    private ArrayList<Silla> sillas;
    private AvionesService aviones;
    private String fileName;

    public SillasService(AvionesService aviones) throws IOException {
        this.aviones = aviones;
        sillas = new ArrayList<>();
        fileName = UtilFiles.FILE_PATH + "sillas";
        if (UtilFiles.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (UtilFiles.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.println("Aún no se ha creado un archivo: " + fileName);
        }
    }

    public ArrayList<Silla> loadJSON() throws IOException {

        sillas = new ArrayList<>();
        String data = UtilFiles.readText(fileName + ".json");
        JSONArray jsonArrSilla = new JSONArray(data);
        for (int i = 0; i < jsonArrSilla.length(); i++) {
            JSONObject jsonObjectSilla = jsonArrSilla.getJSONObject(i);
            if (jsonObjectSilla.has("licor") && jsonObjectSilla.has("menu")) {
                sillas.add(new SillaEjecutiva(jsonObjectSilla));
            } else {
                sillas.add(new Silla(jsonObjectSilla));
            }

        }
        return sillas;
    }

    private void loadCSV() throws IOException {
        String linea;
        String matricula;
        int fila;
        char columna;
        try (BufferedReader archivo = Files.newBufferedReader(Paths.get(fileName + ".csv"))) {
            while ((linea = archivo.readLine()) != null) {
                String data[] = linea.split(";");

                matricula = data[0];
                fila = Integer.parseInt(data[1]);
                columna = data[2].charAt(0);
                Avion avion = aviones.get(new Avion(matricula, ""));

                if (data.length == 7) {
                    Menu menu = Menu.valueOf(data[5]);
                    Licor licor = Licor.valueOf(data[6]);
                    sillas.add(new SillaEjecutiva(fila, columna, menu, licor, avion));
                } else if (data.length == 5) {
                    sillas.add(new Silla(fila, columna, avion));
                } else {
                    throw new IOException("Se esperaban 5 o 7 datos por línea");
                }

            }
        }
    }

    @SuppressWarnings("unchecked")
    public void listAll() throws Exception {
        int length = 42;
        String title = "LISTADO DE  SILLAS";
        ArrayList<String> listSilla = (ArrayList<String>) (ArrayList<?>) (sillas);

        listSilla.add(0, " ".repeat((length - title.length()) / 2) + title);
        listSilla.add(1, "-".repeat(length));
        listSilla.add(2, "SILLAS DEL AVION: ");
        listSilla.add("-".repeat(length));
        listSilla.add("-".repeat(length));
        UtilFiles.writeText(listSilla, fileName + ".txt");
    }

    public JSONObject numberOfSeats(String matricula) throws Exception {
        JSONObject data = new JSONObject();

        Avion avion = getAviones().get(new Avion(matricula, ""));
        int totalEjecutivas = 0;
        int totalEconomicas = 0;
        for (Silla silla : sillas) {
            if (silla.getAvion().equals(avion)) {
                if (silla instanceof SillaEjecutiva) {
                    totalEjecutivas++;
                } else {
                    totalEconomicas++;
                }
            }
        }
        data.put("totalSillas", new JSONObject().put("ejecutivas", totalEjecutivas).put("economicas", totalEconomicas));
        data.put("matricula", matricula);
        data.put("modelo", avion.getModelo());

        return data;
    }

    public JSONArray aircraftWithNumberSeats() throws Exception {

        JSONArray data = new JSONArray();
        for (Avion av : aviones.getList()) {
            data.put(numberOfSeats(av.getMatricula()));
        }
        return data;
    }

    public JSONObject get(String paramsSilla) throws Exception {
        JSONObject json = UtilFiles.paramsToJson(paramsSilla);

        Avion avion = new Avion(json.getString("avion"), "");

        int fila = Integer.parseInt(json.getString("fila"));

        char columna = json.getString("columna").charAt(0);
        Silla silla = get(new Silla(fila, columna, avion));
        if (silla == null) {
            throw new Exception("no se ha encontrado la silla");
        }
        return getJSON(silla);
    }

    public JSONArray getSillasAvion(String params) {
        JSONArray sillasAvion = new JSONArray();
        Avion avion = getAviones().get(new Avion(params, ""));
        for (Silla s : sillas) {
            if (s.getAvion().equals(avion)) {
                sillasAvion.put(new JSONObject(s));
            }
        }
        return sillasAvion;
    }

    public JSONObject set(JSONObject json) throws IOException {
        Avion avion = aviones.get(new Avion(json.getString("avion"), ""));
        Silla silla = new Silla(json.getInt("fila"), json.getString("columna").charAt(0), avion);
        silla = get(silla);
        silla.setDisponible(json.getBoolean("disponible"));

        UtilFiles.writeData(sillas, fileName);
        return new JSONObject(silla);
    }

    public void remove(String params) throws Exception {

        JSONObject json = get(params);

        Avion avion = new Avion(json.getJSONObject("avion"));

        String columna = json.getString("posicion");
        char c = columna.substring(columna.length() - 1, columna.length()).charAt(0);

        int fila = json.getInt("fila");
        Silla silla = new Silla(fila, c, avion);

        if (UtilFiles.exists(UtilFiles.FILE_PATH + "vuelos-de-reservas", "silla", silla)) {
            throw new Exception(String.format(
                    "No se eliminó la silla %s, está en vuelos reservados", silla.getPosicion()));
        } else if (!sillas.remove(silla)) {
            throw new Exception(String.format("No se encontró la silla %s", silla.getPosicion()));
        }

        UtilFiles.writeData(sillas, fileName);
    }

    public void update() throws IOException {
        sillas = new ArrayList<>();
        loadCSV();
        UtilFiles.writeJSON(sillas, fileName + ".json");
    }

    public AvionesService getAviones() {
        return aviones;
    }

    public JSONArray getJSON() throws IOException {
        return new JSONArray(UtilFiles.readText(fileName + ".json"));
    }

    public JSONObject getJSON(Silla silla) {
        int index = sillas.indexOf(silla);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONObject getJSON(int index) {
        return new JSONObject(sillas.get(index));
    }

    public void create(
            String matriculaAvion, int ejecutivas, int economicas) throws IOException {
        Avion avion = aviones.get(new Avion(matriculaAvion, ""));
        create(avion, ejecutivas, economicas);
    }

    public void create(Avion avion, int ejecutivas, int economicas) throws IOException {
        create(avion, ejecutivas, new char[] { 'A', 'B', 'C', 'D' }, 1);
        create(avion, economicas, new char[] { 'A', 'B', 'C', 'D', 'E', 'F' },
                ejecutivas / 4 + 1);
        UtilFiles.writeData(sillas, fileName);
        System.out.println("Creadas las sillas del avión: " + avion.getMatricula());
    }

    public void create(Avion avion, int totalSillas, char[] columnas, int inicio) throws IOException {

        if (columnas.length == 4) {
            if (totalSillas % 4 != 0) {
                throw new IndexOutOfBoundsException("El número de filas  ejecutivas debe ser un múltiplo de 4");
            }
            totalSillas /= 4;
            for (int fila = inicio; fila <= totalSillas; fila++) {
                for (char columna : columnas) {
                    sillas.add(new SillaEjecutiva(fila, columna, Menu.INDEFINIDO, Licor.NINGUNO, avion));
                }
            }
        } else {
            if (totalSillas % 6 != 0) {
                throw new IndexOutOfBoundsException("El número de filas  economicas debe ser un múltiplo de 6");
            }
            totalSillas /= 6;
            for (int fila = inicio; fila < totalSillas + inicio; fila++) {
                for (char columna : columnas) {
                    sillas.add(new Silla(fila, columna, avion));
                }
            }
        }
    }

    public boolean add(Silla silla) throws IOException {
        if (contains(silla)) {
            throw new IOException(
                    String.format("No agregada, la silla %s ya existe%n", silla.getPosicion()));
        }

        boolean ok = sillas.add(silla);
        return ok;
    }

    public Silla get(int index) {
        return sillas.get(index);
    }

    public Silla get(Silla silla) {
        int index = sillas.indexOf(silla);
        return index > -1 ? sillas.get(index) : null;
    }

    public boolean contains(Silla s) {
        return sillas.contains(s);
    }

    public ArrayList<Silla> getList() {
        return sillas;
    }

}
