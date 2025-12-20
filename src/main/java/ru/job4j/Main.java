package ru.job4j;

import org.apache.log4j.Logger;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.*;
import ru.job4j.grabber.stores.JdbcStore;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) throws ClassNotFoundException {

        var config = new Config();
        config.load("application.properties");
        Class.forName(config.get("db.driver-class-name"));
        try (var connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password")
        );
             var scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);

            Parse habrParser = new HabrCareerParse(new HabrCareerDateTimeParser());
            List<Post> habrPosts = habrParser.fetch();
            habrPosts.forEach(store::save);

            System.out.println(store.getAll());
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store);
            new Web(store).start(Integer.parseInt(config.get("server.port")));
        } catch (SQLException e) {
            LOG.error("When create a connection", e);
        }
    }
}