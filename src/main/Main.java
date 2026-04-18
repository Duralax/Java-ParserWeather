package main;

import java.io.IOException;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        Parser parser = new Parser();
        FileStorage csv = new FileStorage();
        WeatherData weather1 = parser.getForecast("утро");
        System.out.println(weather1.toString());
        while (true){
            LocalTime timeNow = LocalTime.now();
            int hours = timeNow.getHour();
            int minutes = timeNow.getMinute();

            if ((hours == 23 && minutes == 48) || (hours == 8 && minutes == 30) || (hours == 12 && minutes == 30) || (hours == 17 && minutes == 40) || (hours == 20 && minutes == 0)){
                try {
                    WeatherData weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.save(weather);

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                System.out.println("Завершение работы");
                break;
            }
        }

    }
}
