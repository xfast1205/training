import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static Document getPage(){
        String url = "http://www.pogoda.spb.ru/";
        Document page = null;
        try {
            page = Jsoup.parse(new URL(url), 3000);                             //url и время ожидания
        } catch (IOException e) {
            e.printStackTrace();
        }
        return page;
    }

    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");                   // шаблон 22.22

    private static String getDateFromString(String stringDate) throws Exception {
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Can't extract date!");
    }

    private static int printPartValues(Elements values, int index){                      //index - начало печати
        int iterationCount = 4;
        if (index == 0){
            Element valueLn = values.get(3);
            boolean isMorning = valueLn.text().contains("Утро");
            if (isMorning){
                iterationCount = 3;
            }
        }
        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);
            for (Element td : valueLine.select("td")) {
                System.out.print(td.text() + "    ");
            }
            System.out.println();
            }
        return iterationCount;
    }

    public static void main(String[] args) {
        Document page = getPage();
        Element tableWeather = page.select("table[class=wt]").first();          //CSS QL запрос
        Elements names = tableWeather.select("tr[class=wth]");
        Elements values = tableWeather.select("tr[valign=top]");
        int index = 0;
        for (Element name :
                names) {
            String dateString = name.select("th[id=dt]").text();
            String date = null;
            try {
                date = getDateFromString(dateString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(date + "    Явления    Температура    Давление    Влажность    Ветер");
            int iterationCount = printPartValues(values, index);
            index += iterationCount;
        }
    }
}