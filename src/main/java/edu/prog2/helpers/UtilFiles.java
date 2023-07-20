package edu.prog2.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.Property;
import edu.prog2.model.IFormatCSV;

import java.lang.reflect.*;

public class UtilFiles {
    public static final String FILE_PATH = "./data/";

    public static boolean exists(String fileName, String key, Object search) throws Exception {
        String data = readText(fileName + ".json");
        JSONArray array = new JSONArray(data);
        JSONObject json = new JSONObject();
        Constructor<?> constructor = search.getClass().getConstructor(JSONObject.class);
        Object current = null;
        for (int i = 0; i < array.length(); i++) {
            json = array.getJSONObject(i);
            current = constructor.newInstance(json.get(key));
            if (current.equals(search)) {
                return true;
            }
        }
        return false;
    }

    public static JSONObject paramsToJson(String s) throws IOException {
        s = s.replace("&", "\n");
        StringReader reader = new StringReader(s);
        Properties properties = new Properties();
        properties.load(reader);
        return Property.toJSONObject(properties);
    }

    public static void writeData(List<?> list, String fileName) throws IOException {
        writeCSV(list, fileName + ".csv");
        writeJSON(list, fileName + ".json");
    }

    public static void writeJSON(List<?> list, String fileName) throws IOException {
        JSONArray jsonArray = new JSONArray(list);
        writeText(jsonArray.toString(2), fileName);
    }

    public static void writeText(String content, String fileName) throws IOException {
        Path path = initPath(fileName);
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean fileExists(String fileName) {
        Path dirPath = Paths.get(fileName);
        return Files.exists(dirPath) && !Files.isDirectory(dirPath);
    }

    public static boolean pathExists(String path) {
        Path folder = Paths.get(path);
        return Files.exists(folder) && Files.isDirectory(folder);
    }

    public static void createFolderIfNotExist(String folder) throws IOException {
        if (!pathExists(folder)) {
            Path dirPath = Paths.get(folder);
            Files.createDirectories(dirPath);
        }
    }

    public static String getPath(String path) {
        Path parentPath = Paths.get(path).getParent();
        return parentPath == null ? null : parentPath.toString();
    }

    public static Path initPath(String filePath) throws IOException {
        String path = getPath(filePath);
        createFolderIfNotExist(path);
        return Paths.get(filePath);
    }

    public static void writeText(List<?> list, String fileName) throws Exception {
        UtilFiles.initPath(fileName);
        try (FileWriter fw = new FileWriter(
                new File(fileName), StandardCharsets.UTF_8);
                BufferedWriter writer = new BufferedWriter(fw)) {
            for (int i = 0; i < list.size(); i++) {
                writer.append(list.get(i).toString());
                writer.newLine();
            }
        }
    }

    public static String readText(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static void writeCSV(List<?> list, String fileName) throws IOException {
        UtilFiles.initPath(fileName);
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(new File(fileName), StandardCharsets.UTF_8))) {
            for (Object obj : list) {
                IFormatCSV aux = (IFormatCSV) obj;
                writer.append(aux.toCSV());
            }
        }
    }
}