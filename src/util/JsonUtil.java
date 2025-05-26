package util;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import model.Project;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JsonUtil {
    private static final String FILE_PATH = "data/projects.json";

    public static List<Project> loadProjects() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<List<Project>>(){}.getType();
            return new Gson().fromJson(reader, listType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveProjects(List<Project> projects) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            new Gson().toJson(projects, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
