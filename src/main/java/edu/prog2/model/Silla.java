package edu.prog2.model;

import java.util.InputMismatchException;

import org.json.JSONObject;

public class Silla implements IFormatCSV {
    protected int fila;
    protected char columna;
    protected Avion avion;
    protected Ubicacion ubicacion;
    protected boolean disponible;

    public Silla() {

    }

    public Silla(int fila, char columna, Avion avion) {
        if (columna == 'A' || columna == 'B') {

            ubicacion = Ubicacion.VENTANA;

        } else if (columna == 'E' || columna == 'D') {

            ubicacion = Ubicacion.CENTRAL;

        } else if (columna == 'C' || columna == 'F') {

            ubicacion = Ubicacion.PASILLO;
        } else {
            throw new InputMismatchException("columna erronea");

        }
        disponible = true;
        this.avion = avion;
        this.fila = fila;
        this.columna = columna;

    }

    public Silla(String json) {
        this(new JSONObject(json));
    }

    public Silla(Silla s) {
        this(s.fila, s.columna, s.avion);
    }

    public Silla(JSONObject jsonSilla) {
        this(jsonSilla.getInt("fila"), jsonSilla.getString("columna").charAt(0),
                new Avion(jsonSilla.getJSONObject("avion")));
    }

    public String verificarAsiento() {
        String ver = (this.disponible == true) ? "SI" : "NO";
        return ver;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public char getColumna() {
        return columna;
    }

    public void setColumna(char columna) {
        this.columna = columna;
    }

 

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getPosicion() {
        return String.format("%02d%c", fila, columna);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Silla)) {
            return false;
        }
        Silla p = (Silla) obj;
        return fila == p.fila && columna == p.columna && avion.equals(p.avion);
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%s;%s;%b%n", avion.getMatricula(), fila, columna, ubicacion, getDisponible());
    }

    public String toString() {
        return String.format(" %-10s  %-10s  %-10s ", getPosicion(), ubicacion, verificarAsiento());
    }

}
