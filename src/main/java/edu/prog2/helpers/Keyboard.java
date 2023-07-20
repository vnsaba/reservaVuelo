package edu.prog2.helpers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Keyboard {
    public static Scanner sc = new Scanner(System.in).useDelimiter("[\n]+|[\r\n]+");

    public static String readString(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine();
    }

    public static String readString(int from, int to, String message) {
        String value;
        int tmp = Math.min(from, to);
        if (tmp == to) {
            to = from;
            from = tmp;
        }

        int length;

        do {
            value = readString(message);
            length = value.length();
            if (length < from || length > to) {
                System.out.print("Longitud no permitida. ");
            }
        } while (length < from || length > to);
       
        return value;
    }

    public static int readInt(String mensaje) {
        boolean ok;
        int value = Integer.MIN_VALUE;
        System.out.print(mensaje);

        do {
            try {
                ok = true;
                value = sc.nextInt();
            } catch (InputMismatchException e) {
                ok = false;
                System.out.print(">> Valor erróneo. " + mensaje);
            } finally {
                sc.nextLine();
            }
        } while (!ok);

        return value;
    }

    public static long readLong(String mensaje) {
        boolean ok;
        long value = Long.MIN_VALUE;
        System.out.println(mensaje);
        do {
            try {
                ok = true;
                value = sc.nextLong();
            } catch (InputMismatchException e) {
                ok = false;
                System.out.println(" >> valor erroneo. " + mensaje);
            } finally {
                sc.nextLine();
            }
        } while (!ok);
        return value;
    }

    public static long readLong(long from, long to, String mensaje) {
        long value;
        long tmp = Math.min(from, to);
        if (tmp == to) {
            to = from;
            from = tmp;
        }

        do {
            value = readLong(mensaje);
            if (value < from || value > to) {
                System.out.print("Rango inválido. ");
            }
        } while (value < from || value > to);

        return value;
    }

    public static double readDouble(double desde, double hasta, String mensaje) {

        double value;
        double min = Math.min(desde, hasta);
        if (min == hasta) {
            hasta = desde;
            desde = min;
        }

        do {
            value = readDouble(mensaje);
            if (value < desde || value > hasta) {
                System.out.println("Rango invalido. ");
            }
        } while (value < desde || value > hasta);
        return value;

    }

    public static int readInt(int desde, int hasta, String mensaje) {
        int value;
        int min = Math.min(desde, hasta); // devuelve el numero menor entre los dos numeros
        if (min == hasta) {
            hasta = desde;
            desde = min;
        }
        do {
            value = readInt(mensaje);
            if (value < desde || value > hasta) {
                System.out.println("Rango invalido. ");
            }
        } while (value < desde || value > hasta);
        return value;
    }

    public static double readDouble(String mensaje) {
        boolean ok;
        double value = Double.NaN;
        System.out.print(mensaje);
        do {
            try {
                ok = true;
                value = sc.nextDouble();
            } catch (InputMismatchException e) {
                ok = false;
                System.out.print(" >> valor erroneo. " + mensaje);
            } finally {
                sc.nextLine();
            }
        } while (!ok);
        return value;
    }

    public static boolean readBoolean(String message) {
        boolean ok;
        boolean value = false;
        System.out.print(message);

        do {
            try {
                ok = true;
                String str = ' ' + sc.next().toLowerCase().trim() + ' ';
                if (" si s true t yes y ".contains(str)) {
                    value = true;
                } else if (" no n false f not ".contains(str)) {
                    value = false;
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                ok = false;
                System.out.print(">> Se esperaba [si|s|true|t|yes|y|no|not|n|false|f] \n"
                        + message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);

        return value;
    }

    public static LocalDate readDate(String message) {
        boolean ok;
        LocalDate date = LocalDate.now();
        System.out.print(message);

        do {
            try {
                ok = true;
                String strDate = sc.next().trim();
                if (!strDate.toLowerCase().equals("hoy")) {
                    date = LocalDate.parse(strDate);
                }
            } catch (DateTimeParseException dtps) {
                ok = false;
                System.out.print(">> Fecha errónea. " + message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);

        return date;
    }

    public static Duration readDuration(String mensaje) {
        boolean ok;
        Duration duracion = Duration.ZERO;
        System.out.print(mensaje);

        do {
            try {
                ok = true;
                String strDuration[] = sc.next().trim().split(":");
                duracion = Duration.parse(String.format("PT%sH%sM", strDuration[0], strDuration[1]));

            } catch (DateTimeParseException e) {
                ok = false;
                System.out.print(">> duracion errónea. " + mensaje);
            } finally {
                sc.nextLine();
            }
        } while (!ok);
        return duracion;
    }

    public static LocalDateTime readDateTime(String message) {
        boolean ok;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.print(message);
        do {
            try {
                ok = true;
                String strDate = sc.next().trim();
                if (!strDate.toLowerCase().equals("ahora")) {
                    dateTime = LocalDateTime.parse(strDate, formatter);
                }
            } catch (DateTimeParseException dtps) {
                ok = false;
                System.out.print(">> Fecha y hora erróneas. " + message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);

        return dateTime;
    }

    public static <T extends Enum<T>> T readEnum(Class<T> c, String message) {
        boolean ok;
        System.out.print(message);

        if (c != null) {
            do {
                ok = true;
                try {
                    String type = sc.nextLine().trim().toUpperCase();
                    return type.length() > 0 ? Enum.valueOf(c, type) : null;
                } catch (IllegalArgumentException ex) {
                    ok = false;
                    System.out.printf(
                            ">> %s no incluye el valor esperado. %s",
                            c.getSimpleName(), message);
                }
            } while (!ok);
        }

        return null;
    }

    public static <T extends Enum<T>> T readEnum(Class<T> c) {
        Object[] allItems = (EnumSet.allOf(c)).toArray();
        String message = String.format("%nOpciones de %s:", c.getSimpleName());

        int i;
        for (i = 0; i < allItems.length; i++) {
            String item = allItems[i].toString().replaceAll("_", " ");
            message += String.format("%n%3d-%s", i + 1, item);
        }

        message = String.format(
                "%s%nElija un tipo entre 1 y %d: ", message, allItems.length);

        do {
            i = readInt(message);
        } while (i < 1 || i > allItems.length);

        return Enum.valueOf(c, allItems[i - 1].toString());
    }
}
