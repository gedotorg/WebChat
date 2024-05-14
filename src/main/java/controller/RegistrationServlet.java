package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.UserDAO;
import exception.DaoException;
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
import java.sql.SQLException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
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

            User user = new User(0, name, password, false);

            try {
                if (UserDAO.findByName(name).isEmpty()) {
                    UserDAO.save(user);

                    JsonObject json = new JsonObject();

                    json.addProperty("userId", user.getId());

                    PrintWriter out = response.getWriter();

                    out.print(json);
                    out.flush();
                } else {
                    JsonObject errorJson = new JsonObject();
                    errorJson.addProperty("error", "Имя уже занято");

                    PrintWriter out = response.getWriter();

                    out.print(errorJson);
                    out.flush();
                }
            } catch (SQLException e) {
                throw new DaoException(e);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
