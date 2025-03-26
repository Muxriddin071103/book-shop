package uz.app.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;

@RestController
@RequestMapping("/files")
public class FileController {

    @GetMapping("/{partialName}")
    public ResponseEntity<Map<String, Object>> serveFile(@PathVariable String partialName) throws MalformedURLException {
        File baseDir = new File("src/main/files/photos/");

        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return ResponseEntity.notFound().build();
        }

        File foundFile = findFile(baseDir, partialName);

        if (foundFile != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("filename", foundFile.getName());
            response.put("size", foundFile.length());
            response.put("url", "/files/photos/" + foundFile.getName());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }

    private File findFile(File directory, String partialName) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File found = findFile(file, partialName);
                    if (found != null) {
                        return found;
                    }
                } else if (file.getName().toLowerCase().contains(partialName.toLowerCase())) {
                    return file;
                }
            }
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllFiles() {
        File directory = new File("src/main/files/photos/");
        Map<String, Object> response = new HashMap<>();

        if (!directory.exists() || !directory.isDirectory()) {
            response.put("error", "Directory not found");
            return ResponseEntity.badRequest().body(response);
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".webp"));
        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }

        response.put("count", fileNames.size());
        response.put("files", fileNames);

        return ResponseEntity.ok(response);
    }
}
