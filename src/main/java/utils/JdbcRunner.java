package utils;

import dao.ChatDAO;
import dao.MessageDAO;
import dao.UserChatDAO;
import dao.UserDAO;

import java.sql.SQLException;

public class JdbcRunner {
    public static void main(String args[]) throws SQLException{
        var messageDao = MessageDAO.getInstance();
        var chatDao = ChatDAO.getINSTANCE();
        var userChatDao = UserChatDAO.getINSTANCE();
        var userDao = UserDAO.getINSTANCE();
        System.out.println(userDao.findByName("papa"));
        //var filter = new MessageFilter();
        //System.out.println(MessageDAO.findMessagesByChatId(2));
        //System.out.println(userChatDao.findUsersByChatId(2));
    }
}
