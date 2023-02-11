package com.healthpointsfitness.healthpointsfitness.configuration.WebSockets;

import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Service
public class VigoChatHandler extends TextWebSocketHandler {

    //Container to store the active sessions
    private final ConcurrentLinkedQueue<WebSocketSession> chatUserSessions = new ConcurrentLinkedQueue<>();

    //Limit for number of messages in the message queue
    Integer maxMessages = 100;

    //UserDao
    UserRepository userRepository;

    private final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

    private final ConcurrentHashMap<WebSocketSession,User> userSessionMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long,WebSocketSession> userIdToSessionMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long,User> userIdToUserMap = new ConcurrentHashMap<>();

    public VigoChatHandler(UserRepository userRepository){
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
            //Save the user and session in our map
            userSessionMap.put(session,user.get());
            userIdToSessionMap.put(userId,session);
            userIdToUserMap.put(userId,user.get());

            //Grab the users first and last name
            String firstName = user.get().getFirstName();
            String lastName = user.get().getLastName();

            //Create a welcome message
            broadcastMessage("[ADMIN]==>" + firstName + " " + lastName + " has joined the chat. Say hello everybody!!",-1L);
            broadcastMessage("[ADMIN]==> Hey, " + firstName + " " + lastName + " make sure to check out one of our many paths to greatness. Complete the challenges and earn all the badges like a pro!",-1L);

            //If there's less than 100 messages, then send all of them
            if(messages.size() <= maxMessages) {
                //Make a copy of the messages
                List<String> msgsCpy = new ArrayList<String>(messages.stream().toList());

                //Flip the list
                Collections.reverse(msgsCpy);

                //Send them out
                msgsCpy.forEach(message->{
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }else{ //Otherwise only send the max amount of messages

                //Iterate through the messages in reverse order
                for(int i = maxMessages; i >= 0; i--){
                    //Grab the message at the current index
                    var message = (String) messages.stream().toList().get(i);

                    //send it
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    public void broadcastMessage(String msg,Long userId){
        try { //Try to save the message
            if (userId != -1L) { //If it's not an admin message
                //Grab the user for the message
                User sender = userRepository.findUserById(Math.toIntExact(userId));

                //Add the message to the message queue
                messages.add(sender.getFirstName() + " " + sender.getLastName() + " - " + msg);
            } else { //Otherwise mark it as an admin message
                messages.add("[ADMIN]==>" + msg);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        //Start de-queueing messages from the front of the array past the 100th message
        if(messages.size() > maxMessages){
            messages.remove(0);
        }

        //Iterate through all open sessions
        chatUserSessions.forEach(session->{
            try { //Try to broadcast the message to all user
                User usr = userIdToUserMap.get(userId);
                if(session.isOpen()) {
                    if(userId == -1){ //If this is an admin message
                        session.sendMessage(new TextMessage(msg));
                    }else{
                        session.sendMessage(new TextMessage(usr.getFirstName() + " " + usr.getLastName() + " - " + msg));
                    }
                }else{
                    User curUsr = userSessionMap.get(session);
                    String fn = curUsr.getFirstName();
                    String ln = curUsr.getLastName();
                    chatUserSessions.remove(session);
                    //Notify users that user has left the chat
                    chatUserSessions.forEach(s->{
                        if(s.isOpen()){
                            try {
                                s.sendMessage(new TextMessage("[ADMIN]==>" + fn + " " + ln + "has left the chat. Wah Wah Waaah! :(" ));
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

        try {
            //Grab the message payload
            String payload = message.getPayload();

            //Generate a JSON object from the payload
            JSONObject jsonObject = new JSONObject(payload);

            //If the chat user sessions don't contain this incoming session
            if (!chatUserSessions.contains(session)) {
                String userid = String.valueOf(jsonObject.get("userid"));
                if (!userid.isBlank() && !userid.isEmpty()) {
                    Long sessionUserId = Long.valueOf(userid);
                    initialUserSetup(session, sessionUserId);
                }
            } else {
                String msg = (String) jsonObject.get("message");
                Long userId = Long.valueOf((String) jsonObject.get("userid"));
                broadcastMessage(msg, userId);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

