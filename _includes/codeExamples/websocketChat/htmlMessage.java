//Builds a HTML element with username, message and timestamp,
private static String createHtmlMessageFromUser(Session user, String message) {
    return article().with(
            b(usernameMap.get(user) + " says:"),
            p(message),
            span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
    ).render();
}