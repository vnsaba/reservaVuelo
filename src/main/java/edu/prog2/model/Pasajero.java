package edu.prog2.model;

import org.json.JSONObject;

public class Pasajero implements IFormatCSV {
    private String identificacion;
    private String nombres;
    private String apellidos;

    public Pasajero() {

    }

    public Pasajero(String identificacion, String nombres, String apellidos) {
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public Pasajero(Pasajero pa) {
        this(pa.identificacion, pa.nombres, pa.apellidos);

    }

    public Pasajero(JSONObject json) {
        this(json.getString("identificacion"),
                json.getString("nombres"),
                json.getString("apellidos"));
    }

    public Pasajero(String json) {
        this(new JSONObject(json));
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Pasajero)) {
            return false;
        }

        Pasajero p = (Pasajero) obj;

        return identificacion.equals(p.identificacion);
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%s%n", identificacion, nombres, apellidos);
    }

    @Override
    public String toString() {
        return String.format("%-10s%-15s%s", identificacion, nombres, apellidos);
    }

}