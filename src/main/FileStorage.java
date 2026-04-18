package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class Csv {
    public void save(WeatherData weather){
        try {
            String filePath = "src/main/weather_data/weather_history.csv";
            Files.createDirectories(Paths.get("src/main/weather_data"));

            boolean fileExists = Files.exists(Paths.get(filePath));

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, true), StandardCharsets.UTF_8))) {

                if (!fileExists) {

                    writer.println("Дата" + ";" +
                            "Время" + ";" +
                            "Город" + ";" +
                            "Температура" + ";" +
                            "Ощущается" + ";" +
                            "Описание" + ";" +
                            "Ветер" + ";" +
                            "Давление" + ";" +
                            "Влажность");
                }

                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

                //System.out.println("DEBUG температура перед сохранением: " + weather.getCurrent_temperature());

                String line = weather.getDateTime().format(dateFormat) + ";" +
                        weather.getDateTime().format(timeFormat) + ";" +
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
}
