package model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private Chat chat;
    private User user;
    private String text;
    private Timestamp timestamp;

    // Конструкторы
    public Message() {
    }

    public Message(int id, Chat chat, User user, String text, Timestamp timestamp) {
        this.id = id;
        this.chat = chat;
        this.user = user;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Переопределение метода toString() для удобства отладки
    @Override
    public String toString() {
        return "Message{" +
               "id=" + this.id +
               ", chat=" + this.chat +
               ", user=" + this.user +
               ", text='" + this.text + '\'' +
               ", timestamp=" + this.timestamp +
               '}';
    }
}
