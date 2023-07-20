package edu.prog2;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import edu.prog2.helpers.Keyboard;
import edu.prog2.helpers.UtilFiles;
import edu.prog2.model.Pasajero;
import edu.prog2.model.Trayecto;
import edu.prog2.services.PasajerosService;
import edu.prog2.services.TrayectosService;

public class FileTests {
    public static void main(String[] args) {
        menu();
    }

    private static int leerOpcion() {
        String opciones = "\nMenú de opciones:\n"
                + " 1 - testFileExists                       5 -  testInitPath\n"
                + " 2 - testPathExists                       6 -  testWriteText\n"
                + " 3 - testCreateFolderIfNotExist           7 -  testReadText\n"
                + " 4 - testGetPath                          8 -  testWriteCSV\n"
                + "\n elegina una ocpion (0 para salir) >";
        int opcion = Keyboard.readInt(opciones);
        System.out.println();
        return opcion;
    }

    private static void menu() {

        do {
            try {
                int opcion = leerOpcion();
                switch (opcion) {
                    case 1:
                        testFileExists();
                        break;
                    case 2:
                        testPathExists();
                        break;
                    case 3:
                        testCreateFolderIfNotExist();
                        break;
                    case 4:
                        testGetPath();
                        break;
                    case 5:
                        testInitPath();
                        break;
                    case 6:
                        testWriteText();
                        break;
                    case 7:
                        testReadText();
                        break;
                    case 8:
                        testWriteCSV();
                        break;
                    default:
                        System.out.println("opcion invalida");
                        break;
                    case 0:
                        System.exit(0);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }

    private static void testWriteCSV() throws IOException {
        TrayectosService trayectos = new TrayectosService();
        trayectos.add(new Trayecto("Manizales", "Pasto", Duration.parse("PT45M"), 25));
        trayectos.add(new Trayecto("Pasto", "Manizales", Duration.parse("PT45M"), 25));
        trayectos.add(new Trayecto("Cali", "Tunja", Duration.parse("PT1H15M"), 30));
        trayectos.add(new Trayecto("Tunja", "Cali", Duration.parse("PT1H15M"), 30));
        trayectos.add(new Trayecto("Bogotá", "Pereira", Duration.parse("PT1H30M"), 23));
        trayectos.add(new Trayecto("Pereira", "Bogotá", Duration.parse("PT1H30M"), 23));
        UtilFiles.writeCSV(trayectos.getList(), "./data/trayectos.csv");
    }

    private static void testReadText() throws IOException {
        String filePath = "./src/main/java/edu/prog2/model/Pasajero.java";
        String texto = UtilFiles.readText(filePath);
        System.out.println(texto);
        String filePath2 = "./data/jugadores.txt";
        String texto2 = UtilFiles.readText(filePath2);
        System.out.println(texto2);
        String filePath3 = "./target/classes/edu/prog2/model/Avion.class";
        String texto3 = UtilFiles.readText(filePath3);
        System.out.println(texto3);

    }

    private static void testWriteText() throws Exception {
        ArrayList<String> jugadores = new ArrayList<>();
        jugadores.add("Diego Armando Maradona");
        jugadores.add("Lionel Messi");
        jugadores.add("Cristiano Ronaldo");
        jugadores.add("Johan Cruyff");
        jugadores.add("Franz Beckenbauer");
        UtilFiles.writeText(jugadores, "./data/jugadores.txt");

        PasajerosService pasajeros = new PasajerosService();
        pasajeros.add(new Pasajero("P0101", "Angela María", "García Gómez"));
        pasajeros.add(new Pasajero("P0202", "Ricardo", "Rodríguez"));
        pasajeros.add(new Pasajero("P0303", "Jhon Jairo", "González Díaz"));
        pasajeros.add(new Pasajero("P0404", "Andrés Felipe", "Manrique"));
        pasajeros.add(new Pasajero("P0505", "Felipe Cesar", "Rincón"));
        pasajeros.add(new Pasajero("P0506", "Alexandra", "Duarte Mendez"));
        UtilFiles.writeText(pasajeros.getList(), "./data/pasajeros.txt");
    }

    private static void testInitPath() throws IOException {
        UtilFiles.initPath("./data/varios/prueba.txt");
        UtilFiles.initPath("./data/trazas/prueba.txt");
    }

    private static void testGetPath() {
        String ruta = UtilFiles.getPath(
                "./src/main/java/edu/prog2/controllers/CtrlSillas.java");

        System.out.println("La ruta padre es: " + ruta);

        ruta = UtilFiles.getPath("./src/main/java/edu/prog2/controllers/");
        System.out.println("La ruta padre es: " + ruta);
    }

    private static void testCreateFolderIfNotExist() throws IOException {
        UtilFiles.createFolderIfNotExist("./data/prueba1");
        System.out.println("Carpeta \"./data/prueba1\" lista para ser usada");
    }

    private static void testPathExists() {
        boolean existeCarp = UtilFiles.pathExists("target/test-classes/edu");
        System.out.println("¿Existe la carpeta target? = " + existeCarp);

        existeCarp = UtilFiles.pathExists("./src/main/java/edu/prog2/helpers");
        System.out.println("¿Existe la carpeta  helpers? = " + existeCarp);

        existeCarp = UtilFiles.pathExists(" ./src/main/java/edu/prog2/model/Pasajero.java");
        System.out.println("¿Exsite la carpeta pasajero ? = " + existeCarp);
    }

    private static void testFileExists() {
        boolean existe = UtilFiles.fileExists("./pom.xml");
        System.out.println("¿Existe pom.xml? = " + existe);

        existe = UtilFiles.fileExists("./src/main/java/edu/prog2/helpers/Keyboard.java");
        System.out.println("¿Existe Keyboard.java? = " + existe);

        existe = UtilFiles.fileExists("./src/main/java/edu/prog2/helpers/");
        System.out.println("¿Existe helpers? = " + existe);
    }

}
