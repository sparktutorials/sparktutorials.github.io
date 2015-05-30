//Builds a HTML element with a sender-name, a message, and a timestamp,
private static String createHtmlMessageFromSender(String sender, String message) {
    return article().with(
            b(sender + " says:"),
            p(message),
            span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
    ).render();
}