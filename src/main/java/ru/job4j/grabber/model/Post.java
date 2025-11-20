package ru.job4j.grabber.model;

import java.util.Objects;

public class Post {
    long id;
    String title = new String();
    String link = new String();
    String description = new String();
    long time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return time == post.time && Objects.equals(title, post.title) && Objects.equals(link, post.link) && Objects.equals(description, post.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link, description, time);
    }
}
