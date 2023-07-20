package edu.prog2.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.json.JSONObject;

public class Reserva implements IFormatCSV {
    private LocalDateTime fechaHoraReserva;
    private boolean cancelado;
    private Pasajero pasajero;

    public Reserva() {
    }

    public Reserva(String json) {
        this(new JSONObject(json));
    }

    public Reserva(LocalDateTime fechaHora, Pasajero pasajero) {
        this.fechaHoraReserva = fechaHora;
        this.cancelado = false;
        this.pasajero = pasajero;
    }

    public Reserva(LocalDateTime fechaHora) {
        this.fechaHoraReserva = fechaHora;
    }

    public Reserva(JSONObject jsonReserva) {
        this(
                LocalDateTime.parse(jsonReserva.getString("fechaHora")),
                new Pasajero(jsonReserva.getJSONObject("pasajero")));

    }

    public Reserva(Reserva r) {
        this(r.fechaHoraReserva, r.pasajero);
    }

    public String StringCancelado() {
        String cancelado = this.cancelado == false ? "vigente" : "cancelado";
        return cancelado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHoraReserva;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHoraReserva = fechaHora;
    }

    public boolean getCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public String strFechaHora() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return fechaHoraReserva.format(formatter);
    }

    @Override

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Reserva)) {
            return false;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;

        Reserva res = (Reserva) obj;

        return res.getPasajero().equals(pasajero)
                && res.getFechaHora().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).format(dtf)
                        .equals(fechaHoraReserva.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).format(dtf));
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%b%n", fechaHoraReserva, pasajero.getIdentificacion(), getCancelado());
    }

    @Override
    public String toString() {
        return String.format("\nfecha y hora de la reserva: %-2s  - Estado: %-2s%n pasajero: %-2s %2s %-2s%n",
                strFechaHora(), StringCancelado(), pasajero.getIdentificacion(), pasajero.getNombres(),
                pasajero.getApellidos());
    }

}
