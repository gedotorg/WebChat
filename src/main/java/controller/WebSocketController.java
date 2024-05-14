package controller;

import handler.MessageHandler;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import utils.WebSocketSessionManager;

import java.io.IOException;
import java.sql.SQLException;


@ServerEndpoint("/chat/{userId}")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WebSocketController{

    private Session session;
    private int userId;
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") int userId) throws IOException {
        // | ,
        this.session = session;
        this.userId = userId;
        WebSocketSessionManager sessionManager = new WebSocketSessionManager();
        sessionManager.addSession(userId, session);
        System.out.println("after open - " + sessionManager.SessionCount());

    }

    @OnMessage
    public void onMessage(Session session, String message) throws SQLException, IOException {
        MessageHandler handler = new MessageHandler();
        handler.handleMessage(session, message);
    }

    @OnClose
    public void onClose(Session session) {

        WebSocketSessionManager sessionManager = new WebSocketSessionManager();
        sessionManager.removeSession(userId);
        System.out.println("after close - " + sessionManager.SessionCount());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }
}
