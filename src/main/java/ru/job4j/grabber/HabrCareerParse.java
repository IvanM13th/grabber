package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareeDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com/";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse hcp = new HabrCareerParse(new HabrCareeDateTimeParser());
        hcp.list(PAGE_LINK);
    }

    private String retrieveDescription(String link) {
        String text;
        try {
            text = Jsoup.connect(link).get()
                    .select(".style-ugc")
                    .text();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        return text;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 1; i++) {
            Connection connection = Jsoup.connect(String.format("%s?page=%s", link, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element title = row.select(".vacancy-card__title").first();
                String vTitle = title.text();
                String vLink = String.format("%s%s", SOURCE_LINK, title.child(0).attr("href"));
                String vDate = row.select(".vacancy-card__date").first().child(0).attr("datetime");
                String vDesc = retrieveDescription(vLink);
                postList.add(
                        new Post(
                                vTitle,
                                vLink,
                                vDesc,
                                dateTimeParser.parse(vDate))
                );
            });
        }
        return postList;
    }
}
