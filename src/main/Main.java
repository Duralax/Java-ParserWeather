package main;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Locale;

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
    public static void checkAndNotify(WeatherData weather, WeatherData forecast, String period){
        System.out.println("\n--------- Погода ---------");
        // Текущая погода
        System.out.println("Сейчас: \n" + "\t" + weather.getCurrent_temperature() + "°C градусов \n"
                + "\t по ощущениям как " + weather.getTemperature_feel() + "°C, \n" +
                "\t" + weather.getDescription() + "\n" +
                "\t Давление: " + weather.getPressure() + "\n" +
                "\t Ветер: " + weather.getWind() + "\n" +
                "\t Влажность: " + weather.getHumidity() + "\n"
        );
        // Если прогноз есть
        if (forecast != null) {
            System.out.println("\nОтличия от прогноза:");
            boolean hasDiff = false;

            // Температура
            if (weather.getCurrent_temperature().equals("None") || forecast.getCurrent_temperature().equals("None")) {
                System.out.println("Данные для сравнения неполные");
            } else {
                int weatherTemp = Integer.parseInt(weather.getCurrent_temperature().replaceAll("[^0-9-]", ""));
                int forecastTemp = Integer.parseInt(forecast.getCurrent_temperature().replaceAll("[^0-9-]", ""));
                int tempDiff = weatherTemp - forecastTemp;

                if (Math.abs(tempDiff) >= 4) {
                    if (tempDiff > 0){
                        System.out.println("Теплее на " + tempDiff + " °C");
                    } else {
                        System.out.println("Холоднее на " + tempDiff + " °C");
                    }
                    hasDiff = true;
                }

            }

            // Ощущается как
            if (weather.getTemperature_feel().equals("None") || forecast.getTemperature_feel().equals("None")) {
                System.out.println("Данные для сравнения неполные");
            } else {
                int weatherFeel = Integer.parseInt(weather.getTemperature_feel().replaceAll("[^0-9-]", ""));
                int forecastFeel = Integer.parseInt(forecast.getTemperature_feel().replaceAll("[^0-9-]", ""));
                int feelDiff = weatherFeel - forecastFeel;

                if (Math.abs(feelDiff) >= 4) {
                    if (feelDiff > 0){
                        System.out.println("Ощущается теплее на " + feelDiff + " °C");
                    } else {
                        System.out.println("Ощущается холоднее на " + feelDiff + " °C");
                    }
                    hasDiff = true;
                }
            }

            // Описание !поменять на более корректное сравнение для описаний (в прогнозе не такое полное)!
            if (weather.getDescription().equals("None") || forecast.getDescription().equals("None")) {
                System.out.println("Данные для сравнения неполные");
            } else {
                if (!weather.getDescription().split("\\.")[0].equalsIgnoreCase(forecast.getDescription())) {
                    System.out.println("  Описание: " + forecast.getDescription() + " → " + weather.getDescription().split("\\.")[0]);
                    hasDiff = true;
                }

                // Давление разница 10+ мм
                int weatherPress = Integer.parseInt(weather.getPressure().replaceAll("[^0-9]", ""));
                int forecastPress = Integer.parseInt(forecast.getPressure().replaceAll("[^0-9]", ""));
                int pressDiff = Math.abs(weatherPress - forecastPress);

                if (pressDiff >= 10) {
                    System.out.println("  Давление: " + forecast.getPressure() + " стало " + weather.getPressure() + " мм рт. ст.");
                    hasDiff = true;
                }
            }

            // Ветер разница 2+
            if (weather.getWind().equals("None") || forecast.getWind().equals("None")) {
                System.out.println("Данные для сравнения неполные");
            } else {
                double weatherWind = Double.parseDouble(weather.getWind().replaceAll("[^0-9.,]", "").replace(",", "."));
                double forecastWind = Double.parseDouble(forecast.getWind().replaceAll("[^0-9.,]", "").replace(",", "."));
                double windDiff = Math.abs(weatherWind - forecastWind);

                if (windDiff >= 2) {
                    System.out.println("  Ветер было " + forecast.getWind() + " стало " + weather.getWind());
                    hasDiff = true;
                }
            }

            // Влажность разница 10+
            if (weather.getHumidity().equals("None") || forecast.getHumidity().equals("None")) {
                System.out.println("Данные для сравнения неполные");
            } else {
                int weatherHum = Integer.parseInt(weather.getHumidity().replaceAll("[^0-9]", ""));
                int forecastHum = Integer.parseInt(forecast.getHumidity().replaceAll("[^0-9]", ""));
                int humDiff = Math.abs(weatherHum - forecastHum);

                if (humDiff >= 10) {
                    System.out.println("  Влажность было " + forecast.getHumidity() + " стало " + weather.getHumidity());
                    hasDiff = true;
                }

                if (!hasDiff) {
                    System.out.println("  Прогноз полностью совпал с фактической погодой!");
                }
            }
        }

    }

}
