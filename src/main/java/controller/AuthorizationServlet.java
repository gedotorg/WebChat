package controller;

import com.google.gson.JsonObject;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/authorization")
public class AuthorizationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String requestBody = stringBuilder.toString();

        try {
            JSONObject jsonObject = new JSONObject(requestBody);
            String name = jsonObject.getString("userName");
            String password = jsonObject.getString("password");

            JsonObject json = new JsonObject();

            if (!UserDAO.findByName(name).isEmpty()) {
                Optional<User> userOptional = UserDAO.findByName(name);
                User user = userOptional.get();
                if(user.getPassword().equals(password)) {

                    json.addProperty("userId", user.getId());

                } else{

                    json.addProperty("error", "неверный пароль");

                }
            } else {

                json.addProperty("error", "пользователь не найден");

            }

            PrintWriter out = response.getWriter();

            out.print(json);
            out.flush();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
