public class Main {

    static List<Session> currentUsers = new ArrayList<>();
    static Map<Session, String> usernameMap = new HashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user

    public static void main(String[] args) {
        staticFileLocation("public"); //index.html will be served at localhost:4567 (default port)
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }
    
}