package com.project.importexel;

import com.project.importexel.entites.Soins;
import com.project.importexel.service.FileService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImportDataResponse {
    private List<Soins> insertedSoins;
    private List<FileService.ImportError> errorDetails;

    // Getters et Setters
}

