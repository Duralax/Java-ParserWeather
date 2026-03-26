package main;

public class Main {
    public static void main(String[] args) {

        Parser parser = new Parser();

        try {
            WeatherData weather = parser.getCurrentWeather();
            System.out.println(weather);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
