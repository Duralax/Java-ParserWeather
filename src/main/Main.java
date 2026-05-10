package main;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        Parser parser = new Parser();
        FileStorage csv = new FileStorage();
        // запуск парсинга для прогноза на части дня для утра следующего дня, дня, вечера
        WeatherData forecastMorningNextDay = null;
        WeatherData forecastDayToday = null;
        WeatherData forecastEveningToday = null;
        // для теста вывода погоды и
        WeatherData testForecast = parser.getForecast("утро");
        WeatherData testWeather = parser.getCurrentWeather();
        System.out.println(testWeather.toString());
        System.out.println(testForecast.toString());

        WeatherData weather;

        //System.out.println(forecastMorningNextDay.toString());
        while (true){
            LocalTime timeNow = LocalTime.now();
            int hours = timeNow.getHour();
            int minutes = timeNow.getMinute();

            if (hours == 8 && minutes == 30){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "факт", "утро");
                    checkAndNotify(weather, forecastMorningNextDay);
                    
                    forecastDayToday = parser.getForecast("день");
                    forecastEveningToday = parser.getForecast("вечер");

                    csv.saveWeather(forecastDayToday, "прогноз", "день");
                    csv.saveWeather(forecastEveningToday, "прогноз", "вечер");
                    showForecast(forecastDayToday, "день");
                    showForecast(forecastEveningToday, "вечер");

                    
                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
            if (hours == 12 && minutes == 30){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "факт", "день");
                    checkAndNotify(weather, forecastDayToday);

                    forecastEveningToday = parser.getForecast("вечер");
                    csv.saveWeather(forecastEveningToday, "прогноз", "вечер");
                    showForecast(forecastEveningToday, "вечер");

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
            if (hours == 17 && minutes == 40){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "факт", "вечер");
                    checkAndNotify(weather, forecastEveningToday);

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }

            if (hours == 20 && minutes == 0){
                try {
                    weather = parser.getCurrentWeather();
                    System.out.println(weather.toString());
                    csv.saveWeather(weather, "факт", "поздний вечер");
                    checkAndNotify(weather, null);

                    forecastMorningNextDay = parser.getForecast("утро");
                    csv.saveWeather(forecastMorningNextDay, "прогноз", "утро");
                    showForecast(forecastMorningNextDay, "утро");

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

    public static void showForecast(WeatherData forecast, String period){

        System.out.println("Прогноз погоды на " + period);
        System.out.println(forecast.toString());

        String desc = forecast.getDescription().toLowerCase();
        int temp = Integer.parseInt(forecast.getCurrent_temperature().replaceAll("[^0-9-]", ""));
        int wind = Integer.parseInt(forecast.getWind().replaceAll("[^0-9-]", ""));
        int pressure = Integer.parseInt(forecast.getPressure().replaceAll("[^0-9]", ""));
        int humidity = Integer.parseInt(forecast.getHumidity().replaceAll("[^0-9]", ""));

        // Температура
        if (temp > 25) {
            System.out.println("Очень жарко.");
        } else if (temp > 15) {
            System.out.println("Тепло. Хорошая погода для прогулок.");
        } else if (temp > 5) {
            System.out.println("Ожидается прохладная погода.");
        } else if (temp > 0) {
            System.out.println("Холодно, одевайтесь теплее.");
        } else if (temp < -5) {
            System.out.println("Ожидается мороз.");
        }

        // Ветер
        if (wind > 10) {
            System.out.println("Сильный ветер.");
        } else if (wind > 7) {
            System.out.println("Ветреная погода.");
        }

        // Давление
        if (pressure > 755) {
            System.out.println("Ожидается повышенное давление.");
        } else if (pressure < 735) {
            System.out.println("Ожидается пониженное давление, возможна сонливость.");
        }

        // Влажность
        if (humidity > 80) {
            System.out.println("Ожидается высокая влажность, может быть душно.");
        } else if (humidity < 30) {
            System.out.println("Ожидается сухой воздух.");
        }

    }

    public static void checkAndNotify(WeatherData weather, WeatherData forecast){
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
                int weatherWind = Integer.parseInt(weather.getWind().replaceAll("[^0-9.,]", ""));
                int forecastWind = Integer.parseInt(forecast.getWind().replaceAll("[^0-9.,]", ""));
                int windDiff = Math.abs(weatherWind - forecastWind);

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
                    System.out.println("  Прогноз полностью совпал с фактической погодой");
                }
            }
        }

    }

}
