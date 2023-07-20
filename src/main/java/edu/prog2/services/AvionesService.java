package edu.prog2.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import edu.prog2.helpers.UtilFiles;
import edu.prog2.model.Avion;
import edu.prog2.model.Silla;

public class AvionesService {
    private ArrayList<Avion> aviones;
    private String fileName;

    public AvionesService() throws IOException {
        aviones = new ArrayList<>();
        fileName = UtilFiles.FILE_PATH + "Aviones";
        if (UtilFiles.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (UtilFiles.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.println("Aún no se ha creado un archivo: " + fileName);
        }
    }

    public ArrayList<Avion> loadJSON() throws IOException {
        aviones = new ArrayList<>();
        String data = UtilFiles.readText(fileName + ".json");
        JSONArray jsonArrAvion = new JSONArray(data);
        for (int i = 0; i < jsonArrAvion.length(); i++) {
            JSONObject jsonObjectAvion = jsonArrAvion.getJSONObject(i);
            aviones.add(new Avion(jsonObjectAvion));
        }
        return aviones;
    }

    private void loadCSV() throws IOException {
        String textAvion = UtilFiles.readText(fileName + ".csv");
        try (Scanner sc = new Scanner(textAvion).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                String matricula = sc.next();
                String modelo = sc.next();
                add(new Avion(matricula, modelo));
                sc.nextLine();
            }
        }
    }

    public void listAll(SillasService silla) throws Exception {
        int length = 62;
        String title = "LISTADO DE AVIONES";
        ArrayList<String> listAvion = new ArrayList<>();
        for (Avion avion : aviones) {
            listAvion.add(avion.toString());
            ArrayList<Silla> sillas = silla.getList();
            for (Silla sillaAvion : sillas) {
                if (avion.equals(sillaAvion.getAvion())) {
                    listAvion.add(sillaAvion.toString());
                }
            }
        }
        listAvion.add(0, " ".repeat((length - title.length()) / 2) + title);
        listAvion.add(1, "-".repeat(length));
        listAvion.add(2, "MATRICULA   MODELO");
        listAvion.add(3, "-".repeat(length));
        listAvion.add("-".repeat(length));
        UtilFiles.writeText(listAvion, fileName + ".txt");
    }

    public JSONObject set(String modelo, JSONObject json) throws IOException {
        Avion avion = new Avion(json);
        avion.setMatricula(modelo);
        int index = aviones.indexOf(avion);
        aviones.set(index, avion);
        UtilFiles.writeData(aviones, fileName);
        return new JSONObject(avion);
    }

     
    public void remove(String matricula) throws Exception {
        Avion avion = new Avion(matricula, "");
        if (UtilFiles.exists(UtilFiles.FILE_PATH + "sillas", "avion", avion)) {

            throw new Exception(String.format(
                    "No se eliminó el el avion con matricula:  %s, debido a que se encuentra en sillas ",
                    matricula));

        }
        if ((UtilFiles.exists(UtilFiles.FILE_PATH + "vuelos", "avion", avion))) {

            throw new Exception(String.format(
                    "No se eliminó el el avion con matricula:  %s, debido a que se encuentra en  vuelos",
                    matricula));

        }
        if (!aviones.remove(avion)) {
            throw new Exception(String.format(
                    "No se ha econtrado el avion con matricula %s", matricula));
        }
        UtilFiles.writeData(aviones, fileName);
    }
 

    public void update() throws IOException {
        aviones = new ArrayList<>();
        loadCSV();
        UtilFiles.writeJSON(aviones, fileName + ".json");
    }

    public JSONArray getJSON() throws IOException {
        return new JSONArray(UtilFiles.readText(fileName + ".json"));
    }

    public JSONObject getJSON(Avion avion) {
        int index = aviones.indexOf(avion);
        return index > -1 ? getJSON(index) : null;
    }

    public JSONObject getJSON(int index) {
        return new JSONObject(aviones.get(index));
    }

    public boolean add(Avion avion) throws IOException {
        if (contains(avion)) {
            throw new ArrayStoreException(String.format("El avion %s ya existe", avion.getMatricula()));
        }
        boolean ok = aviones.add(avion);
        UtilFiles.writeData(aviones, fileName);
        return ok;
    }

    public Avion get(int index) {
        return aviones.get(index);
    }

    public Avion get(Avion avion) {
        int index = aviones.indexOf(avion);
        return index > -1 ? aviones.get(index) : null;
    }

    public boolean contains(Avion p) {
        return aviones.contains(p);
    }

    public ArrayList<Avion> getList() {
        return aviones;
    }

}
