package main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Parser {
    private static final String URL_NOW = "https://yandex.ru/pogoda/ru/yekaterinburg";
    //private static final String URL_COMMON = "https://www.gismeteo.ru/weather-yekaterinburg-4517/now/";

    public WeatherData getCurrentWeather() throws IOException{
        Document doc = Jsoup.connect(URL_NOW).userAgent("Chrome/4.0.249.0 Safari/532.5")
                .timeout(10000)
                .get();
        WeatherData weather = new WeatherData();

        weather.setCity("Екатеринбург");
        System.out.println("1");
        Element cur_temp_div = doc.selectFirst(".AppLayoutCommon_overlay__h5IHD");
        System.out.println(cur_temp_div);

        // Для текущей температуры - по строкам разбит знак и значение у погоды

        Element tempElement = doc.selectFirst(".AppFactTemperature_value__2qhsG");
        Element signElement = doc.selectFirst(".AppFactTemperature_sign__1MeN4");
        if (tempElement != null && signElement != null) {
            String temp = tempElement.text();
            String sign = signElement.text();
            weather.setCurrent_temperature(sign + temp);
        } else {
            weather.setCurrent_temperature("None");
        }

        // Для ощущается как - в строке будет Ощущается как что-то

        Element tempFeelElement = doc.selectFirst(".AppFact_feels__base__bw86b");

        if (tempFeelElement != null) {
            String tempFeel = tempFeelElement.text();
            tempFeel = tempFeel.replaceAll("[^0-9+-]", "");
            weather.setTemperature_feel(tempFeel);
        } else {
            weather.setCurrent_temperature("None");
        }

        // Для описания погоды (убрать или адаптировать вторую часть описания, можно оставить для прогноза только в начале дня)

        Element descElement = doc.selectFirst(".AppFact_warning__first_text___wtkV");
        Element secondDescElement = doc.selectFirst(".AppFact_warning__second__BMdKC");

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

        Elements details = doc.select(".AppFact_details__item__QFIXI");

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

}
