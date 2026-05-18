package com.cts.mfrp.oneappetite.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExcelUtils {

    private ExcelUtils() {}

    /** Reads a sheet into a list of row maps keyed by the first-row headers. */
    public static List<Map<String, String>> read(String path, String sheetName) {
        List<Map<String, String>> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) return rows;
            Row header = sheet.getRow(0);
            if (header == null) return rows;
            List<String> headers = new ArrayList<>();
            for (Cell c : header) headers.add(c.getStringCellValue());
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;
                Map<String, String> map = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell c = r.getCell(j);
                    map.put(headers.get(j), c == null ? "" : asString(c));
                }
                rows.add(map);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path, e);
        }
        return rows;
    }

    private static String asString(Cell c) {
        return switch (c.getCellType()) {
            case STRING  -> c.getStringCellValue();
            case NUMERIC -> String.valueOf((long) c.getNumericCellValue());
            case BOOLEAN -> String.valueOf(c.getBooleanCellValue());
            case FORMULA -> c.getCellFormula();
            default      -> "";
        };
    }
}
