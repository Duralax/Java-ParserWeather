package main;

import java.io.IOException;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        Parser parser = new Parser();
        FileStorage csv = new FileStorage();
        // запуск парсинга для прогноза на части дня для утра следующего дня, дня, вечера
        WeatherData weatherMorningNextDay;
        WeatherData weatherDayToday;
        WeatherData weatherEveningToday;

        WeatherData weather;

        //System.out.println(weatherMorningNextDay.toString());
        while (true){
            LocalTime timeNow = LocalTime.now();
            int hours = timeNow.getHour();
            int minutes = timeNow.getMinute();

            if (hours == 8 && minutes == 30){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "факт", "утро");

                    weatherDayToday = parser.getForecast("день");
                    weatherEveningToday = parser.getForecast("вечер");

                    csv.saveWeather(weatherDayToday, "прогноз", "день");
                    csv.saveWeather(weatherEveningToday, "прогноз", "вечер");

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
            if (hours == 12 && minutes == 30){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "факт", "день");

                    weatherEveningToday = parser.getForecast("вечер");
                    csv.saveWeather(weatherEveningToday, "прогноз", "вечер");

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
            if (hours == 17 && minutes == 40){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "факт", "вечер");

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }

            if (hours == 20 && minutes == 0){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "факт", "поздний вечер");

                    weatherMorningNextDay = parser.getForecast("утро");
                    csv.saveWeather(weatherMorningNextDay, "прогноз", "утро");

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }


            // потом убрать все что ниже
            if ((hours == 8 && minutes == 30) || (hours == 12 && minutes == 30) || (hours == 17 && minutes == 40) || (hours == 20 && minutes == 0)){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "-", "-");

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
