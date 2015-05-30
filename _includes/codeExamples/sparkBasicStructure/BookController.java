public static Route fetchOneBook = (Request request, Response response) -> {
    LoginController.ensureUserIsLoggedIn(request, response);
    if (clientAcceptsHtml(request)) {
        HashMap<String, Object> model = new HashMap<>();
        Book book = bookDao.getBookByIsbn(getParamIsbn(request));
        model.put("book", book);
        return ViewUtil.render(request, model, Path.Template.BOOKS_ONE);
    }
    if (clientAcceptsJson(request)) {
        return dataToJson(bookDao.getBookByIsbn(getParamIsbn(request)));
    }
    return ViewUtil.notAcceptable.handle(request, response);
};