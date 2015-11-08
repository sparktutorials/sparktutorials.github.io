//Sends a message from one user to all users, along with a list of current users
public static void broadcastMessageFromUser(Session user, String message) {
    currentUsers.stream().filter(Session::isOpen).forEach(session -> {
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                .put("userMessage", createHtmlMessageFromUser(user, message))
                .put("userlist", currentUsers.stream().map(usernameMap::get).collect(Collectors.toList()))
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}