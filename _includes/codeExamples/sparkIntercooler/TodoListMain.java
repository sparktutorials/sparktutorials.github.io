public static void main(String[] args) {

    exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
    staticFiles.location("/public");
    port(9999);

    get("/",                        (req, res)      -> renderTodos(req));
    get("/todos/:id/edit",          (req, res)      -> renderEditTodo(req));

    post("/todos",                  (ICRoute) (req) -> TodoDao.add(Todo.create(req.queryParams("todo-title"))));
    delete("/todos/completed",      (ICRoute) (req) -> TodoDao.removeCompleted());
    delete("/todos/:id",            (ICRoute) (req) -> TodoDao.remove(req.params("id")));
    put("/todos/toggle_status",     (ICRoute) (req) -> TodoDao.toggleAll(req.queryParams("toggle-all") != null));
    put("/todos/:id",               (ICRoute) (req) -> TodoDao.update(req.params("id"), req.queryParams("todo-title")));
    put("/todos/:id/toggle_status", (ICRoute) (req) -> TodoDao.toggleStatus(req.params("id")));

    after((req, res) -> {
        if (res.body() == null) { // if the route didn't return anything
            res.body(renderTodos(req));
        }
    });

}
