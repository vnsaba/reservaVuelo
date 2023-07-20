package edu.prog2;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.json.JSONObject;

import edu.prog2.helpers.*;
import edu.prog2.model.*;
import edu.prog2.services.*;

public class App {

    static PasajerosService pasajeros;
    static AvionesService aviones;
    static ReservasService reservas;
    static SillasService sillas;
    static TrayectosService trayectos;
    static VuelosService vuelos;
    static ReservasVuelosService reservaVuelos;
    static JSONObject jsonPasajero;
    static JSONObject jsonAvion;
    static JSONObject jsonSilla;
    static JSONObject jsonSillaEjecutiva;
    static JSONObject jsonTrayecto;
    static JSONObject jsonVuelo;
    static JSONObject jsonReserva;
    static JSONObject jsonReservaVuelo;

    public static void main(String[] args) {
        menu();

    }

    private static void inicializar() throws IOException {
        pasajeros = new PasajerosService();
        trayectos = new TrayectosService();
        aviones = new AvionesService();
        sillas = new SillasService(aviones);
        vuelos = new VuelosService(trayectos, aviones);
        reservas = new ReservasService(pasajeros);
        reservaVuelos = new ReservasVuelosService(reservas, vuelos, sillas);
    }

    private static void menu() {

        try {
            inicializar();

        } catch (Exception e) {
            e.printStackTrace();
        }
        do {
            try {
                int opcion = leerOpcion();

                switch (opcion) {
                    case 1:
                        crearPasajeros();
                        break;
                    case 2:
                        crearTrayectos();
                        break;
                    case 3:
                        crearAviones();
                        break;
                    case 4:
                        programarVuelos();
                        break;
                    case 5:
                        crearReserva();
                        break;
                    case 6:
                        listarPasajeros();
                        break;
                    case 7:
                        listarTrayectos();
                        break;
                    case 8:
                        listarAviones();
                        break;
                    case 9:
                        listarVuelos();
                        break;
                    case 10:
                        listarAvionesConSillas();
                        break;
                    case 11:
                        listarReservas();
                        break;
                    case 12:
                        listarPasajerosCSV();
                        break;
                    case 13:
                        listarTrayectosCSV();
                        break;
                    case 14:
                        listarAvionesCSV();
                        break;
                    case 15:
                        listarSillaCSV();
                        break;
                    case 16:
                        listarVuelosCSV();
                        break;
                    case 17:
                        listarReservaVuelosCSV();
                        break;
                    case 18:
                        listarReservaCSV();
                        break;
                    case 19:
                        elegirVuelo();
                        break;
                    case 20:
                        elegirPasajero();
                        break;
                    case 21:
                        elegirSillaDisponible(vuelos.get(0));
                        break;
                    case 22:
                        listarVuelosReserva(reservas.get(0));// caso prueba
                        break;
                    case 23:
                        listarSillasDisponiblesEnVuelo(vuelos.get(0));
                        break;
                    case 24:
                        vuelos.listAll();
                        break;
                    case 25:
                        pasajeros.listAll();
                        break;
                    case 26:
                        aviones.listAll(sillas);
                        break;
                    case 27:
                        trayectos.listAll();
                        break;
                    case 28:
                        reservas.listAll(reservaVuelos);
                        break;
                    case 0:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }

    static int leerOpcion() {
        String opciones = "\nMenú de opciones:\n"
                + " 1 - crear pasajeros                       15 - listar sillas con CSV                         \n"
                + " 2 - crear trayectos                       16 - listar vuelos con CSV                         \n"
                + " 3 - crear aviones                         17 - listar reservas vuelos con CSV                \n"
                + " 4 - programar vuelos                      18 - listar reservas con CSV                       \n"
                + " 5 - crear reserva                         19 - elegir vuelo                                  \n"
                + " 6 - listar pasajeros                      20 - elegir pasajero                               \n"
                + " 7 - listar trayectos                      21 - elegir silla disponible (indice 0)            \n"
                + " 8 - listar aviones                        22 - listar vuelos reservas  (indice 0)            \n"
                + " 9 - listar vuelos                         23 - listar silla disponible en vuelo  (indice 0)  \n"
                + "10 - listar aviones con silla              24 - listar vuelos con txt                         \n"
                + "11 - listar reservas                       25 - listar pasajeros con txt                      \n"
                + "12 - listar pasajeros con CSV              26 - listar aviones con sus sillas txt             \n"
                + "13 - listar trayectos con CSV              27 - listar trayectos con txt                      \n"
                + "14 - listar aviones con CSV                28 - listado de reservas con sus vuelos                                                 \n"

                + "\nElija una opción (0 para salir) > ";

        int opcion = Keyboard.readInt(opciones);
        System.out.println();
        return opcion;
    }

    public static void listarSillaCSV() {
        for (Silla silla : sillas.getList()) {
            System.out.print(silla.toCSV());
        }
    }

    public static void listarReservaCSV() {

        for (Reserva reserva : reservas.getList()) {
            System.out.print(reserva.toCSV());
        }
    }

    public static void listarReservaVuelosCSV() {
        for (ReservaVuelo reservaVuelo : reservaVuelos.getList()) {
            System.out.print(reservaVuelo.toCSV());
        }
    }

    public static void listarVuelosCSV() {
        for (Vuelo vuelo : vuelos.getList()) {
            System.out.print(vuelo.toCSV());
        }
    }

    public static void listarTrayectosCSV() {
        for (Trayecto trayecto : trayectos.getList()) {
            System.out.print(trayecto.toCSV());
        }
    }

    public static void listarPasajerosCSV() {
        for (Pasajero pasajero : pasajeros.getList()) {
            System.out.print(pasajero.toCSV());
        }
    }

    static void listarAvionesCSV() {
        for (Avion avion : aviones.getList()) {
            System.out.print(avion.toCSV());
        }
    }

    public static void listarReservas() {
        for (Reserva r : reservas.getList()) {
            System.out.println(r.toString());
            listarVuelosReserva(r);
        }
    }

    private static void listarVuelosReserva(Reserva r) {
        System.out.println("vuelos:");
        for (ReservaVuelo re : reservaVuelos.getList()) {
            if (re.getReserva().equals(r)) {
                System.out.println(re.toString());

            }
        }
    }

    private static Trayecto elegiTrayecto() {
        if (trayectos == null) {
            System.out.println("no hay trayectos disponibles");
        } else {
            System.out.println("\ntrayectos disponibles:");
            for (int i = 0; i < trayectos.getList().size(); i++) {
                Trayecto trayecto = trayectos.get(i);
                System.out.printf("%2d: %s %s %.2f %s%n", (i + 1), trayecto.getOrigen(), trayecto.getDestino(),
                        trayecto.getCosto(), trayecto.strDuracion());
            }
        }
        int i;
        do {
            i = Keyboard.readInt("Elija el índice del vuelo  (0-Termina): ");
            if (i < 0 || i > trayectos.getList().size()) {
                System.out.printf("erro. erlija el indice entre 0 y %d%n", trayectos.getList().size());
            }
        } while (i < 0 || i > trayectos.getList().size());
        return i > 0 ? trayectos.get(i - 1) : null;
    }

    private static Avion elegirAvion() {

        System.out.println("\nAviones disponibles:");
        for (int j = 0; j < aviones.getList().size(); j++) {
            Avion avion = aviones.get(j);
            System.out.printf("%2d: %s-%s%n", (j + 1), avion.getMatricula(), avion.getModelo());
        }

        int j;
        do {
            j = Keyboard.readInt("Elija el índice del avion (0-Termina): ");
            if (j < 0 || j > aviones.getList().size()) {
                System.out.printf("Error. Elija un índice entre 1 y %d%n",
                        aviones.getList().size());
            }
        } while (j < 0 || j > aviones.getList().size());
        return j > 0 ? aviones.get(j - 1) : null;

    }

    private static Vuelo elegirVuelo() {
        System.out.println("vuelos programados:");
        for (int i = 0; i < vuelos.getList().size(); i++) {
            Vuelo vue = vuelos.get(i);
            System.out.printf("%2d:%s-%s %s %s %s%n",
                    (i + 1), vue.strFechaHora(), vue.getTrayecto().strDuracion(),
                    vue.getAvion().getMatricula(),
                    vue.getTrayecto().getOrigen(), vue.getTrayecto().getDestino());
        }
        int i;
        do {
            i = Keyboard.readInt("Elija el índice del vuelo  (0-Termina): ");
            if (i < 0 || i > vuelos.getList().size()) {
                System.out.printf("Error. Elija un índice entre 1 y %d%n", vuelos.getList().size());
            }
        } while (i < 0 || i > vuelos.getList().size());
        return i > 0 ? vuelos.get(i - 1) : null;
    }

    static Pasajero elegirPasajero() {
        System.out.println("Pasajeros:");
        for (int i = 0; i < pasajeros.getList().size(); i++) {
            Pasajero p = pasajeros.get(i);
            System.out.printf("%2d: %s-%s %s%n", (i + 1), p.getIdentificacion(), p.getNombres(), p.getApellidos());
        }
        int i;
        do {
            i = Keyboard.readInt("\nElija el índice del pasajero (0-Termina): ");
            if (i < 0 || i > pasajeros.getList().size()) {
                System.out.printf("Error. Elija un índice entre 1 y %d%n",
                        pasajeros.getList().size());
            }
        } while (i < 0 || i > pasajeros.getList().size());
        return i > 0 ? pasajeros.get(i - 1) : null;
    }

    private static Silla elegirSillaDisponible(Vuelo vuelo) {
        listarSillasDisponiblesEnVuelo(vuelo);
        ArrayList<Silla> lista = sillasDisponiblesEnVuelo(vuelo);
        int indice;
        do {
            indice = Keyboard.readInt("\ningrese el indice del puesto deseado ");
            if (indice < 0 || indice > lista.size() - 1) {
                System.out.printf("Error. Elija un índice entre 0 y %d%n", lista.size() - 1);

            }
        } while (indice < 0 || indice > lista.size());

        return indice >= 0 ? lista.get(indice) : null;
    }

    private static void listarSillasDisponiblesEnVuelo(Vuelo vuelo) {
        ArrayList<Silla> listaSilla = sillasDisponiblesEnVuelo(vuelo);
        System.out.printf("%s: %s-%s -%s%n", "\nlistado se sillas disponibles en vuelo  " +
                vuelo.getAvion().getMatricula(), vuelo.getTrayecto().getOrigen(),
                vuelo.getTrayecto().getDestino(), vuelo.strFechaHora());

        int con = 0;
        for (Silla silla : listaSilla) {
            if (con % 10 == 0) {
                System.out.println();
            }
            System.out.format("%4d-%s-%s", listaSilla.indexOf(silla), silla.getPosicion(),
                    (silla instanceof SillaEjecutiva) ? 'E' : 'S');
            con++;
        }
    }

    private static ArrayList<Silla> sillasDisponiblesEnVuelo(Vuelo vuelo) {
        ArrayList<Silla> Sillasdisponible = new ArrayList<>();
        for (Silla silla : sillas.getList()) {
            if (silla.getAvion().equals(vuelo.getAvion()) && sillaDisponibleEnVuelo(silla, vuelo) == true) {
                Sillasdisponible.add(silla);
            }
        }
        return Sillasdisponible;
    }

    private static boolean sillaDisponibleEnVuelo(Silla silla, Vuelo vuelo) {
        ArrayList<Silla> sillasReservadas = sillasReservadasEnVuelo(vuelo);
        for (Silla sillas : sillasReservadas) {
            if (silla.getAvion().equals(vuelo.getAvion()) && silla.equals(sillas)) {
                return false;
            }
        }
        return true;
    }

    private static ArrayList<Silla> sillasReservadasEnVuelo(Vuelo vuelo) {
        ArrayList<Silla> sillasReservadas = new ArrayList<>();
        for (ReservaVuelo silla : reservaVuelos.getList()) {
            Silla sillas = silla.getSilla();
            if (sillas.getAvion().equals(vuelo.getAvion())) {
                if (sillas.getDisponible() == false && sillas.getDisponible() == false) {
                    sillasReservadas.add(sillas);
                }
            }
        }
        return sillasReservadas;
    }

    private static void listarAvionesConSillas() {
        if (aviones == null) {
            System.out.println("problemas con el arraylist de aviones");
        } else {
            for (Avion silla : aviones.getList()) {
                listarSillas(silla);
            }
        }
    }

    private static void listarSillas(Avion avion) {
        System.out.println(sillas.getList().size());
        if (avion == null || sillas == null) {
            System.out.println("Problemas con los ArrayList de aviones y de sillas");
        } else {
            System.out.println("SILLAS DEL AVIÓN " + avion.getMatricula());
            for (Silla s : sillas.getList()) {
                if (s.getAvion().equals(avion)) {
                    System.out.printf("%3d %s%n", sillas.getList().indexOf(s), s);
                }
            }
        }
    }

    private static void listarVuelos() {
        if (vuelos == null) {
            System.out.println("debe crear los aviones antes de listarlos");
        } else {
            for (Vuelo vue : vuelos.getList()) {
                System.out.println(vue.toString());
            }
        }
    }

    private static void listarAviones() {
        if (aviones == null) {
            System.out.println("debe crear los aviones antes de listarlos");
        } else {
            for (Avion av : aviones.getList()) {
                System.out.println(av.toString());
            }
        }
    }

    private static void listarTrayectos() {
        if (trayectos == null) {
            System.out.println("debe crear los trayectos antes de listar");
        } else {
            for (Trayecto t : trayectos.getList()) {
                System.out.println(t.toString());
            }
        }
    }

    private static void listarPasajeros() {
        if (pasajeros == null) {
            System.out.println("debe crear pasajeros antes de listar");
        } else {
            for (Pasajero p : pasajeros.getList()) {
                System.out.println(p.toString());
            }
        }
    }

    private static void crearReserva() throws IOException {
        ReservaVuelo reservaVuelo = new ReservaVuelo();
        Reserva reserva = new Reserva();
        Menu menu = Menu.INDEFINIDO;
        Licor licor = Licor.NINGUNO;
        System.out.println("Ingreso de pasajeros\n");
        do {

            Pasajero pasajero = elegirPasajero();
            if (pasajero == null) {
                break;
            }
            LocalDateTime fechaHoraReserva = Keyboard.readDateTime("fecha y hora: ");
            reserva = new Reserva(fechaHoraReserva, pasajero);
            boolean ok = false;
            do {
                Vuelo vuelo = elegirVuelo();
                if (vuelo == null) {
                    break;
                }
                Silla sillaElegida = elegirSillaDisponible(vuelo);
                if (sillaElegida instanceof SillaEjecutiva) {
                    SillaEjecutiva ejecutiva = (SillaEjecutiva) sillaElegida;
                    menu = Keyboard.readEnum(Menu.class);
                    licor = Keyboard.readEnum(Licor.class);

                    sillaElegida = ejecutiva;
                    reservaVuelo = new ReservaVuelo(reserva, vuelo, new SillaEjecutiva(sillaElegida.getFila(),
                            sillaElegida.getColumna(), menu, licor, vuelo.getAvion()));
                } else {
                    reservaVuelo = new ReservaVuelo(reserva, vuelo, sillaElegida);
                }
                reservaVuelo.getSilla().setDisponible(false);

                ok = reservaVuelos.add(reservaVuelo);

                System.out.printf("\nReserva registrada.\n");

            } while (true);
            if (ok) {
                reservas.add(reserva);
            }

        } while (true);

    }

    private static void programarVuelos() throws IOException {

        do {
            Trayecto trayecto = elegiTrayecto();
            if (trayecto == null) {
                return;
            }
            LocalDateTime fechaHoraVuelo = Keyboard.readDateTime("fecha y hora: ");
            Avion avion = elegirAvion();
            if (avion == null) {
                return;
            }
            Vuelo vuelo = new Vuelo(fechaHoraVuelo, trayecto, avion);
            boolean ok = vuelos.add(vuelo);
            if (ok) {
                System.out.printf("\nse agrego %s-%s, %s, fecha/hora: %s  (%s)%n", trayecto.getOrigen(),
                        trayecto.getDestino(), avion.getMatricula(), vuelo.strFechaHora(),
                        trayecto.getCosto());
            }
        } while (true);
    }

    private static void crearAviones() throws IOException {

        System.out.println("ingrese las sillas del avion");
        do {
            String matricula = Keyboard.readString("\nmatricula del avion: (intro termina) ");
            if (matricula.length() == 0) {
                return;
            }
            String modelo = Keyboard.readString(3, 15, "modelo del avion: ");
            int SillaEjecutiva = Keyboard.readInt("numero de sillas ejecutivas: ");
            int sillaEconomica = Keyboard.readInt("numero de sillas economicas: ");
            Avion avion = new Avion(matricula, modelo);
            boolean ok = aviones.add(avion);
            if (ok) {
                sillas.create(avion, SillaEjecutiva, sillaEconomica);
            }
        } while (true);
    }

    private static void crearTrayectos() throws IOException {

        do {
            String origen = Keyboard.readString("\norigen  (Intro termina):  ");
            if (origen.length() == 0) {
                return;
            }
            String destino = Keyboard.readString(3, 15, "destino:  ");
            Duration duracion = Keyboard.readDuration("duracion (HH:MM): ");
            double costo = Keyboard.readDouble("costo: ");
            boolean ok = trayectos.add(new Trayecto(origen, destino, duracion, costo));
            if (ok) {
                System.out.printf("se agrego el trayecto %s-%s%n", origen, destino);
            }
        } while (true);
    }

    private static void crearPasajeros() throws IOException {

        System.out.println("Ingreso de pasajeros\n");
        do {

            String identificacion = Keyboard.readString("Identificación (Intro termina): ");
            if (identificacion.length() == 0) {
                return;
            }
            String nombres = Keyboard.readString(3, 20, "Nombres: ");
            String apellidos = Keyboard.readString(3, 20, "Apellidos: ");
            boolean ok = pasajeros.add(new Pasajero(identificacion, nombres, apellidos));
            if (ok) {
                System.out.printf(
                        "Se agregó el pasajero con identificación: %s%n%n", identificacion);
            }
        } while (true);
    }
}