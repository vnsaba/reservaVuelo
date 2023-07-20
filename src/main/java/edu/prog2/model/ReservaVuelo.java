package edu.prog2.model;

import org.json.JSONObject;

public class ReservaVuelo implements IFormatCSV {
    private boolean checkin;
    private Reserva reserva;
    private Vuelo vuelo;
    private Silla silla;

    public ReservaVuelo() {
    }

    public ReservaVuelo(String json) {
        this(new JSONObject(json));
    }

    public ReservaVuelo(Reserva reserva, Vuelo vuelo, Silla silla) {
        if (!silla.getAvion().equals(vuelo.getAvion())) {
            throw new IllegalArgumentException("La silla reservada no corresponde a una silla del avión del vuelo");
        }
        this.reserva = reserva;
        this.vuelo = vuelo;
        this.silla = silla instanceof SillaEjecutiva ? new SillaEjecutiva((SillaEjecutiva) silla) : new Silla(silla);// composicion
    }

    public ReservaVuelo(JSONObject jsonReservaVuelo) {
        this(
                new Reserva(jsonReservaVuelo.getJSONObject("reserva")),
                new Vuelo(jsonReservaVuelo.getJSONObject("vuelo")),
                jsonReservaVuelo.getJSONObject("silla").has("menu")
                        ? new SillaEjecutiva(jsonReservaVuelo.getJSONObject("silla"))
                        : new Silla(jsonReservaVuelo.getJSONObject("silla")));
    }

    public ReservaVuelo(ReservaVuelo reser) {
        this(reser.reserva, reser.vuelo, reser.silla);
    }

    public String chekReserva() {
        String check = (checkin == false ? "pendiente " : "suspendido");
        return check;
    }

    public boolean getCheckin() {
        return checkin;
    }

    public void setCheckin(boolean checkin) {
        this.checkin = checkin;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Vuelo getVuelo() {
        return vuelo;
    }

    public void setVuelo(Vuelo vuelo) {
        this.vuelo = vuelo;
    }

    public Silla getSilla() {
        return silla;
    }

    public void setSilla(Silla silla) {
        this.silla = silla;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ReservaVuelo)) {
            return false;
        }
        ReservaVuelo rese = (ReservaVuelo) obj;
        return rese.getVuelo().equals(vuelo) && rese.getSilla().equals(silla);

    }

    @Override
    public String toCSV() {
        String sillas = String.format("%s;%s;%s;%s;%s;%s;%s;%s;%b;%b", reserva.getFechaHora(),
                reserva.getPasajero().getIdentificacion(),
                vuelo.getFechaHora(), vuelo.getTrayecto().getOrigen(), vuelo.getTrayecto().getDestino(),
                vuelo.getAvion().getMatricula(),
                silla.getFila(), silla.getColumna(), silla.getDisponible(), getCheckin());

        if (silla instanceof SillaEjecutiva) {
            SillaEjecutiva ejecutiva = (SillaEjecutiva) silla;
            sillas = String.format("%s;%s;%s", sillas, ejecutiva.getMenu(), ejecutiva.getLicor());
        }
        return String.format("%s%n", sillas);
    }

    @Override
    public String toString() {

        String sillas = String.format(
                "\n \tfecha y hora: %s - Estado: %s - chekin: %s %n\tAvion: %s - %s, Silla %s - economica ",
                vuelo.strFechaHora(), reserva.StringCancelado(),
                chekReserva(), vuelo.getAvion().getMatricula(), vuelo.getAvion().getModelo(), silla.getPosicion());
        if (silla instanceof SillaEjecutiva) {
            SillaEjecutiva ejecutiva = (SillaEjecutiva) silla;
            String menu = ejecutiva.getMenu().toString().replaceAll("_", " ");
            String licor = ejecutiva.getLicor().toString().replaceAll("_", " ");
            String silass = sillas.toString().replace("economica", "ejecutiva,");
            sillas = String.format("%s menu: %s, licor:  %s", silass, menu, licor);
        }
        return sillas;
    }
}
