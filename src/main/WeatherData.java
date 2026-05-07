package main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherData {

    private LocalDateTime dateTime;
    private String city;
    private String current_temperature;
    private String temperature_feel;
    private String description;
    private String pressure;
    private String wind;
    private String humidity;

    public WeatherData(){
        this.dateTime = LocalDateTime.now();
    }
    
    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public String getCity() {
        return city;
    }

    public String getCurrent_temperature() {
        return current_temperature;
    }

    public String getTemperature_feel() {
        return temperature_feel;
    }

    public String getDescription() {
        return description;
    }

    public String getPressure() {
        return pressure;
    }

    public String getWind() {
        return wind;
    }

    public String getHumidity() {
        return humidity;
    }


    public void setCity(String city) {
        this.city = city;
    }

    public void setCurrent_temperature(String current_temperature) {
        this.current_temperature = current_temperature;
    }

    public void setTemperature_feel(String temperature_feel) {
        this.temperature_feel = temperature_feel;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = dateTime.format(formatter);
        return "\nПогода для города: " + city + "\n" +
                "  Дата и время: " + formattedDate + "\n" +
                "  Температура: " + current_temperature + " °C по ощущениям как " + temperature_feel + " °C \n" +
                "  Описание: " + description + "\n" +
                "  Ветер: " + wind + "\n" +
                "  Давление: " + pressure + " мм рт. ст. " + "\n" +
                "  Влажность: " + humidity + "\n";
    }
}
