package model;

public class UserChat {
    private User user;
    private Chat chat;

    // Конструкторы
    public UserChat() {
    }

    public UserChat(User user, Chat chat) {
        this.user = user;
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public String toString() {
        return "UserChat{" +
               "userId=" + user +
               ", chatId=" + chat +
               '}';
    }
}
