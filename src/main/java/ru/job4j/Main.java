package ru.job4j;

import org.apache.log4j.Logger;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;
import ru.job4j.grabber.stores.MemStore;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final org.apache.log4j.Logger log = Logger.getLogger(Main.class);
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
            var post = new Post();
            post.setTitle("Super Java Job");
            store.save(post);
            System.out.println(store.getAll());
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store);
            Thread.sleep(10000);
        } catch (SQLException e) {
            log.error("When create a connection", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}