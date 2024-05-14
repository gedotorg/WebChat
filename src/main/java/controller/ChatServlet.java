package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.ChatDAO;
import dao.MessageDAO;
import dao.UserChatDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chat;
import model.Message;
import model.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        BufferedReader reader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String requestBody = stringBuilder.toString();

        String userIdStr;
        try {
            JSONObject jsonObject = new JSONObject(requestBody);
            userIdStr = jsonObject.getString("userId");

            if (userIdStr != null && !userIdStr.isEmpty()) {
                int userId = Integer.parseInt(userIdStr);

                Optional<User> userOptional = UserDAO.findById(userId);
                User user = userOptional.get();

                List<Chat> chats = UserChatDAO.findChatsByUserId(userId);
                List<Message> messages = new ArrayList<>();

                JsonObject jsonResponse = new JsonObject();

                JsonObject messageJson = new JsonObject();
                JsonArray messagesJsonArray = new JsonArray();

                JsonObject chatJson = new JsonObject();
                JsonArray chatsJsonArray = new JsonArray();

                JsonObject adminData = new JsonObject();

                JsonObject userJson = new JsonObject();
                JsonArray usersJsonArray = new JsonArray();


                //формирование JSON для отправки
                jsonResponse.addProperty("userName", user.getName());
                jsonResponse.addProperty("isAdmin", user.isAdmin());

                if(user.isAdmin()){
                    List<User> allUsers = UserDAO.findAll();
                    allUsers.remove(user);
                    for(User user1: allUsers){
                        userJson.addProperty("id", user1.getId());
                        userJson.addProperty("name", user1.getName());
                        List<Chat> userChats = UserChatDAO.findChatsByUserId(user1.getId());
                        for(Chat chat: userChats){
                            chatJson.addProperty("id", chat.getId());
                            chatJson.addProperty("title", chat.getName());
                            chatsJsonArray.add(chatJson);
                            chatJson = new JsonObject();
                        }
                        userJson.add("channels", chatsJsonArray);
                        chatsJsonArray = new JsonArray();
                        usersJsonArray.add(userJson);
                        userJson = new JsonObject();
                    }

                    adminData.add("allUsers", usersJsonArray);

                    List<Chat> allChats = ChatDAO.findAll();
                    for(Chat chat: allChats){
                        messages = MessageDAO.findMessagesByChatId(chat.getId());
                        for(Message message: messages){
                            messageJson.addProperty("sender", message.getUser().getName());
                            messageJson.addProperty("text", message.getText());
                            messagesJsonArray.add(messageJson);
                            messageJson = new JsonObject();
                        }
                        chatJson.addProperty("id", chat.getId());
                        chatJson.addProperty("title", chat.getName());
                        chatJson.add("messages", messagesJsonArray);
                        chatsJsonArray.add(chatJson);
                        chatJson = new JsonObject();
                        messagesJsonArray = new JsonArray();
                    }
                    adminData.add("allChannels", chatsJsonArray);
                }

                chatsJsonArray = new JsonArray();

                jsonResponse.add("adminData", adminData);

                for(Chat chat: chats){
                    messages = MessageDAO.findMessagesByChatId(chat.getId());
                    for(Message message: messages){
                        messageJson.addProperty("sender", message.getUser().getName());
                        messageJson.addProperty("text", message.getText());
                        messagesJsonArray.add(messageJson);
                        messageJson = new JsonObject();
                    }
                    chatJson.addProperty("id", chat.getId());
                    chatJson.addProperty("title", chat.getName());
                    chatJson.add("messages", messagesJsonArray);
                    chatsJsonArray.add(chatJson);
                    chatJson = new JsonObject();
                    messagesJsonArray = new JsonArray();
                }
                jsonResponse.add("userChannels", chatsJsonArray);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                PrintWriter out = response.getWriter();

                out.print(jsonResponse);
                out.flush();
            } else {
                // Если параметр userId не указан, возвращаем ошибку 400 (Bad Request)
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'userId' is missing or empty");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
