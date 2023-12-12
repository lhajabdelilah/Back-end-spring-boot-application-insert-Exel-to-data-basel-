package com.project.importexel.service;

import com.project.importexel.dao.SoinsRepository;
import com.project.importexel.entites.Soins;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class FileService {

    @Autowired
    private SoinsRepository soinsRepository;

    public class ImportResult {
        private List<Soins> insertedSoins;
        private List<ImportError> errorDetails;

        public List<Soins> getInsertedSoins() {
            return insertedSoins;
        }

        public void setInsertedSoins(List<Soins> insertedSoins) {
            this.insertedSoins = insertedSoins;
        }

        public List<ImportError> getErrorDetails() {
            return errorDetails;
        }

        public void setErrorDetails(List<ImportError> errorDetails) {
            this.errorDetails = errorDetails;
        }
    }

    public class ImportError {
        private int rowNumber;
        private String errorType;

        public int getRowNumber() {
            return rowNumber;
        }

        public void setRowNumber(int rowNumber) {
            this.rowNumber = rowNumber;
        }

        public String getErrorType() {
            return errorType;
        }

        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }
    }

    public ImportResult importDataFromExcel(MultipartFile file) throws IOException {
        ImportResult importResult = new ImportResult();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            List<Soins> insertedSoins = new ArrayList<>();
            List<ImportError> errorDetails = new ArrayList<>();
            Set<String> uniqueRows = new HashSet<>();

            // Skip header if needed
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            int rowNumber = 1;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Soins soins = createSoinsFromRow(row);

                // Validate data
                String validationResult = validateSoins(soins, uniqueRows);

                if (validationResult == null) {
                    // Save valid data to the database
                    try {
                        Soins insertedSoinsEntity = soinsRepository.save(soins);
                        insertedSoins.add(insertedSoinsEntity);
                        uniqueRows.add(generateUniqueKey(soins)); // Store unique row identifier
                    } catch (Exception e) {
                        // Handle database insertion errors
                        ImportError error = new ImportError();
                        error.setRowNumber(rowNumber);
                        error.setErrorType("Database Insertion Error: " + e.getMessage());
                        errorDetails.add(error);
                    }
                } else {
                    // If validation error, add to errorDetails list
                    ImportError error = new ImportError();
                    error.setRowNumber(rowNumber);
                    error.setErrorType("Validation Error: " + validationResult);
                    errorDetails.add(error);
                }

                rowNumber++;
            }

            importResult.setInsertedSoins(insertedSoins);
            importResult.setErrorDetails(errorDetails);
        }
        return importResult;
    }

    private Soins createSoinsFromRow(Row row) {
        Soins soins = new Soins();
        soins.setRegion(getStringValue(row.getCell(0)));
        soins.setDelegation(getStringValue(row.getCell(1)));
        soins.setCommune(getStringValue(row.getCell(2)));
        soins.setNom_établissement(getStringValue(row.getCell(3)));
        soins.setCategorie(getStringValue(row.getCell(4)));
        return soins;
    }

    private String validateSoins(Soins soins, Set<String> uniqueRows) {
        // Perform validation
        if (soins.getRegion() == null || soins.getDelegation() == null || soins.getNom_établissement() == null) {
            return "Mandatory field missing";
        }

        // Check for duplicate rows
        String uniqueKey = generateUniqueKey(soins);
        if (uniqueRows.contains(uniqueKey)) {
            return "Duplicate row";
        }

        // Other validations...

        return null; // Indicates no validation error
    }

    private String generateUniqueKey(Soins soins) {
        // Generate a unique key for a row based on the fields that should be unique
        return soins.getRegion() + "-" + soins.getDelegation() + "-" + soins.getNom_établissement();
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
