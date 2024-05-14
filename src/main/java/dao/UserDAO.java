package dao;

import exception.DaoException;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private final static UserDAO INSTANCE = new UserDAO();

    public static UserDAO getINSTANCE() {
        return INSTANCE;
    }

    private final static String SAVE_SQL = """ 
                                                INSERT INTO users
                                                (name, password, isadmin) 
                                                VALUES(?, ?, ?)                                               
                                                """;
    private final static String FIND_ALL_SQL = """ 
                                                SELECT id, name, password, isadmin
                                                from users                                 
                                                """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """ 
                                                WHERE id = ?                                
                                                """;
    private final static String FIND_BY_NAME_SQL = FIND_ALL_SQL + """ 
                                                WHERE name = ?                                
                                                """;

    private static User buildUser(ResultSet result) throws SQLException {
        return new User(
                result.getInt("id"),
                result.getString("name"),
                result.getString("password"),
                result.getBoolean("isadmin")
        );
    }

    public static User save(User user) throws SQLException {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setBoolean(3, user.isAdmin());

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();

            if(keys.next()){
                user.setId(keys.getInt("id"));
            }

            return user;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static List<User> findAll(){

        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL, Statement.RETURN_GENERATED_KEYS)) {
            List<User> users = new ArrayList<>();

            var result = statement.executeQuery();

            while (result.next()){
                users.add(
                        buildUser(result)
                );
            }

            return users;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static Optional<User> findById(int id){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var result = statement.executeQuery();

            User user = null;

            if (result.next()){
                user = buildUser(result);
            }
            return Optional.ofNullable(user);
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static Optional<User> findByName(String name){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_NAME_SQL)) {
            statement.setString(1, name);
            var result = statement.executeQuery();

            User user = null;

            if (result.next()){
                user = buildUser(result);
            }
            return Optional.ofNullable(user);
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

}
