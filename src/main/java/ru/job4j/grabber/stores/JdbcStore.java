package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


public class JdbcStore implements ru.job4j.grabber.stores.Store {
    private Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        long millis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(millis);
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO post(name, text, link, created) VALUES (?, ?, ?, ?)"
                        + "ON CONFLICT (link) DO NOTHING;",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, timestamp);
            statement.execute();
            try (ResultSet generatedID = statement.getGeneratedKeys()) {
                if (generatedID.next()) {
                    post.setId(generatedID.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM post",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.execute();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Post post = new Post();
                    post.setId(resultSet.getInt("id"));
                    post.setTitle(resultSet.getString("name"));
                    post.setDescription(resultSet.getString("text"));
                    post.setLink(resultSet.getString("link"));
                    post.setTime(resultSet.getTimestamp("created").getTime());
                    rsl.add(post);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rsl;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.empty();
    }
}