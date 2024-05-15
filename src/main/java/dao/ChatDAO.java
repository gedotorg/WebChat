package dao;

import exception.DaoException;
import model.Chat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatDAO {
    private final static ChatDAO INSTANCE = new ChatDAO();

    public static ChatDAO getINSTANCE() {
        return INSTANCE;
    }

    private final static String SAVE_SQL = """ 
                                                INSERT INTO chats
                                                (name) 
                                                VALUES(?)                                               
                                                """;
    private final static String FIND_ALL_SQL = """ 
                                                SELECT id, name
                                                from chats                                  
                                                """;
    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """ 
                                                WHERE id = ?                                  
                                                """;
    private final static String DELETE_SQL = """ 
                                                DELETE FROM chats
                                                WHERE id = ?                                  
                                                """;

    private static Chat buildChat(ResultSet result) throws SQLException{
        return new Chat(
                result.getInt("id"),
                result.getString("name")
        );
    }

    public static Chat save(Chat chat) throws SQLException {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, chat.getName());

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();

            if(keys.next()){
                chat.setId(keys.getInt("id"));
            }

            return chat;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static List<Chat> findAll(){

        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL, Statement.RETURN_GENERATED_KEYS)) {
            List<Chat> chats = new ArrayList<>();

            var result = statement.executeQuery();

            while (result.next()){
                chats.add(
                        new Chat(
                                result.getInt("id"),
                                result.getString("name")
                        )
                );
            }

            return chats;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static Optional<Chat> findById(int id){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var result = statement.executeQuery();

            Chat chat = null;

            if (result.next()){
                chat = buildChat(result);
            }
            return Optional.ofNullable(chat);
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static boolean delete(Chat chat){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, chat.getId());

            return statement.executeUpdate() > 0;

        }catch (SQLException e){
            throw new DaoException(e);
        }
    }
}
