package com.project.importexel.web;


import com.project.importexel.ImportDataResponse;
import com.project.importexel.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/download-error-file")
    public ResponseEntity<InputStreamResource> downloadErrorFile(@RequestParam("file") MultipartFile file) throws IOException {
     return null;   // Votre code actuel pour télécharger le fichier d'erreur
    }

    @PostMapping("/import-excel")
    public ResponseEntity<ImportDataResponse> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            FileService.ImportResult importResult = fileService.importDataFromExcel(file);

            ImportDataResponse response = new ImportDataResponse();
            response.setInsertedSoins(importResult.getInsertedSoins());
            response.setErrorDetails(importResult.getErrorDetails());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
