package edu.prog2.model;

import java.time.Duration;

import org.json.JSONObject;

public class Trayecto implements IFormatCSV {
    private String origen;
    private String destino;
    private Duration duracion;
    private double costo;

    public Trayecto() {
    }

    public Trayecto(String json) {
        this(new JSONObject(json));
    }

    public Trayecto(String origen, String destino, Duration duracion, double costo) {
        this.origen = origen;
        this.destino = destino;
        this.duracion = duracion;
        this.costo = costo;

    }

    public Trayecto(String origen, String destino) {
        this(origen, destino, null, 0.0);
    }

    public Trayecto(JSONObject jsonTrayecto) {
        this(jsonTrayecto.getString("origen"),
                jsonTrayecto.getString("destino"),
                Duration.parse(jsonTrayecto.getString("duracion")),
                jsonTrayecto.getDouble("costo"));
    }

    public Trayecto(Trayecto tr) {
        this(tr.origen, tr.destino, tr.duracion, tr.costo);
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = Duration.parse(duracion);
    }
    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
    }
    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String strDuracion() {
        long hh = duracion.toHours();
        long mm = duracion.toMinutesPart();
        return String.format("%02d:%02d", hh, mm);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Trayecto)) {
            return true;
        }
        Trayecto tr = (Trayecto) obj;
        return (origen.equals(tr.origen) && destino.equals(tr.destino));
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%8.0f;%s%n", origen, destino, costo, duracion);
    }

    @Override
    public String toString() {
        return String.format("%-12s %-13s %8.0f %-15s ", origen, destino, costo, strDuracion());
    }

}
