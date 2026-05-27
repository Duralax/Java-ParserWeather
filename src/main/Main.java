package main;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.regex.Matcher;

public class Main {

    public static String emailMessage = "";

    public static void main(String[] args) throws InterruptedException, IOException {

        //Parser parser = new Parser();
        //FileStorage csv = new FileStorage();
        // запуск парсинга для прогноза на части дня для утра следующего дня, дня, вечера
        WeatherData forecastMorningNextDay = null;
        WeatherData forecastDayToday = null;
        WeatherData forecastEveningToday = null;
        //WeatherData forecastNightToday = null;
        // для теста вывода погоды и
        WeatherData testForecast = Parser.getForecast("утро");
        WeatherData testWeather = Parser.getCurrentWeather();
        System.out.println(testWeather.toString());
        System.out.println(testForecast.toString());
        checkAndNotify(testWeather, testForecast);
        showForecast(testForecast, "-");
        EmailNotify.send(emailMessage);
        WeatherData weather;

        //System.out.println(forecastMorningNextDay.toString());
        while (true){
            LocalTime timeNow = LocalTime.now();
            int hours = timeNow.getHour();
            int minutes = timeNow.getMinute();

            if (hours == 8 && minutes == 30){
                try {
                    emailMessage = "";
                    weather = Parser.getCurrentWeather();
                    //System.out.println(weather.toString());
                    FileStorage.saveWeather(weather, "факт", "утро");
                    checkAndNotify(weather, forecastMorningNextDay);
                    
                    forecastDayToday = Parser.getForecast("день");
                    forecastEveningToday = Parser.getForecast("вечер");

                    FileStorage.saveWeather(forecastDayToday, "прогноз", "день");
                    //FileStorage.saveWeather(forecastEveningToday, "прогноз", "вечер");
                    showForecast(forecastDayToday, "день");
                    showForecast(forecastEveningToday, "вечер");
                    EmailNotify.send(emailMessage);
                    
                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
            if (hours == 12 && minutes == 30){
                try {
                    emailMessage = "";
                    weather = Parser.getCurrentWeather();
                    //System.out.println(weather.toString());
                    FileStorage.saveWeather(weather, "факт", "день");
                    checkAndNotify(weather, forecastDayToday);

                    forecastEveningToday = Parser.getForecast("вечер");
                    FileStorage.saveWeather(forecastEveningToday, "прогноз", "вечер");
                    showForecast(forecastEveningToday, "вечер");
                    EmailNotify.send(emailMessage);

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
            if (hours == 17 && minutes == 40){
                try {
                    emailMessage = "";
                    weather = Parser.getCurrentWeather();
                    //System.out.println(weather.toString());
                    FileStorage.saveWeather(weather, "факт", "вечер");
                    checkAndNotify(weather, forecastEveningToday);

                    //forecastNightToday = Parser.getForecast("ночь");
                    //FileStorage.saveWeather(forecastNightToday, "прогноз", "ночь");
                    //showForecast(forecastNightToday, "ночь");
                    EmailNotify.send(emailMessage);

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }

            if (hours == 20 && minutes == 0){
                try {
                    emailMessage = "";
                    weather = Parser.getCurrentWeather();
                    //System.out.println(weather.toString());
                    checkAndNotify(weather, null);

                    forecastMorningNextDay = Parser.getForecast("утро");
                    forecastMorningNextDay.setDateTime(LocalDateTime.now().plusDays(1));

                    FileStorage.saveWeather(forecastMorningNextDay, "прогноз", "утро");
                    showForecast(forecastMorningNextDay, "утро");

                    FileStorage.makeDailyReport(LocalDate.now());
                    EmailNotify.send(emailMessage);
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

        System.out.println("\nПрогноз погоды на " + period);
        emailMessage += "\n\nПрогноз погоды на " + period;
        System.out.println(forecast.toString());

        String desc = forecast.getDescription().toLowerCase();
        int temp = Integer.parseInt(forecast.getCurrent_temperature().replaceAll("[^0-9−]", ""));
        int wind = Integer.parseInt(forecast.getWind().replaceAll("[^0-9]", ""));
        int pressure = Integer.parseInt(forecast.getPressure().replaceAll("[^0-9]", ""));
        int humidity = Integer.parseInt(forecast.getHumidity().replaceAll("[^0-9]", ""));

        // Температура
        if (temp > 25) {
            System.out.println("Очень жарко.");
            emailMessage += "\nОчень жарко.";
        } else if (temp > 15) {
            System.out.println("Тепло.");
            emailMessage += "\nТепло.";
        } else if (temp > 5) {
            System.out.println("Ожидается прохладная погода.");
            emailMessage += "\nОжидается прохладная погода.";
        } else if (temp > 0) {
            System.out.println("Холодно, одевайтесь теплее.");
            emailMessage += "\nХолодно, одевайтесь теплее.";
        } else if (temp < -5) {
            System.out.println("Ожидается мороз.");
            emailMessage += "\nОжидается мороз.";
        }

        // Ветер
        if (wind > 10) {
            System.out.println("Сильный ветер.");
            emailMessage += "\nСильный ветер.";
        } else if (wind > 7) {
            System.out.println("Ветреная погода.");
            emailMessage += "\nВетреная погода.";
        }

        // Давление
        if (pressure > 755) {
            System.out.println("Ожидается повышенное давление.");
            emailMessage += "\nОжидается повышенное давление.";
        } else if (pressure < 735) {
            System.out.println("Ожидается пониженное давление, возможна сонливость.");
            emailMessage += "\nОжидается пониженное давление, возможна сонливость.";
        }

        // Влажность
        if (humidity > 80) {
            System.out.println("Ожидается высокая влажность, может быть душно.");
            emailMessage += "\nОжидается высокая влажность, может быть душно.";
        } else if (humidity < 30) {
            System.out.println("Ожидается сухой воздух.");
            emailMessage += "\nОжидается сухой воздух.";
        }

    }

    public static void checkAndNotify(WeatherData weather, WeatherData forecast){
        System.out.println("\n--------- Погода ---------");
        emailMessage += "\n--------- Погода ---------";
        // Текущая погода
        System.out.println("Сейчас: " + weather.toString());
        emailMessage += "\nСейчас: " + weather.toString();
        // Если прогноз есть
        if (forecast != null) {
            System.out.println("Отличия от прогноза:");
            emailMessage += "\nОтличия от прогноза:";
            boolean hasDiff = false;

            // Температура
            if (weather.getCurrent_temperature().equals("None") || forecast.getCurrent_temperature().equals("None")) {
                System.out.println("Данные для сравнения неполные");
                emailMessage += "\nДанные для сравнения неполные";
            } else {
                int weatherTemp = Integer.parseInt(weather.getCurrent_temperature().replaceAll("[^0-9−]", ""));
                int forecastTemp = Integer.parseInt(forecast.getCurrent_temperature().replaceAll("[^0-9−]", ""));
                int tempDiff = weatherTemp - forecastTemp;

                if (Math.abs(tempDiff) >= 4) {
                    if (tempDiff > 0){
                        System.out.println("  Теплее на " +  Math.abs(tempDiff) + " °C");
                        emailMessage += "\n  Теплее на " +  Math.abs(tempDiff) + " °C";
                    } else {
                        System.out.println("  Холоднее на " +  Math.abs(tempDiff) + " °C");
                        emailMessage += "\n  Холоднее на " +  Math.abs(tempDiff) + " °C";
                    }
                    hasDiff = true;
                }

            }

            // Ощущается как
            if (weather.getTemperature_feel().equals("None") || forecast.getTemperature_feel().equals("None")) {
                System.out.println("Данные для сравнения неполные");
                emailMessage += "\nДанные для сравнения неполные";
            } else {
                int weatherFeel = Integer.parseInt(weather.getTemperature_feel().replaceAll("[^0-9-]", ""));
                int forecastFeel = Integer.parseInt(forecast.getTemperature_feel().replaceAll("[^0-9-]", ""));
                int feelDiff = weatherFeel - forecastFeel;

                if (Math.abs(feelDiff) >= 4) {
                    if (feelDiff > 0){
                        System.out.println("  Ощущается теплее на " +  Math.abs(feelDiff) + " °C");
                        emailMessage += "\n  Ощущается теплее на " +  Math.abs(feelDiff) + " °C";
                    } else {
                        System.out.println("  Ощущается холоднее на " +  Math.abs(feelDiff) + " °C");
                        emailMessage += "\n  Ощущается холоднее на " +  Math.abs(feelDiff) + " °C";
                    }
                    hasDiff = true;
                }
            }

            // Описание !поменять на более корректное сравнение для описаний (в прогнозе не такое полное)!
            if (weather.getDescription().equals("None") || forecast.getDescription().equals("None")) {
                System.out.println("Данные для сравнения неполные");
                emailMessage += "\nДанные для сравнения неполные";
            } else {
                if (!weather.getDescription().split("\\.")[0].equalsIgnoreCase(forecast.getDescription())) {
                    System.out.println("  Описание: " + forecast.getDescription() + " -> " + weather.getDescription().split("\\.")[0]);
                    emailMessage += "\n  Описание: " + forecast.getDescription() + " -> " + weather.getDescription().split("\\.")[0];
                    hasDiff = true;
                }

            }

            // Давление разница 10+ мм
            if (weather.getPressure().equals("None") || forecast.getPressure().equals("None")) {
                System.out.println("Данные для сравнения неполные");
                emailMessage += "\nДанные для сравнения неполные";
            }else {
                int weatherPress = Integer.parseInt(weather.getPressure().replaceAll("[^0-9]", ""));
                int forecastPress = Integer.parseInt(forecast.getPressure().replaceAll("[^0-9]", ""));
                int pressDiff = Math.abs(weatherPress - forecastPress);

                if (pressDiff >= 10) {
                    System.out.println("  Давление: " + forecast.getPressure() + " стало " + weather.getPressure() + " мм рт. ст.");
                    emailMessage += "\n  Давление: " + forecast.getPressure() + " стало " + weather.getPressure() + " мм рт. ст.";
                    hasDiff = true;
                }
            }
            // Ветер разница 2+
            if (weather.getWind().equals("None") || forecast.getWind().equals("None")) {
                System.out.println("Данные для сравнения неполные");
                emailMessage += "\nДанные для сравнения неполные";
            } else {

                Matcher weatherReg = java.util.regex.Pattern.compile("\\d+,?\\d*").matcher(weather.getWind());
                Matcher forecastReg = java.util.regex.Pattern.compile("\\d+,?\\d*").matcher(forecast.getWind());
                if (weatherReg.find() && forecastReg.find()) {
                    double weatherWind = Double.parseDouble(weatherReg.group().replace(',', '.'));
                    double forecastWind = Double.parseDouble(forecastReg.group().replace(',', '.'));
                    double windDiff = Math.abs(weatherWind - forecastWind);

                    if (windDiff >= 2) {
                        System.out.println("  Ветер было " + weatherWind + " м/с стало " + forecastWind + " м/с");
                        emailMessage += "\n  Ветер было " + weatherWind + " м/с стало " + forecastWind + " м/с";
                        hasDiff = true;
                    }
                }

            }

            // Влажность разница 10+
            if (weather.getHumidity().equals("None") || forecast.getHumidity().equals("None")) {
                System.out.println("Данные для сравнения неполные");
                emailMessage += "\nДанные для сравнения неполные";
            } else {
                int weatherHum = Integer.parseInt(weather.getHumidity().replaceAll("[^0-9]", ""));
                int forecastHum = Integer.parseInt(forecast.getHumidity().replaceAll("[^0-9]", ""));
                int humDiff = Math.abs(weatherHum - forecastHum);

                if (humDiff >= 10) {
                    System.out.println("  Влажность было " + forecast.getHumidity() + " стало " + weather.getHumidity());
                    emailMessage += "\n  Влажность было " + forecast.getHumidity() + " стало " + weather.getHumidity();
                    hasDiff = true;
                }

                if (!hasDiff) {
                    System.out.println("Прогноз полностью совпал с фактической погодой");
                    emailMessage += "\nПрогноз полностью совпал с фактической погодой";

                }
            }
        }

    }

}
