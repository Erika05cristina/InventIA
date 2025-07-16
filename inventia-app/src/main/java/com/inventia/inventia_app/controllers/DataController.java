package com.inventia.inventia_app.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.inventia.inventia_app.entities.ProductRecord;
import com.inventia.inventia_app.services.CsvService;
import com.inventia.inventia_app.services.DataService;

/**
 * DataController
 */
@RestController
@RequestMapping("/data")
public class DataController {

    private CsvService csvService;
    private DataService dataService;

    public DataController(CsvService csvService, DataService dataService) {
        this.csvService = csvService;
        this.dataService = dataService;
    }

    @PostMapping(value="/upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> uploadData(@RequestPart("file") MultipartFile file){
        if(file.isEmpty()){
            return new ResponseEntity<>("El archivo cargado está vacío. Revise el archivo", HttpStatus.BAD_REQUEST);
        }
        if (!"text/csv".equals(file.getContentType()) && !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            return new ResponseEntity<>("Solo se permiten archivos con extension .csv.", HttpStatus.BAD_REQUEST);
        }

        try {
            System.out.println("Archivo .csv recibido: " + file.getOriginalFilename() + " con tamaño: " + file.getSize());
            List<ProductRecord> products = csvService.parseCsvFile(file);

            System.out.println("Se encontraron " + products.size() + " productos en el CSV.");
            products.forEach(product -> System.out.println("Parsed product: " + product));
            // productRepository.saveAll(products);
            this.dataService.upload(file);

            return new ResponseEntity<>("Archivo CSV procesado correctamente. Se encontraron " + products.size() + " registros de productos.", HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            System.out.println("Error en la validación del CSV: " + e.getMessage());
            return new ResponseEntity<>("Error en el formato del CSV: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            System.out.println("Error al cargar y procesar el archivo CSV: " + e.getMessage());
            return new ResponseEntity<>("Error al procesar el archivo CSV: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
