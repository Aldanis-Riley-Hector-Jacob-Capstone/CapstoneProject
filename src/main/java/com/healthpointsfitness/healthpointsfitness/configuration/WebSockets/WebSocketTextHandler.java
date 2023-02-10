package com.healthpointsfitness.healthpointsfitness.configuration.WebSockets;

import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import jakarta.websocket.Session;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
@Service
public class WebSocketTextHandler extends TextWebSocketHandler {

    //Container to store the active sessions
    List<WebSocketSession> chatUserSessions = new ArrayList<>();

    //Container to store the incoming message queue
    List<String> messageQueue = new ArrayList<>();

    //Limit for number of messages in the message queue
    Integer maxMessages = 100;

    //UserDao
    UserRepository userRepository;

    private List<String> messages = new ArrayList<>();

    Map<WebSocketSession,User> userSessionMap = new HashMap<>();

    Map<Long,WebSocketSession> userIdToSessionMap = new HashMap();

    Map<Long,User> userIdToUserMap = new HashMap();
    public WebSocketTextHandler(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //Setup new user session
    private void initialUserSetup(WebSocketSession session, Long userId) throws IOException {
        //Save the session to the list of available and open sessions
        chatUserSessions.add(session);

        //Get the user by the userId
        Optional<User> user = userRepository.findById(userId);

        //If the user is present
        if(user.isPresent()){
            //Save the user and session in the
            userSessionMap.put(session,user.get());

            userIdToSessionMap.put(userId,session);

            userIdToUserMap.put(userId,user.get());

            String firstName = user.get().getFirstName();
            String lastName = user.get().getLastName();

            //Create a welcome message
            session.sendMessage(new TextMessage(firstName + " " + lastName + " has joined the chat."));

            //Send last 100 messages
            if(messages.size() <= maxMessages) {
                List<String> messagesCpy = messages;
                Collections.reverse(messagesCpy);
                messagesCpy.forEach(message->{
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }else{
                for(int i = maxMessages; i >= 0; i--){
                    session.sendMessage(new TextMessage(messages.get(i)));
                }
            }
        }
    }

    public void broadcastMessage(String msg,Long userId){
        //Add the message to the message queue
        messages.add(msg);

        //Start de-queueing messages from the front of the array past the 100th message
        if(messages.size() > maxMessages){
            messages.remove(0);
        }

        //Iterate through all open sessions
        chatUserSessions.forEach(session->{
            try { //Try to broadcast the message to all user
                User usr = userIdToUserMap.get(userId);
                if(session.isOpen()) {
                    session.sendMessage(new TextMessage(usr.getFirstName() + " " + usr.getLastName() + " - " + msg));
                }else{
                    User curUsr = userSessionMap.get(session);
                    String fn = curUsr.getFirstName();
                    String ln = curUsr.getLastName();
                    chatUserSessions.remove(session);
                    //Notify users that user has left the chat
                    chatUserSessions.forEach(s->{
                        if(s.isOpen()){
                            try {
                                s.sendMessage(new TextMessage("[ADMIN]=>" + fn + " " + ln + "has left the chat." ));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        //Grab the message payload
        String payload = message.getPayload();

        //Generate a JSON object from the payload
        JSONObject jsonObject = new JSONObject(payload);

        //If the chat user sessions don't contain this incoming session
        if(!chatUserSessions.contains(session)){
            String userid = String.valueOf(jsonObject.get("userid"));
            Long sessionUserId = Long.valueOf(userid);
            initialUserSetup(session,sessionUserId);
        }else {
            String msg = (String) jsonObject.get("message");
            Long userId = Long.valueOf((String) jsonObject.get("userid"));
            broadcastMessage(msg,userId);
        }
    }
}