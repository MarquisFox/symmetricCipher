package org.example.utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils{
    public static String readTextFile(String fileName) throws IOException {
        return Files.readString(Path.of(fileName));
    }

    public static void writeTextFile(String fileName, String content) throws IOException {
        Files.writeString(Path.of(fileName), content);
    }
}
