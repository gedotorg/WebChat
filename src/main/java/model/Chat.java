package model;


public class Chat {
    private int id;
    private String name;

    // Конструкторы
    public Chat() {
    }

    public Chat(int id, String name) {
        this.id = id;
        this.name = name;
    }


    // Геттеры и сеттеры
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Переопределение метода toString() для удобства отладки
    @Override
    public String toString() {
        return "Chat{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                '}';
    }
}
