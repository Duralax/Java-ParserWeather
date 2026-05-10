package main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Parser {
    private static final String URL_NOW = "https://yandex.ru/pogoda/ru/yekaterinburg";
    private static final String URL_DETAILS = "https://yandex.ru/pogoda/ru/yekaterinburg/details";
    //private static final String URL_COMMON = "https://www.gismeteo.ru/weather-yekaterinburg-4517/now/";

    public WeatherData getCurrentWeather() throws IOException{
        Document doc = Jsoup.connect(URL_NOW).userAgent("Chrome/4.0.249.0 Safari/532.5")
                .timeout(10000)
                .get();
        WeatherData weather = new WeatherData();

        weather.setCity("Екатеринбург");
        //System.out.println("1");
        Element cur_temp_div = doc.selectFirst("[class*=AppLayoutCommon_overlay]");
        System.out.println(cur_temp_div);

        // Для текущей температуры - по строкам разбит знак и значение у погоды

        Element tempElement = doc.selectFirst("[class*=AppFactTemperature_value]");
        Element signElement = doc.selectFirst("[class*=AppFactTemperature_sign]");
        if (tempElement != null && signElement != null) {
            String temp = tempElement.text();
            String sign = signElement.text();
            weather.setCurrent_temperature(sign + temp);
        } else {
            weather.setCurrent_temperature("None");
        }

        // Для ощущается как - в строке будет ощущается как что-то

        Element tempFeelElement = doc.selectFirst("[class*=AppFact_feels__base]");

        if (tempFeelElement != null) {
            String tempFeel = tempFeelElement.text();
            tempFeel = tempFeel.replaceAll("[^0-9+−]", "");
            weather.setTemperature_feel(tempFeel);
        } else {
            weather.setTemperature_feel("None");
        }

        // Для описания погоды (убрать или адаптировать вторую часть описания, можно оставить для прогноза только в начале дня)

        Element descElement = doc.selectFirst("[class*=AppFact_warning__first_text]");
        Element secondDescElement = doc.selectFirst("[class*=AppFact_warning__second]");

        if (descElement != null) {
            String description = descElement.text() + ". ";
            if (secondDescElement != null){
                description += secondDescElement.text();
            }
            weather.setDescription(description);
        } else {
            weather.setDescription("None");
        }


        // Для данных с давлением, ветром и влажностью

        Elements details = doc.select("[class*=AppFact_details__item]");

        if (details.size() >= 3) {
            // Для ветра (первый элемент)
            weather.setWind(details.get(0).text());

            // Для давления (второй элемент)
            weather.setPressure(details.get(1).text());

            // Для влажности (третий элемент)
            weather.setHumidity(details.get(2).text());

        } else {
            weather.setWind("None");
            weather.setPressure("None");
            weather.setHumidity("None");
        }

        return weather;
    }

    public WeatherData getForecast(String dayPart) throws IOException {
        Document doc = Jsoup.connect(URL_DETAILS).userAgent("Chrome/4.0.249.0 Safari/532.5")
                .timeout(10000)
                .get();

        WeatherData forecast = new WeatherData();
        forecast.setCity("Екатеринбург");

        System.out.println(doc.body());

        String part = "";
        Element card = null;
        if (dayPart.equals("утро")){
            Elements cards = doc.select("li[class*=AppForecastDay_dayCard]");
            System.out.println("КАРТОЧКИ");
            System.out.println("\n" + "\n" + cards);
            card = cards.get(1);
            part = "m";
        } else if (dayPart.equals("день") || dayPart.equals("вечер")) {
            card = doc.selectFirst("li[class*=AppForecastDay_dayCard]");
            if (dayPart.equals("день")){
                part = "d";
            } else if (dayPart.equals("вечер")){
                part = "e";
            }
        }
        // Температура
        Element tempElement = card.selectFirst("[style*='grid-area:" + part + "-temp']");
        if (tempElement != null) {
            forecast.setCurrent_temperature(tempElement.text().replace("°", ""));
        } else {
            forecast.setCurrent_temperature("None");
        }
        // Температура по ощущениям
        Element feelsTempElement = card.selectFirst("[style*='grid-area:" + part + "-feels']");
        if (feelsTempElement != null) {
            forecast.setTemperature_feel(feelsTempElement.text().replace("°", ""));
        } else {
            forecast.setTemperature_feel("None");
        }

        // Описание
        Element descElement = card.selectFirst("[style*='grid-area:" + part + "-text']");
        if (descElement != null) {
            forecast.setDescription(descElement.text());
        }else {
            forecast.setDescription("None");
        }

        // Ветер
        Element windElement = card.selectFirst("[style*='grid-area:" + part + "-wind']");
        if (windElement != null) {
            forecast.setWind(windElement.text() + " м/c");
        }else {
            forecast.setWind("None");
        }

        // Влажность
        Element humElement = card.selectFirst("[style*='grid-area:" + part + "-hum']");
        if (humElement != null) {
            forecast.setHumidity(humElement.text());
        }else {
            forecast.setHumidity("None");
        }

        // Давление
        Element pressElement = card.selectFirst("[style*='grid-area:" + part + "-press']");
        if (pressElement != null) {
            forecast.setPressure(pressElement.text());
        }else {
            forecast.setPressure("None");
        }

        return forecast;
    }

}
