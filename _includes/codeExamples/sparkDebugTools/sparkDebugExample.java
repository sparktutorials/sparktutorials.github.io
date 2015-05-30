public class DebugScreenExample {
    public static void main(String[] args) {
        port(4567);
        get("*", (req, res) -> {
            throw new Exception("Exceptions everywhere!");
        });
        // Add this line to your project to enable the debug screen
        enableDebugScreen(); 
    }
}