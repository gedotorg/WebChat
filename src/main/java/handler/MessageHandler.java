package handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.ChatDAO;
import dao.MessageDAO;
import dao.UserChatDAO;
import dao.UserDAO;
import exception.DaoException;
import model.Chat;
import model.Message;
import model.User;
import model.UserChat;
import org.json.JSONObject;
import utils.WebSocketSessionManager;

import jakarta.websocket.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static dao.MessageDAO.*;

public class MessageHandler {

    public void handleMessage(Session session, String message){
        // Получаем текст сообщения от клиента
        JSONObject jsonRequest = new JSONObject(message);

        // Выполняем логику обработки сообщения
        System.out.println("Received message: " + message);

        String flag = jsonRequest.getString("flag");

        switch (flag) {
            case "FORWARD_MESSAGE":
                forwardMessage(jsonRequest);
                break;
            case "DELETE_CHANNEL":
                deleteChat(jsonRequest, session);
                break;
            case "CREATE_CHANNEL":
                createChat(jsonRequest, session);
                break;
            case "ADD_USER_TO_CHANNEL":
                addUserToChannel(jsonRequest, session);
                break;
            case "DELETE_USER_FROM_CHANNEL":
                deleteUserFromChat(jsonRequest, session);
                break;
        };

    }

    private void forwardMessage(JSONObject jsonRequest){
        int userId = jsonRequest.getInt("senderId");
        int chatId = jsonRequest.getInt("channelId");
        String text = jsonRequest.getString("text");

        Optional<User> userOptional = UserDAO.findById(userId);
        User user = userOptional.get();
        Optional<Chat> chatOptional = ChatDAO.findById(chatId);
        Chat chat = chatOptional.get();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {
            save(
                    new Message(0, chat, user, text, currentTime)
            );
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        String chatIdStr = String.valueOf(chat.getId());

        JsonObject jsonResponse = new JsonObject();

        jsonResponse.addProperty("flag", "FORWARD_MESSAGE");
        jsonResponse.addProperty("channelId", chatIdStr);
        jsonResponse.addProperty("sender", user.getName());
        jsonResponse.addProperty("text", text);

        List<User> userIds = UserChatDAO.findUsersByChatId(chatId);
        var sessionManager = WebSocketSessionManager.getInstance();

        List<Session> selectedSessions = new ArrayList<>();

        for (User chatUser: userIds) {
            if (sessionManager.contains(chatUser.getId())) {
                selectedSessions.add(sessionManager.getSession(chatUser.getId()));
            }
        }

        System.out.println(jsonResponse);
        String jsonResponseStr = jsonResponse.toString();

        for (Session addressee : selectedSessions) {
            try {
                if(addressee.isOpen()) {
                    addressee.getBasicRemote().sendText(jsonResponseStr);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            System.out.println("message delivered to " + selectedSessions.size() + "members of chat " + chat.getId());
        }
    }

    private void addUserToChannel(JSONObject jsonRequest, Session session){
        int userId = jsonRequest.getInt("userId");
        int chatId = jsonRequest.getInt("channelId");

        Optional<User> userOptional = UserDAO.findById(userId);
        User user = userOptional.get();
        Optional<Chat> chatOptional = ChatDAO.findById(chatId);
        Chat chat = chatOptional.get();

        try {
            UserChatDAO.save(
                    new UserChat(user, chat)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JsonObject userResponse = new JsonObject();
        JsonObject messageJson = new JsonObject();
        JsonArray messagesJson = new JsonArray();

        List<Message> messages = findMessagesByChatId(chatId);

        for(Message message: messages){
            messageJson.addProperty("sender", message.getUser().getName());
            messageJson.addProperty("text", message.getText());
            messagesJson.add(messageJson);
            messageJson = new JsonObject();
        }

        JsonObject adminResponse = new JsonObject();
        adminResponse.addProperty("flag", "ADD_USER_TO_CHANNEL");
        adminResponse.addProperty("userId", userId);
        adminResponse.addProperty("channelId", chatId);

        try {
            if(session.isOpen()) {
                session.getBasicRemote().sendText(adminResponse.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userResponse.addProperty("flag", "ADD_USER_TO_CHANNEL");
        userResponse.addProperty("id", chat.getId());
        userResponse.addProperty("title", chat.getName());
        userResponse.add("messages", messagesJson);

        try {
            if(WebSocketSessionManager.getInstance().getSession(userId) != null) {
                WebSocketSessionManager.getInstance().getSession(userId).getBasicRemote().sendText(userResponse.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void deleteUserFromChat(JSONObject jsonRequest, Session session){
        int userId = jsonRequest.getInt("userId");
        int chatId = jsonRequest.getInt("channelId");

        Optional<User> userOptional = UserDAO.findById(userId);
        User user = userOptional.get();
        Optional<Chat> chatOptional = ChatDAO.findById(chatId);
        Chat chat = chatOptional.get();

        boolean result = UserChatDAO.delete(
                new UserChat(user, chat)
        );

        System.out.println(result);


        JsonObject adminResponse = new JsonObject();
        adminResponse.addProperty("flag", "DELETE_USER_FROM_CHANNEL");
        adminResponse.addProperty("userId", userId);
        adminResponse.addProperty("channelId", chatId);

        try {
            if(session.isOpen()) {
                session.getBasicRemote().sendText(adminResponse.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonObject userResponse = new JsonObject();
        userResponse.addProperty("flag", "DELETE_USER_FROM_CHANNEL");
        userResponse.addProperty("channelId", chatId);

        try {
            if(WebSocketSessionManager.getInstance().getSession(userId) != null) {
                WebSocketSessionManager.getInstance().getSession(userId).getBasicRemote().sendText(userResponse.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void createChat(JSONObject jsonRequest, Session session){
        String name = jsonRequest.getString("title");

        Chat chat = new Chat(0, name);
        try {
            ChatDAO.save(chat);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Optional<User> userOptional = UserDAO.findByName("admin");
        User user = userOptional.get();

        try {
            UserChatDAO.save(
                    new UserChat(user, chat)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JsonObject adminResponse = new JsonObject();
        adminResponse.addProperty("flag", "CREATE_CHANNEL");
        adminResponse.addProperty("channelId", chat.getId());
        adminResponse.addProperty("title", chat.getName());

        try {
            if(session.isOpen()) {
                session.getBasicRemote().sendText(adminResponse.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private  void deleteChat(JSONObject jsonRequest, Session session){
        int chatId = jsonRequest.getInt("channelId");

        Optional<Chat> chatOptional = ChatDAO.findById(chatId);
        Chat chat = chatOptional.get();

        List<User> userIds = UserChatDAO.findUsersByChatId(chatId);
        var sessionManager = WebSocketSessionManager.getInstance();

        List<Session> selectedSessions = new ArrayList<>();

        for (User chatUser: userIds) {
            if (sessionManager.contains(chatUser.getId())) {
                selectedSessions.add(sessionManager.getSession(chatUser.getId()));
            }
        }

        JsonObject userResponse = new JsonObject();
        userResponse.addProperty("flag", "DELETE_CHANNEL");
        userResponse.addProperty("channelId", chatId);

        for (Session addressee : selectedSessions) {
            try {
                if(addressee.isOpen()) {
                    addressee.getBasicRemote().sendText(userResponse.toString());
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            System.out.println("message delivered to " + selectedSessions.size() + "members of chat " + chat.getId());
        }

        JsonObject adminResponse = new JsonObject();
        adminResponse.addProperty("flag", "DELETE_CHANNEL");
        adminResponse.addProperty("channelId", chatId);

        JsonArray userIdsJson = new JsonArray();
        for (User chatUser: userIds) {
            if(chatUser.getId() != 3) {
                userIdsJson.add(chatUser.getId());
            }
        }

        adminResponse.add("userIds", userIdsJson);

        try {
            if(session.isOpen()) {
                session.getBasicRemote().sendText(adminResponse.toString());
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        boolean result = UserChatDAO.deleteByChatId(chat);
        System.out.println(result);

        result = MessageDAO.deleteByChatId(chat);
        System.out.println(result);

        result = ChatDAO.delete(chat);
        System.out.println(result);

    }
}
