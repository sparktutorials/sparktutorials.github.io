@WebSocket
public class ChatWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session currentUser) throws Exception {
        Main.currentUsers.add(currentUser);
        Main.usernameMap.put(currentUser, "User" + Main.nextUserNumber++);
        Main.broadcastMessageFromUser(currentUser, "I have arrived!");
    }

    @OnWebSocketClose
    public void onClose(Session currentUser, int statusCode, String reason) {
        Main.currentUsers.remove(currentUser);
        Main.broadcastMessageFromUser(currentUser, "I'm outta here!");
    }

    @OnWebSocketMessage
    public void onMessage(Session currentUser, String message) {
        Main.broadcastMessageFromUser(currentUser, message);
    }

}