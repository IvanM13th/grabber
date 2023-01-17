package ru.job4j.grabber;

import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareeDateTimeParser;

import java.io.InputStream;
import java.security.PrivateKey;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(
                cfg.getProperty("url"),
                cfg.getProperty("username"),
                cfg.getProperty("password")
        );
    }

    public static void main(String[] args) throws SQLException {
        PsqlStore store = new PsqlStore(readProperties());
        store.save(new Post("testName", "testText", "testLink7", LocalDateTime.now()));
        store.save(new Post("testName2", "testText2", "testLink9", LocalDateTime.now()));
        List<Post> posts = store.getAll();
        for (var p : posts) {
            System.out.println(p);
        }
        System.out.println(store.findById(1));
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post (name, text, link, created) values(?,?,?,?) on conflict (link) do nothing", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet ids = statement.getGeneratedKeys()) {
                if (ids.next()) {
                    post.setId(ids.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    posts.add(setValues(set));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement("select * from post where id=?")) {
            statement.setInt(1, id);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    post = setValues(set);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    private Post setValues(ResultSet set) throws SQLException {
        Post post = new Post();
        post.setId(set.getInt("id"));
        post.setTitle(set.getString("name"));
        post.setDescription(set.getString("text"));
        post.setLink(set.getString("link"));
        post.setCreated(Timestamp.valueOf(set.getString("created")).toLocalDateTime());
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    private static Properties readProperties() {
        Properties config = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            config.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }
}
