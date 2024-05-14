package model;


import java.util.Objects;

public class User {
    private int id;
    private String name;
    private String password;
    boolean isAdmin = false;

    public User() {
    }

    public User(int id, String name, String password, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + this.id +
               ", username='" + this.name + '\'' +
               ", password='" + this.password + '\'' +
               ", isAdmin='" + this.isAdmin + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || getClass() != obj.getClass()){
            return false;
        }
        User otherUser = (User) obj;
        return Objects.equals(this.id, otherUser.getId());
    }
}
