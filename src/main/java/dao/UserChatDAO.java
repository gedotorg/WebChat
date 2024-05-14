package dao;

import exception.DaoException;
import model.Chat;
import model.User;
import model.UserChat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserChatDAO {
    private final static UserChatDAO INSTANCE = new UserChatDAO();

    public static UserChatDAO getINSTANCE() {
        return INSTANCE;
    }

    private final static String SAVE_SQL = """ 
                                                INSERT INTO user_chat
                                                (user_id, chat_id) 
                                                VALUES(?, ?)                                               
                                                """;
    private final static String FIND_ALL_SQL = """ 
                                                SELECT user_id, u.name as username, u.password, u.isadmin, chat_id, c.name as chatname
                                                from user_chat uc
                                                JOIN chats c on c.id = uc.chat_id
                                                JOIN users u on u.id = uc.user_id                                    
                                                """;
    private final static String FIND_CHATS_BY_USER_ID_SQL = """ 
                                                SELECT c.id, c.name
                                                from user_chat uc
                                                JOIN chats c on c.id = uc.chat_id
                                                JOIN users u on u.id = uc.user_id 
                                                WHERE u.id = ?                                  
                                                """;
    private final static String FIND_USERS_BY_CHAT_ID_SQL = """
                                                SELECT u.id, u.name, u.password, u.isadmin
                                                from user_chat uc
                                                JOIN chats c on c.id = uc.chat_id
                                                JOIN users u on u.id = uc.user_id
                                                WHERE c.id = ?
                                                """;

    private final static String DELETE_SQL = """
                                             DELETE FROM user_chat
                                             WHERE user_id = ? AND chat_id = ?
                                             """;
    private final static String DELETE_BY_CHAT_ID_SQL = """
                                             DELETE FROM user_chat
                                             WHERE chat_id = ?
                                             """;

    private UserChat buildUserChat(ResultSet result) throws SQLException {
        var user = new User(
                result.getInt("user_id"),
                result.getString("username"),
                result.getString("password"),
                result.getBoolean("isAdmin")
        );
        var chat = new Chat(
                result.getInt("chat_id"),
                result.getString("chatname")
        );
        return new UserChat(
                user,
                chat
        );
    }

    public static void save(UserChat userChat) throws SQLException {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userChat.getUser().getId());
            statement.setInt(2, userChat.getChat().getId());

            statement.executeUpdate();
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public List<UserChat> findAll(){

        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL, Statement.RETURN_GENERATED_KEYS)) {
            List<UserChat> userChats = new ArrayList<>();

            var result = statement.executeQuery();

            while (result.next()){
                userChats.add(
                        buildUserChat(result)
                );
            }

            return userChats;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static List<Chat> findChatsByUserId(int userId){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_CHATS_BY_USER_ID_SQL)) {
            statement.setInt(1, userId);
            var result = statement.executeQuery();

            List<Chat> chats = new ArrayList<>();

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

    public static List<User> findUsersByChatId(int chatId){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_USERS_BY_CHAT_ID_SQL)) {
            statement.setInt(1, chatId);
            var result = statement.executeQuery();

            List<User> users= new ArrayList<>();

            while (result.next()){
                users.add(
                        new User(
                                result.getInt("id"),
                                result.getString("name"),
                                result.getString("password"),
                                result.getBoolean("isadmin")
                        )
                );
            }
            return users;
        }
        catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static boolean delete(UserChat userChat){
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, userChat.getUser().getId());
            statement.setInt(2, userChat.getChat().getId());

            return statement.executeUpdate() > 0;

        }catch (SQLException e){
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