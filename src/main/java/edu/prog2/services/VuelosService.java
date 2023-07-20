package edu.prog2.services;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import edu.prog2.helpers.UtilFiles;
import edu.prog2.model.Avion;
import edu.prog2.model.Trayecto;
import edu.prog2.model.Vuelo;

public class VuelosService {
    private ArrayList<Vuelo> vuelos;
    private String fileName;
    private TrayectosService trayectos;
    private AvionesService aviones;

    public VuelosService(TrayectosService trayectos, AvionesService aviones) throws IOException {
        this.trayectos = trayectos;
        this.aviones = aviones;
        vuelos = new ArrayList<>();

        fileName = UtilFiles.FILE_PATH + "vuelos";
        if (UtilFiles.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (UtilFiles.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            System.out.println("Aún no se ha creado un archivo: " + fileName);
        }
    }

    public ArrayList<Vuelo> loadJSON() throws IOException {
        vuelos = new ArrayList<>();
        String data = UtilFiles.readText(fileName + ".json");
        JSONArray jsonArrVuelo = new JSONArray(data);
        for (int i = 0; i < jsonArrVuelo.length(); i++) {
            JSONObject jsonObjecVuelo = jsonArrVuelo.getJSONObject(i);
            vuelos.add(new Vuelo(jsonObjecVuelo));
        }
        return vuelos;
    }

    private void loadCSV() throws IOException {
        String text = UtilFiles.readText(fileName + ".csv");
        try (Scanner sc = new Scanner(text).useDelimiter(";|[\n]+|[\r\n]+")) {
            while (sc.hasNext()) {
                LocalDateTime fechaHora = LocalDateTime.parse(sc.next());
                String origen = sc.next();
                String destino = sc.next();
                Trayecto trayecto = trayectos.get(new Trayecto(origen, destino));
                Avion avion = aviones.get(new Avion(sc.next(), ""));
                Vuelo vuelo = new Vuelo(fechaHora, trayecto, avion);
                vuelo.setCancelado(sc.nextBoolean());
                vuelos.add(new Vuelo(vuelo));
                sc.nextLine();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void listAll() throws Exception {
        ArrayList<String> list = (ArrayList<String>) (ArrayList<?>) (vuelos);
        int length = 72;
        String title = "LISTADO DE VUELOS";

        list.add(0, " ".repeat((length - title.length()) / 2) + title);
        list.add(1, "-".repeat(length));
        list.add(2, "MATR.     ORIGEN         DESTINO            PRECIO  DUR.  FECHA Y HORA");
        list.add(3, "-".repeat(length));
        list.add("-".repeat(length));

        UtilFiles.writeText(list, fileName + ".txt");

    }

    public JSONObject get(String paramsVuelo) throws Exception {

        JSONObject json = UtilFiles.paramsToJson(paramsVuelo);
        Avion avion = aviones.get(new Avion(json.getString("avion"), ""));
        Trayecto trayecto = trayectos
                .get(new Trayecto(json.getString("origen"), json.getString("destino"), Duration.ZERO, 0.0));
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
        Vuelo vuelo = get(new Vuelo(fechaHora, trayecto, avion));
        if (vuelo == null) {
            throw new Exception("no se ha encontrado el vuelo");
        }
        return getJSON(vuelo);
    }

    public JSONArray getVuelos(String params) throws IOException {
        // buscar vuelos a partir de una Fecha con un origen y un destino determinado
        JSONObject json = UtilFiles.paramsToJson(params);
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
        String origen = json.getString("origen");
        System.out.println(origen);
        String destino = json.getString("destino");
        JSONArray vuelosEncontrados = new JSONArray();
        for (Vuelo v : vuelos) {
            if (v.getFechaHora().equals(fechaHora) || v.getFechaHora().isAfter(fechaHora)
                    && v.getTrayecto().getOrigen().equals(origen) && v.getTrayecto().getDestino().equals(destino)) {
                vuelosEncontrados.put(new JSONObject(v));
            }
            ;

        }

        return vuelosEncontrados;
    }

    public JSONObject set(JSONObject json) throws IOException {
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
        Trayecto trayecto = getTrayectos()
                .get(new Trayecto(json.getString("origen"), json.getString("destino"), Duration.ZERO, 0.0));
        Avion avion = getAviones().get(new Avion("avion", ""));
        Vuelo vuelo = new Vuelo(fechaHora, trayecto, avion);
        vuelo = get(vuelo);
        vuelo.setCancelado(json.getBoolean("cancelado"));
        System.out.println(vuelo.getCancelado());
        UtilFiles.writeData(vuelos, fileName);
        return new JSONObject(vuelo);
    }

    public void add(JSONObject json) throws IOException {
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
        Trayecto trayecto = new Trayecto(json.getString("origen"), json.getString("destino"), Duration.ZERO, 0.0);
        trayecto = trayectos.get(trayecto);
        Avion avion = aviones.get(new Avion(json.getString("avion"), ""));
        add(new Vuelo(fechaHora, trayecto, avion));
    }

    public void remove(String params) throws Exception {

        JSONObject json = get(params);
        LocalDateTime fechaHora = LocalDateTime.parse(json.getString("fechaHora"));
        Trayecto trayecto = new Trayecto(json.getJSONObject("trayecto"));
        Avion avion = new Avion(json.getJSONObject("avion"));
        Vuelo vuelo = new Vuelo(fechaHora, trayecto, avion);
        if (UtilFiles.exists(UtilFiles.FILE_PATH + "vuelos-de-reservas", "vuelo", vuelo)) {
            throw new Exception(String.format(
                    "No se ha eliminado el vuelo = con fecha-hora : %s, origen %s, destino : %s, Avion-matricula : %s debido a que  está en vuelos reservados",
                    vuelo.strFechaHora(),
                    trayecto.getOrigen(), trayecto.getDestino(), avion.getMatricula()));
        }

        if (!vuelos.remove(vuelo)) {
            throw new Exception(String.format(
                    "No se encontró el Vuelo = con fecha-hora : %s, origen %s, destino : %s, Avion-matricula : %s, debido a que  está en vuelos reservados",
                    vuelo.strFechaHora(),
                    trayecto.getOrigen(), trayecto.getDestino(), avion.getMatricula()));
        }

        UtilFiles.writeData(vuelos, fileName);
    }

    public void update() throws IOException {
        vuelos = new ArrayList<>();
        loadCSV();
        UtilFiles.writeJSON(vuelos, fileName + ".json");
    }

    public AvionesService getAviones() {
        return aviones;
    }

    public TrayectosService getTrayectos() {
        return trayectos;
    }

    public JSONArray getJSON() throws IOException {
        return new JSONArray(UtilFiles.readText(fileName + ".json"));
    }

    public JSONObject getJSON(int index) {
        return new JSONObject(vuelos.get(index));
    }

    public JSONObject getJSON(Vuelo vuelo) {
        int index = vuelos.indexOf(vuelo);
        return index > -1 ? getJSON(index) : null;
    }

    public boolean add(Vuelo vuelo) throws IOException {
        if (contains(vuelo)) {
            throw new ArrayStoreException(String.format("El vuelo %s %s %s ya existe", vuelo.strFechaHora(),
                    vuelo.getAvion(), vuelo.getTrayecto()));
        }
        boolean ok = vuelos.add(vuelo);
        UtilFiles.writeData(vuelos, fileName);
        return ok;
    }

    public boolean contains(Vuelo v) {
        return vuelos.contains(v);
    }

    public Vuelo get(int index) {
        return vuelos.get(index);
    }

    public Vuelo get(Vuelo vuelo) {
        int index = vuelos.indexOf(vuelo);
        return index > -1 ? vuelos.get(index) : null;
    }

    public ArrayList<Vuelo> getList() {
        return vuelos;
    }

}
