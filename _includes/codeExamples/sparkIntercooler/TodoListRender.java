private static String renderTodos(Request req) {
    String statusStr = req.queryParams("status");
    Map<String, Object> model = new HashMap<>();
    model.put("todos", TodoDao.ofStatus(statusStr));
    model.put("filter", Optional.ofNullable(statusStr).orElse(""));
    model.put("activeCount", TodoDao.ofStatus(Status.ACTIVE).size());
    model.put("anyCompleteTodos", TodoDao.ofStatus(Status.COMPLETE).size() > 0);
    model.put("allComplete", TodoDao.all().size() == TodoDao.ofStatus(Status.COMPLETE).size());
    model.put("status", Optional.ofNullable(statusStr).orElse(""));
    if ("true".equals(req.queryParams("ic-request"))) {
        return renderTemplate("velocity/todoList.vm", model);
    }
    return renderTemplate("velocity/index.vm", model);
}
