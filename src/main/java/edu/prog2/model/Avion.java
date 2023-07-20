package edu.prog2.model;

import org.json.JSONObject;

public class Avion implements IFormatCSV {
    private String matricula;
    private String modelo;

    public Avion() {
    }

    public Avion(String json) {
        this(new JSONObject(json));
    }

    public Avion(String matricula, String modelo) {
        this.matricula = matricula;
        this.modelo = modelo;
    }

    public Avion(JSONObject jsonAvion) {
        this(jsonAvion.getString("matricula"), jsonAvion.getString("modelo"));
    }

    public Avion(Avion av) {
        this(av.matricula, av.modelo);
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Avion)) {
            return false;
        }

        Avion av = (Avion) obj;
        return matricula.equals(av.matricula);
    }

    public String toCSV() {
        return String.format("%s;%s%n", matricula, modelo);
    }

    @Override
    public String toString() {
        return String.format("%-10s%-15s", matricula, modelo);
    }
}
