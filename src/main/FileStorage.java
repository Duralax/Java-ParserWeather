package main;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class FileStorage {
    public static void saveWeather(WeatherData weather, String type, String period){
        try {
            String filePath = "src/main/weather_data/weather_history.csv";
            Files.createDirectories(Paths.get("src/main/weather_data"));

            boolean fileExists = Files.exists(Paths.get(filePath));

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, true), StandardCharsets.UTF_8))) {

                if (!fileExists) {

                    writer.println("Дата" + ";" +
                            "Время" + ";" +
                            "Тип данных" + ";" +
                            "Период" + ";" +
                            "Город" + ";" +
                            "Температура" + ";" +
                            "Ощущается" + ";" +
                            "Описание" + ";" +
                            "Ветер" + ";" +
                            "Давление" + ";" +
                            "Влажность");
                }

                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

                //System.out.println("Температура перед сохранением: " + weather.getCurrent_temperature());

                String line = weather.getDateTime().format(dateFormat) + ";" +
                        weather.getDateTime().format(timeFormat) + ";" +
                        type + ";" +
                        period + ";" +
                        weather.getCity() + ";" +
                        weather.getCurrent_temperature() + ";"  +
                        weather.getTemperature_feel() + ";" +
                        weather.getDescription() + ";" +
                        weather.getWind() + ";" +
                        weather.getPressure() + ";" +
                        weather.getHumidity();

                writer.println(line);
                System.out.println("Данные сохранены в CSV");
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения! " + e.getMessage());
        }

    }

    public static void makeDailyReport(LocalDate date) throws IOException{
        String csvFile = "src/main/weather_data/weather_history.csv";
        List<String> lines = Files.readAllLines(Paths.get(csvFile), StandardCharsets.UTF_8);
        if (lines.size() <= 2) {
            System.out.println("Нет данных для отчёта");
            return;
        }
        String targetDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Map<String, String[]> factMap = new HashMap<>();
        Map<String, String[]> forecastMap = new HashMap<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(";");
            if (!parts[0].equals(targetDate)) {
                continue;
            }
            String type = parts[2];
            String period = parts[3];
            String time = parts[1];

            if (type.equals("факт")) {
                if (period.equals("утро")) {
                    factMap.put("08:30", parts);
                } else if (period.equals("день")) {
                    factMap.put("12:30", parts);
                } else if (period.equals("вечер")) {
                    factMap.put("17:40", parts);
                }
            } else if (type.equals("прогноз")) {
                if (period.equals("день") && time.equals("08:30")) {
                    forecastMap.put("12:30", parts);
                } else if (period.equals("вечер") && time.equals("12:30")) {
                    forecastMap.put("17:40", parts);
                } else if (period.equals("утро") && time.equals("20:00")) {
                    forecastMap.put("08:30", parts);
                }
            }
        }
        File template = new File("src/main/template/template.xlsx");
        if (!template.exists()) {
            System.out.println("Шаблон не найден!");
            return;
        }

        String reportName = "daily_report_" + targetDate + ".xlsx";
        Files.copy(template.toPath(), Paths.get(reportName), StandardCopyOption.REPLACE_EXISTING);

        try (FileInputStream in = new FileInputStream(reportName);
             XSSFWorkbook workbook = new XSSFWorkbook(in)) {

            XSSFSheet factSheet = workbook.getSheet("Данные_фактические");
            if (factSheet != null) {
                String[] times = {"08:30", "12:30", "17:40"};
                int rowIdx = 1;
                for (String time : times) {
                    Row row = factSheet.getRow(rowIdx);
                    if (row == null){
                        row = factSheet.createRow(rowIdx);
                    }
                    row.createCell(1).setCellValue(time);
                    String[] data = factMap.get(time);
                    row.createCell(0).setCellValue(targetDate);
                    if (data != null) {
                        row.createCell(2).setCellValue(Integer.parseInt(data[5].replaceAll("[^0-9−]", ""))); // темп.
                        row.createCell(3).setCellValue(Integer.parseInt(data[6].replaceAll("[^0-9−]", ""))); // ощущается
                        row.createCell(4).setCellValue(Integer.parseInt(data[9].replaceAll("[^0-9]", "")));    // давление
                        row.createCell(5).setCellValue(Integer.parseInt(data[10].replaceAll("[^0-9]", "")));   // влажность
                        Matcher windReg = java.util.regex.Pattern.compile("\\d+,?\\d*").matcher(data[8]);
                        if (windReg.find()){
                            row.createCell(6).setCellValue(Double.parseDouble(windReg.group().replace(',', '.'))); // ветер
                        }
                    }
                    rowIdx++;
                }
            }

            XSSFSheet forecastSheet = workbook.getSheet("Данные_прогноз");
            if (forecastSheet != null) {
                String[] times = {"08:30", "12:30", "17:40"};
                int rowIdx = 1;
                for (String time : times) {
                    Row row = forecastSheet.getRow(rowIdx);
                    if (row == null){
                        row = forecastSheet.createRow(rowIdx);
                    }
                    row.createCell(0).setCellValue(targetDate);

                    row.createCell(1).setCellValue(time);

                    String[] data = forecastMap.get(time);
                    if (data != null) {
                        row.createCell(2).setCellValue(Integer.parseInt(data[5].replaceAll("[^0-9−]", ""))); // темп.
                        row.createCell(3).setCellValue(Integer.parseInt(data[6].replaceAll("[^0-9−]", ""))); // ощущается
                        row.createCell(4).setCellValue(Integer.parseInt(data[9].replaceAll("[^0-9]", "")));    // давление
                        row.createCell(5).setCellValue(Integer.parseInt(data[10].replaceAll("[^0-9]", "")));   // влажность
                        Matcher windReg = java.util.regex.Pattern.compile("\\d+,?\\d*").matcher(data[8]);
                        if (windReg.find()){
                            row.createCell(6).setCellValue(Double.parseDouble(windReg.group().replace(',', '.'))); // ветер
                        }
                    }
                    rowIdx++;
                }
            }
            try (FileOutputStream output = new FileOutputStream(reportName)) {
                workbook.write(output);
            }
            System.out.println("Отчёт создан: " + reportName);
        }

    }

}
