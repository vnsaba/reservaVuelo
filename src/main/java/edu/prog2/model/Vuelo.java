package edu.prog2.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.json.JSONObject;

public class Vuelo implements IFormatCSV {
    private LocalDateTime fechaHoraVuelo;
    private boolean cancelado;
    private Trayecto trayecto;
    private Avion avion;

    public Vuelo() {

    }

    public Vuelo(String json) {
        this(new JSONObject(json));
    }

    public Vuelo(LocalDateTime fechaHora, Trayecto trayecto, Avion avion) {
        this.cancelado = false;
        this.fechaHoraVuelo = fechaHora;
        this.trayecto = new Trayecto(trayecto);
        this.avion = avion;
    }

    public Vuelo(JSONObject jsonVuelo) {
        this(
                LocalDateTime.parse(jsonVuelo.getString("fechaHora")),
                new Trayecto(jsonVuelo.getJSONObject("trayecto")),
                new Avion(jsonVuelo.getJSONObject("avion")));
    }

    public Vuelo(LocalDateTime fechaHora) {
        this.fechaHoraVuelo = fechaHora;
    }

    public Vuelo(Avion avion) {
        this.avion = avion;
    }

    public Vuelo(Vuelo vue) {
        this(vue.fechaHoraVuelo, vue.trayecto, vue.avion);
    }

    public LocalDateTime getFechaHora() {
        return fechaHoraVuelo;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHoraVuelo = fechaHora;
    }

    public String getCancelado2() {
        String cancelado = this.cancelado == false ? "NO" : "SI";
        return cancelado;
    }

    public boolean getCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public Trayecto getTrayecto() {
        return trayecto;
    }

    public void setTrayecto(Trayecto trayecto) {
        this.trayecto = trayecto;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public String strFechaHora() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return fechaHoraVuelo.format(formatter);
    }

    @Override
    public boolean equals(Object obj) {
 

        if (this == obj)
            return true;
        if (!(obj instanceof Vuelo)) {
            return false;
        }
        Vuelo vue = (Vuelo) obj;
        return getFechaHora().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).equals(
                vue.fechaHoraVuelo.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)) && vue.trayecto.equals(trayecto);
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%s;%s;%b%n", fechaHoraVuelo, trayecto.getOrigen(),
                trayecto.getDestino(), avion.getMatricula(), cancelado);
    }

    @Override
    public String toString() {

        return String.format("%-9s %-14s %-11s %12.0f %6s %10s", avion.getMatricula(), trayecto.getOrigen(),
                trayecto.getDestino(), getTrayecto().getCosto(), trayecto.strDuracion(), strFechaHora());
    }
}
