package dao;

import exception.DaoException;
import model.Chat;
import model.Message;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDAO {
    private final static MessageDAO INSTANCE = new MessageDAO();

    public static MessageDAO getInstance(){
        return INSTANCE;
    }

    private final static String SAVE_SQL = """ 
                                                INSERT INTO messages
                                                (user_id, chat_id, text, timestamp) 
                                                VALUES(?, ?, ?, ?)                                               
                                                """;
    private final static String FIND_ALL_SQL = """ 
                                                SELECT m.id, chat_id, c.name as chatname, user_id, u.name as username, u.password, u.isadmin, m.text, m.timestamp
                                                from messages m
                                                JOIN users u on u.id = m.user_id
                                                JOIN chats c on c.id = m.chat_id                                    
                                                """;
    private final static String FIND_MESSAGES_BY_CHAT_ID_SQL = FIND_ALL_SQL + """ 
                                                WHERE chat_id = ?
                                                ORDER BY m.timestamp                                    
                                                """;
    private final static String DELETE_BY_CHAT_ID_SQL = """
                                             DELETE FROM messages
                                             WHERE chat_id = ?
                                             """;

    private static Message buildMessage(ResultSet result) throws SQLException{
        var chat = new Chat(
                result.getInt("chat_id"),
                result.getString("chatname")
        );
        var user = new User(
                result.getInt("user_id"),
                result.getString("username"),
                result.getString("password"),
                result.getBoolean("isadmin")
        );
        return new Message(
                result.getInt("id"),
                chat,
                user,
                result.getString("text"),
                result.getTimestamp("timestamp")
        );
    }

    public static Message save(Message message) throws SQLException {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, message.getUser().getId());
            statement.setInt(2, message.getChat().getId());
            statement.setString(3, message.getText());
            statement.setTimestamp(4, message.getTimestamp());

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();

            if(keys.next()){
                message.setId(keys.getInt("id"));
            }

            return message;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static List<Message> findAll(){

        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Message> messages = new ArrayList<>();

            var result = statement.executeQuery();

            while (result.next()){
                messages.add(
                    buildMessage(result)
                );
            }

            return messages;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static List<Message> findMessagesByChatId(int chatId){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_MESSAGES_BY_CHAT_ID_SQL)) {
            statement.setInt(1, chatId);
            List<Message> messages = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()){
                messages.add(
                        buildMessage(result)
                );
            }

            return messages;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static boolean deleteByChatId(Chat chat){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_BY_CHAT_ID_SQL)) {

            statement.setInt(1, chat.getId());

            return statement.executeUpdate() > 0;

        }catch (SQLException e){
            throw new DaoException(e);
        }
    }
}
