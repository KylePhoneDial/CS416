package longbottom.chat;

// copied from here: https://github.com/tipsy/spark-websocket/blob/master/src/main/java/ChatWebSocketHandler.java

import longbottom.accounts.User;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import longbottom.DAO.*;


@WebSocket (maxIdleTime = 999999999)
public class ChatWebSocketHandler {

    private String sender;
    private String message;

    // Integer represents the project ID of the project

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception{

        //if there are no more users in the chat room delete the chat room
        int projectId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("projectId").get(0));

        int userId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("username").get(0));
        User u = DAO.getUser(userId);
        //String username = DAO.getEmailByUserId(userId);

        //chat room already exists
        if (Chat.chatMap.containsKey(projectId)){
            Chat.chatMap.get(projectId).put(user, DAO.getUser(userId).getFullName());
        }
        else{
            Chat.chatMap.put(projectId, new HashMap(){{put(user, (DAO.getUser(userId).getFullName()));}});
        }

        Chat.broadcastMessage(sender = "Server", message = (u.getFullName() + " has joined the chat."), projectId);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason){
        int projectId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("projectId").get(0));
        int userId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("username").get(0));
        //String username = DAO.getEmailByUserId(userId);
        User u = DAO.getUser(userId);

        if(Chat.chatMap.containsKey(projectId)){
            Chat.chatMap.get(projectId).remove(user);
            //if the chat room is empty delete the chat room
            if (Chat.chatMap.get(projectId).isEmpty()){
                Chat.chatMap.remove(projectId);
            }
            else{
                Chat.broadcastMessage("Server", message = (u.getFullName() + " has left the chat."), projectId);
                //Chat.broadcastMessage(sender = "Server", message = (username + " has left the chat."), projectId);
            }
        }
    }


    @OnWebSocketMessage
    public void onMessage(Session user, String message){
        int projectId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("projectId").get(0));
        int userId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("username").get(0));
        User u = DAO.getUser(userId);

        //String username = DAO.getEmailByUserId(userId);
        //add chat message to the database
        DAO.createChatMessage(projectId, userId, message);

        Chat.broadcastMessage(u.getFullName(), message, projectId);
    }
}
