---
layout: post
title: "Creating a library website with login and multiple languages"
author: <a href="https://www.linkedin.com/in/davidaase" target="_blank">David Åse</a>
date: 2016-06-10 11:11:11
comments: true
summary: >
 In this tutorial you will learn how to create a basic Spark application with filters, controllers, views, authentication, localization, error handling, and more.
---

<div class="notification"><em>The source code for this tutorial can be found on <a href="https://github.com/tipsy/spark-basic-structure" target="_blank">GitHub</a>.</em></div>

## What You Will Learn

You will learn how to create a basic Spark application with filters, controllers, views, authentication, localization, error handling, and more, but this is not really a full blown tutorial, it's more a description of a basic structure, with certain points of the code highlighted. To get the full benefit of this tutorial, please clone the example on <a href="https://github.com/tipsy/spark-basic-structure" target="_blank">GitHub</a>, run it, and play around.

### Screenshot
<img src="/img/posts/sparkBasicStructure/screenshot.png" alt="Application Screenshot">

The application is not very fancy, but offers an entity-overview, a locale-switcher, and login/logout functionality, which are all pretty essential pieces of any larger webapp.

## Package structure
<img src="/img/posts/sparkBasicStructure/packageOverview.png" alt="Package Structure">

As you can see, the app is packaged by feature and not by layer. If you need to be convinced that this is a good approach, please have a look at <a href="https://www.youtube.com/watch?v=Nsjsiz2A9mg&feature=youtu.be&t=416" target="_blank">this talk</a> by Robert C. Martin.

## Application.java

This is the class that ties your app together. When you open this class, you should get an immediate understanding of how everything works:
<pre><code class="language-java">
public class Application {

    // Declare dependencies
    public static BookDao bookDao;
    public static UserDao userDao;

    public static void main(String[] args) {

        // Instantiate your dependencies
        bookDao = new BookDao();
        userDao = new UserDao();

        // Configure Spark
        port(4567);
        staticFiles.location("/public");
        staticFiles.expireTime(600L);
        enableDebugScreen();

        // Set up before-filters (called before each get/post)
        before("*",                  Filters.addTrailingSlashes);
        before("*",                  Filters.handleLocaleChange);

        // Set up routes
        get(Path.Web.INDEX,          IndexController.serveIndexPage);
        get(Path.Web.BOOKS,          BookController.fetchAllBooks);
        get(Path.Web.ONE_BOOK,       BookController.fetchOneBook);
        get(Path.Web.LOGIN,          LoginController.serveLoginPage);
        post(Path.Web.LOGIN,         LoginController.handleLoginPost);
        post(Path.Web.LOGOUT,        LoginController.handleLogoutPost);
        get("*",                     ViewUtil.notFound);

        //Set up after-filters (called after each get/post)
        after("*",                   Filters.addGzipHeader);

    }

}
</code></pre>

### Static dependencies?
This is probably not what you learned in Java class, but I believe having static objects is superior to dependency injection when dealing with web applications. Injecting dependencies hides their initialization from controllers, and you have to trace the injected class back to the injection point to realize what's going on. It also makes getting stuff done a lot more ceremoniousm (it complicates things greatly for no real benefit). As can be seen in <a href="https://glot.io/snippets/efivlwbva5" target="_blank"
>this example</a>, you need about twice the amount of code for the same functionality.
You're not launching this thing into space, so you don't need to test everything. If you do decide to test your controllers, then <a href="https://github.com/FluentLenium/FluentLenium" target="_blank">scenario-tests</a> are superior to mocking and unit-tests.

### Before, routes, after
If your application is small, delcaring before-filters, routes, and after-filters all in the same location greatly improves the readability of your code. Just by looking at the class above you can tell that there's a filter that adds trailing slashes to all endpoints (ex: /books -> /books/) and that any page can handle a locale change. You also get an overview of all the endpoints, and see that all routes are GZIPed after everything else.

### Path.Web and Controller.field
It's usually a good idea to keep your paths in some sort of constant. In the above class I have a **Path** class with a subclass **Web** (it also has a subclass **Template**), which holds public final static Strings. That's just my preference, it's up to you how you want to do this. All my handlers are declared as static Route fields, grouping together functionality in the same classes (based on feature). Let's have a look at the LoginController:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/sparkBasicStructure/LoginController.java %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

It has four Routes/methods, *serveLoginPage*, *handleLoginPost*, *handleLogoutPost*, and *ensureUserIsLoggedIn*. This is all the functionality that is related to login/logout.
The serveLoginPage Route inspects the request session and puts necessary variables in the Velocity model (did the user just log out? is there a uri to redirect the user to after login?), then renders the page. The *ensureUserIsLoggedIn* Route is used in other controllers, for example in BookController:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/sparkBasicStructure/BookController.java %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

It intercepts the current Route *fetchOneBook* and redirects the user to the login page (if the user is not logged in). The origin path is stored in *ensureUserIsLoggedIn* so the user is redirected back to the correct place after login. 

## Response types

The *fetchOneBook* above controller gives three different answers based on the HTTP accepts header: Try first to return HTML, then try to return JSON, finally return not-acceptable (this Route only produces HTML and JSON).

## Localization
Localization in Java is pretty straightforward. You create two properties files with different suffixes, for example *messages_en.properties* (english) and *messages_de.properties* (german), then you create a ResourceBundle:
<pre><code class="language-java">
    ResourceBundle.getBundle("localization/messages", new Locale("en"));
</code></pre>

The setup is a bit more elborate if you clone the application (I created a small wrapper object with two methods), but the basics are extremely simple, and only uses native Java.

## Rendering views
Rendering views is taken care of by another static helper, the **ViewUtil**:

<pre><code class="language-java">
public class ViewUtil {
   
    public static String render(Request request, Map model, String templatePath) {
        model.put("msg", new MessageBundle(getSessionLocale(request)));
        model.put("currentUser", getSessionCurrentUser(request));
        model.put("WebPath", Path.Web.class); // Access application URLs from templates
        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }
    
}
</code></pre>
The render method needs acceess to the request to check the locale and the current users. It puts this information in the template-model to ensure that the views are rendered correctly.

The **ViewUtil** also has some Route fields, such as *notFound* and *notAcceptable*. It's a good place to put non-controller-specific error handling.

## Conlusion
Hopefully you've learned a bit about Spark, but also Java and webapps in general. If you disagree with any choices made in the example-app, please create an issue on <a href="https://github.com/tipsy/spark-basic-structure" target="_blank">GitHub</a>. This example will hopefully continue to evolve based on feedback and new Spark features.
