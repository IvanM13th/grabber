package ru.job4j.grabber;

import org.jsoup.Jsoup;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com/";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {

        for (int i = 1; i <= 5; i++) {
            Connection connection = Jsoup.connect(String.format("%s?page=%s", PAGE_LINK, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String vacancyDate = row.select(".vacancy-card__date").first().child(0).attr("datetime");
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s%n", vacancyDate, vacancyName, link);
            });
        }
    }

    private String retrieveDescription(String link) throws IOException {
        return Jsoup.connect(link).get()
                .select(".vacancy-description__text")
                .first()
                .child(0)
                .text();
    }
}
