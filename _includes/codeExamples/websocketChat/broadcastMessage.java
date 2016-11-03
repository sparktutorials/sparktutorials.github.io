//Sends a message from one user to all users, along with a list of current usernames
public static void broadcastMessage(String sender, String message) {
    userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                .put("userMessage", createHtmlMessageFromSender(sender, message))
                .put("userlist", userUsernameMap.values())
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}