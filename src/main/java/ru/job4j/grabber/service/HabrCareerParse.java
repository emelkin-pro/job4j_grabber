package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int TOTAL_PAGES = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            for (int i = 1; i <= TOTAL_PAGES; i++) {
                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, i, SUFFIX);
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                htmlParser(document, result);
            }
        } catch (IOException e) {
            LOG.error("When load page", e);
        }
        return result;
    }

    private void htmlParser(Document document, List<Post> result) {
        var rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            var titleElement = row.select(".vacancy-card__title").first();
            var linkElement = titleElement.child(0);
            var time = dateTimeParser.parse(row
                    .select(".vacancy-card__date time")
                    .attr("datetime"));
            long timeElement = time.toEpochSecond(ZoneOffset.UTC);
            String vacancyName = titleElement.text();
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            String description = retrieveDescription(link);
            var post = new Post();
            post.setTitle(vacancyName);
            post.setLink(link);
            post.setTime(timeElement);
            post.setDescription(description);
            result.add(post);
        });
    }

    private String retrieveDescription(String link) {
        StringBuilder rsl = new StringBuilder();
        try {
            var connectionDescr = Jsoup.connect(link);
            var documentDescr = connectionDescr.get();
            var rows = documentDescr.select(".vacancy-description__text");
            rows.forEach(row -> rsl.append(row.text()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rsl.toString();
    }

    public static void main(String[] args) {
        Parse parse = new HabrCareerParse(new HabrCareerDateTimeParser());
        parse.fetch().forEach(System.out::println);
    }
}