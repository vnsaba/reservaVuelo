package edu.prog2.model;

import org.json.JSONObject;

public class SillaEjecutiva extends Silla {

    private Menu menu;
    private Licor licor;

    public SillaEjecutiva() {

    }

    public SillaEjecutiva(String json) {
        this(new JSONObject(json));
    }

    public SillaEjecutiva(int fila, char columna, Menu menu, Licor licor, Avion avion) {
        super(fila, columna, avion);
        ubicacion = (columna == 'B' || columna == 'C') ? Ubicacion.PASILLO : Ubicacion.VENTANA;
        this.menu = menu;
        this.licor = licor;
    }

    public SillaEjecutiva(JSONObject jsonEjecutiva) {
        this(
                jsonEjecutiva.getInt("fila"), jsonEjecutiva.getString("columna").charAt(0),
                jsonEjecutiva.getEnum(Menu.class, "menu"), jsonEjecutiva.getEnum((Licor.class), "licor"),
                new Avion(jsonEjecutiva.getJSONObject("avion")));
    }

    public SillaEjecutiva(SillaEjecutiva s) {
        this(s.fila, s.columna, s.menu, s.licor, s.avion);
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Licor getLicor() {
        return licor;
    }

    public void setLicor(Licor licor) {
        this.licor = licor;
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%s%n", super.toCSV().substring(0, super.toCSV().length() - 2), menu, licor);
    }

    public String toString() {
        String menu = this.menu.toString().replaceAll("_", " ");
        String licor = this.licor.toString().replaceAll("_", " ");
        return String.format("%-16s%-19s%s", super.toString(), menu, licor);
    }

}