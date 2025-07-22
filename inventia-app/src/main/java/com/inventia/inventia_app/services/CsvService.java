package com.inventia.inventia_app.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.inventia.inventia_app.entities.ProductRecord;

/**
 * CsvService
 */
@Service
public class CsvService {
    //product_id,dt,ventas,dia,dia_semana,es_fin_semana,es_feriado,es_inicio_mes,media_7d,std_7d

    public List<ProductRecord> parseCsvFile(MultipartFile file) {
        List<ProductRecord> products = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            CSVFormat.Builder formatBuilder = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setTrim(true);
            CSVFormat format = formatBuilder.get();
            CSVParser parser = CSVParser.parse(fileReader, format);

            //System.out.println(parser.getHeaderNames());
            List<String> headers = parser.getHeaderNames();
            for (CSVRecord csvRecord : parser) {
                //System.out.println("Record: " + csvRecord.toString());
                List<Double> hoursSale = parseDoublesListValues(csvRecord.get("hours_sale"));
                List<Integer> hoursStockStatus = parseIntegerListValues(csvRecord.get("hours_stock_status"));
                ProductRecord record = new ProductRecord(
                    Integer.parseInt(csvRecord.get(headers.get(0))),
                    Integer.parseInt(csvRecord.get(headers.get(1))),
                    Integer.parseInt(csvRecord.get(headers.get(2))),
                    Integer.parseInt(csvRecord.get(headers.get(3))),
                    Integer.parseInt(csvRecord.get(headers.get(4))),
                    Integer.parseInt(csvRecord.get(headers.get(5))),
                    Integer.parseInt(csvRecord.get(headers.get(6))),
                    csvRecord.get(headers.get(7)),
                    Double.parseDouble(csvRecord.get(headers.get(8))),
                    hoursSale,
                    Integer.parseInt(csvRecord.get(headers.get(10))),
                    hoursStockStatus,
                    Double.parseDouble(csvRecord.get(headers.get(12))),
                    Integer.parseInt(csvRecord.get(headers.get(13))),
                    Integer.parseInt(csvRecord.get(headers.get(14))),
                    Double.parseDouble(csvRecord.get(headers.get(15))),
                    Double.parseDouble(csvRecord.get(headers.get(16))),
                    Double.parseDouble(csvRecord.get(headers.get(17))),
                    Double.parseDouble(csvRecord.get(headers.get(18)))
                );
                products.add(record);
                //System.out.println(csvRecord);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in CSV: " + e.getMessage());
        }
        return products;
    }

    private List<Double> parseDoublesListValues(String values) {
        String cleanedString = values.replace("[", "").replace("]","")
            .replace("\"","")
            .trim();
        return Arrays.stream(cleanedString.split("\\s+"))
            .map(Double::parseDouble)
            .collect(Collectors.toList());
    }

    private List<Integer> parseIntegerListValues(String values) {
        String cleanedString = values.replace("[", "").replace("]","").trim();
        return Arrays.stream(cleanedString.split("\\s+"))
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }

}
