---
layout: post
title:  "Building a Mini Twitter Clone"
author: <a href="http://eherrera.net" target="_blank">Esteban Herrera</a>
date:   2015-09-22 10:34:52
comments: true
summary: >
  This tutorial will walk you through the steps of creating a mini Twitter clone using Spark, Freemarker, Spring, and HSQLDB to show you how to build a simple but complete web application.
---

<div class="notification"><em>The complete code for this application is available on <a href="https://github.com/eh3rrera/minitwit" target="_blank">GitHub</a>.</em></div>

## What you will learn
In this tutorial, you will learn how to create a web application using <a href="http://sparkjava.com" target="_blank">Spark</a> and <a href="http://freemarker.org/" target="_blank">Freemarker</a> on the view layer, <a href="http://projects.spring.io/spring-framework/" target="_blank">Spring</a> for dependency injection and data access, and <a href="http://hsqldb.org/" target="_blank">HSQLDB</a> as the database.

Particularly, you'll learn:

* How to structure a Web application with Spark
* How to handle post requests with Spark
* How to create a master template/layout template in Freemarker
* How to integrate Spring with Spark
* How to use HSQLBD as an in-memory database

And a lot more. So, let's get started!

## What's in a name?

The application we're going to build for this tutorial is a *mini* clone of <a href="https://twitter.com" target="_blank">Twitter</a>.

Why *mini*?

To keep things simple, we'll only implement the following functionality:

* Log in / Log out
* Registration
* User's timeline / Public timeline
* Follow / Unfollow

There won't be any:

* Favorites
* Retweets
* Direct messages
* Notifications
* Recommendations

And all the other things that are the reason we have come to love Twitter.

All of this work (including the name) is based on the <a href="http://github.com/mitsuhiko/flask/tree/master/examples/minitwit/" target="_blank">MiniTwit</a> example for the <a href="http://flask.pocoo.org/" target="_blank">Flask</a> Python web micro framework written by <a href="http://www.pocoo.org/team/#armin-ronacher" target="_blank">Armin Ronacher</a>.

## MiniTwit

Here are some screenshots of the finished application:

<p>
	<img class="img-bordered" src="/img/posts/minitwit/sign_in.png" alt="Sign in" />
	<div style="text-align:center"><em>Sign in</em></div>
</p><br>

<p>
	<img class="img-bordered" src="/img/posts/minitwit/sign_up.png" alt="Sign up" />
	<div style="text-align:center"><em>Sign up</em></div>
</p><br>


<p>
	<img class="img-bordered" src="/img/posts/minitwit/my_timeline.png" alt="My Timeline" />
	<div style="text-align:center"><em>My Timeline</em></div>
</p><br>

<p>
	<img class="img-bordered" src="/img/posts/minitwit/other_user_timeline.png" alt="Other User's Timeline" />
	<div style="text-align:center"><em>Other User's Timeline</em></div>
</p><br>

<p>
	<img class="img-bordered" src="/img/posts/minitwit/public_timeline.png" alt="Public Timeline" />
	<div style="text-align:center"><em>Public Timeline</em></div>
</p><br>

As you can see, this is a very simple application.

One nice feature is that it's also very easy to run. You only need two things installed:

* Java 8
* Maven 3

Just clone or download the repository on <a href="https://github.com/eh3rrera/minitwit" target="_blank">GitHub</a>, go to the root directory, and execute the following Maven command:

<pre><code class="language-bash">mvn compile exec:java</code></pre>

Then, open your browser at <em>http://localhost:4567</em>.

The application uses an embedded in-memory database, meaning that everything you do is not persisted after the application is terminated. The advantage is that you don't need a database installed to run the application, and in the way the code is structured, it can be changed to use any other relational database easily (more on this later).

The database has some dummy users and messages already inserted. You can use user001/user001 as a user/password combination to log into the application, or user002/user002, or user003/user003 until user010/user010, or sign up yourself. If your e-mail address has an associated Gravatar image, this will be used as your profile image.

You can go and try it now to learn more about how the application works.

Ready? Let's dive into the technical part.


## Code Organization

The code is organized into the following packages:

<p>
	<img class="img-bordered" src="/img/posts/minitwit/package_organization.png" alt="Code Package Organization" />
	<div style="text-align:center"><em>Packages of the project in Eclipse IDE</em></div>
</p>

* **src/main/java**

    - *com.minitwit*  
    Base package, contains the main class of the application.

    - *com.minitwit.config*  
    Contains the classes that set up the database and web routes.

    - *com.minitwit.dao*  
    Contains the interfaces that define the functionality to access the database using the Data Access Object (DAO) pattern.

    - *com.minitwit.dao.impl*  
    Contains the classes that implement the functionality to access the HSQLDB database.

    - *com.minitwit.model*  
    Contains the POJO classes that represent the model of the application.

    - *com.minitwit.service.impl*  
    Contains the class that acts as a facade to access the DAOs from the presentation layer.

    - *com.minitwit.util*  
    Contains utility classes for password hashes and Gravatar images.

* **src/main/resources**

    - *public*  
    Contains the static files used by the web application (currently, only the CSS style sheet).

    - *spark/template/freemarker*  
    Contains the Freemarker templates

    - *sql*  
    Contains the SQL initialization scripts for the database.

Now that we know how the code is structured, let's see how the project is set up with Maven.

## pom.xml

Let's start by listing the project's dependencies (the latest versions at the time of the project creation were used):

* Java 8. The latest version of Spark runs on Java 8.
* Spark 2.3. Web microframework.
* Spark Freemarker 2.3.23. Library that handles the integration between Spark and Freemarker.
* Spring 4.2.0. Used for dependency injection and as a JDBC framework.
* HSQLDB 2.3.3. Used as a database.
* DBCP 2.1.1. Used for the pool connection to the database.
* jBCrypt 0.4. Used to hash the user's password.
* Commons Bean Utils 1.9.2. Used to populate a POJO model from a map of request parameters.

It's a good practice to keep the dependency versions as properties so they can be easily changed or for the case that more than one library of the same project uses the same version (like Spring for example).

So here are the properties:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/minitwit/01.maven_props.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

And here are the dependencies:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/minitwit/02.maven_deps.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

Now let's configure two plugins. 

The first is to ensure that Maven compiles and runs the application using Java 8. This is done with the `maven-compiler-plugin`.

The second plugin will be used to easily run the application from the command line. We can execute any Java class with Maven by using the following command:

<pre><code class="language-bash">mvn exec:java -Dexec.mainClass="com.example.MainClass" -Dexec.args="arg0 arg1 arg2"</code></pre>

As you can see, to use this command, you should know the package and name of the class to execute as well as the parameters it takes. Hopefully, you can declare this information in the pom.xml file with `exec-maven-plugin`, so you can run the program with just:

<pre><code class="language-bash">mvn exec:java</code></pre>

So this is how it looks the plugin section of the pom.xml file:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/minitwit/03.maven_plugins.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

Now let's take a deeper look to the frameworks used starting with Spring.

## Using Spring
Spring is a framework that provides support for dependency injection, transaction management, and data access, among other things, to reduce the "plumbing" required to build Java applications.

Spring manages dependencies through the concept of an *Application Context*, a sort of a central registry used to manage the life cycle of the objects (*beans*, in Spring terminology) of an application.

There are two main ways of configuring beans in Spring:

* XML
* Annotations

This application takes the second approach.

The main class (and starting point) is `com.minitwit.App`:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/04.app_class.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

This class has two important annotations:

* `@Configuration`. This annotation makes the `App` class say to Spring *"Hey, I'm a special class, I hold some configuration information for you"*.
* `ComponentScan({"com.minitwit"})`. This annotation takes as an argument a list of packages where Spring will look for annotated classes to add to its configuration. In this case, there's just one package, `com.minitwit`.

Inside the `com.minitwit` package, we can find the following annotated classes:

* *com.minitwit.config.DatabaseConfig*:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/05.database_config_class.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

In this class we can find a method annotated with `@Bean`. This annotation adds the object returned by the method to Spring's application context, making it available to others beans.

This class configures the datasource for our application. It uses Spring support to use HSQLDB as an embedded database (in-memory database). A good tutorial about Spring embedded databases can be found <a href="http://www.mkyong.com/spring/spring-embedded-database-examples/" target="_blank">here</a>. If we were to use another database, this will be the place to configure the access information (datasource) for that other database.

* *com.minitwit.service.impl.MiniTwitService*:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/06.service_class.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

This extract of the class shows two other important annotations, `@Service` and `@Autowired`.

Just as `@Bean` marks a method so it can be used by Spring, `@Service` marks a class as a "service" to be added to Spring's application context.

On the other hand, `@Autowired` is an annotation that tells Spring where to inject an object for you. So instead of coding something like:

<pre><code class="language-java">
private UserDao userDao = new UserDao();
</code></pre>

Spring instantiates the object (and its dependencies) and set it for you. This is the concept of dependency injection. Of course, the only requirement is that the objects to be injected are managed by Spring.

* *com.minitwit.dao.impl.UserDaoImpl*:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/07.user_dao_class.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>
* *com.minitwit.dao.impl.MessageDaoImpl*:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/08.message_dao_class.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

These classes are based on the DAO pattern to abstract the persistence implementation details in the application. If you want to use another database, or an ORM like <a href="http://hibernate.org/" target="_blank">Hibernate</a>, just create another implementation of the interfaces `com.minitwit.dao.UserDao` and `com.minitwit.dao.MessageDao`.

Also, these classes are annotated with `@Repository` instead of `@Service`. `@Repository` is recommended for any class that fulfills the role of a DAO or a repository. Spring uses `@Component` as a generic stereotype for any managed component. `@Repository` (for persistence layer classes), `@Service` (for services), and `@Controller` (for the presentation layer) are specializations of `@Component`.

<br/>
Going back to `com.minitwit.App`, the `main` method creates a Spring application context of type  `AnnotationConfigApplicationContext`, passing as an argument the `App` class itself so Spring can read the annotations on this class and scan the package `com.minitwit` for other classes with Spring annotations:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/09.app_class.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>


The next line creates an instance of `com.minitwit.config.WebConfig` (which holds the web routes configuration) passing as an argument a bean of type `MiniTwitService`. This bean has dependencies to the DAOs that in turn depend on the datasource to HSQLDB to work. As you can see, with some simple annotations, Spring creates all these dependencies for us, making the code simpler.

At some point, the application context has to be closed. We can't just close it at this point with something like `ctx.close()`, because the access to the in-memory database will be lost (remember it  was created through the Spring embedded database classes).

Luckely, there's a method, `registerShutdownHook()`, that register a shutdown hook with the JVM runtime, closing this context on JVM shutdown (when we close or terminate the program). This is perfect for an application that just waits to listen for connections until is terminated, like this one.

This class is just the starting point of the application. The best part is what happens inside `com.minitwit.config.WebConfig`.

## Spark
`com.minitwit.config.WebConfig` holds the web routes configuration.

Its constructor takes an instance of `com.minitwit.service.impl.MiniTwitService` that is used to execute the business logic of the application:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/10.web_config_constructor.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

`MiniTwitService` is stateless (it doesn't store any client state), thus it can be safely saved as an instance variable so it can be shared across all client requests.

In the next line, a static file location (where the CSS style sheet is located) is specified a finally, a method to set up the web routes is called.

The application responds to the following routes:

* */*  
  For authenticated users, it presents the user's timeline, containing her own messages and the ones from the users she follows. If the user is not authenticated, it's redirected to the public timeline.
* */public*  
  It presents a timeline with messages from all users of the application.
* */login*  
  As a *get* request, it presents the sign in form. If the user is already authenticated, this route redirects to the user's timeline. As a *post* request, it performs the actual sign in.
* */register*  
  As a *get* request, it presents the sign up form. If the user is already authenticated, this route redirects to the user's timeline. As a *post* request, it performs the actual registration.
* */t/:username*  
  It presents the timeline of the given user. If the user doesn't exist, a 404 status with an error message is returned.
* */t/:username/follow*  
  Adds the current (authenticated) user as a follower of the given user. If the user doesn't exist, a 404 status with an error message is returned. If the user is not authenticated, this route redirects to the login page.
* */t/:username/unfollow*  
  Removes the current (authenticated) user as a follower of the given user. If the user doesn't exist, a 404 status with an error message is returned. If the user is not authenticated, this route redirects to the login page.

Take for example, the route that presents the user's timeline:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/11.user_timeline_route.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

First, it gets the authenticated user with a method that just retrieves it from the session:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/12.get_authenticated_user.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

Then, it creates a `Map` that will contain the variables used by the Freemarker template.

Notice how the service is called to get the timeline messages. This service is just a thin facade for the user and message DAOs. For example, here's the content of the `MessageDaoImpl.getUserFullTimelineMessages(User)` method:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/13.getUserFullTimelineMessages_method.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

<em>If you don't want to write embedded SQL Strings, you could try out <a href="http://www.jooq.org/" target="_blank">jOOQ</a>. <br>
Check out <a href="https://github.com/lukaseder/minitwit/blob/master/src/main/java/com/minitwit/dao/MessageDao.java" target="_blank">this example</a> by Lucas Eder, which is a fork of this tutorial.</em>

Using Spring's `NamedParameterJdbcTemplate` class, the query is executed with some parameters passed in a `Map`. A `RowMapper` instance creates `Message` objects from the returned `ResultSet`.

The schema of the database is actually very simple (it's located in *src/main/resource/sql*):

<pre><code class="language-sql">
{% capture code %}{% include codeExamples/minitwit/14.database_schema.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

One table stores user information, another table stores message information (with a reference to the user's table), and a third table stores the following relationship between two users.

The user profile image is handled by Gravatar. The `com.minitwit.util.GravatarUtil` class builds the URL to get the image from the user's e-mail. You can know more about how this URL is constructed in this <a href="https://en.gravatar.com/site/implement/images/" target="_blank">link</a>.

Back to the user's timeline route configuration, in the last step, the map with all the variables is passed to the Freemarker template.

Most routes check if the user is authenticated or if a given user exists, either to redirect to the appropriate page or to send an error. To keep things clean, this is done in a before filter. Here's an example from the */t/:username/follow* route:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/15.before_filter_follow.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

First, the code checks if there's an authenticated user. If there isn't one, the user is redirected to the login page. Then, it checks if the user to be followed exists. If the user doesn't exist, an error is returned. Notice that the use of `halt()` is necessary in both cases to prevent the request to continue to the route handler.

Another example worth mentioning is a POST route. This is the code for the registration handler:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/16.registration_post_route.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

At the time of this writing, the only way to access POST parameters in Spark is with the method `Request.body()`. This method returns a `String` like the following:

<pre><code class="language-java">
username=user050&email=user@mail.com&password=12345
</code></pre>

Fortunately, the jetty server that comes with Spark has a class to parse this type of URLs, `UrlEncoded.decodeTo(String, MultiMap, String, int)`, which takes four parameters:

* The string with the parameters to read
* An instance of `org.eclipse.jetty.util.MultiMap` (that extends from  `HashMap`) where the parameters will be stored
* The encoding of the string (most of the times `UTF-8`)
* The maximum numbers of keys to store in the map (`-1` to read them all)

Once the parameters are stored in the map, the method `BeanUtils.populate(Object, Map)` is used to set the properties of the object from the map. The only restriction is that the keys of the map (the parameters' name) must be the identical to the property names of the object.

If the object is populated successfully, the next step is to validate the information sent by the user. The knowledge of how to validate the information should be in the model object itself. Here's the code of the `validate()` method:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/17.validate_user_method.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

The only validation that can't be done inside this method (because it would need a reference to the service and that's not recommended) is the one about the username already taken:

<pre><code class="language-java">
User existingUser = service.getUserbyUsername(user.getUsername());
if(existingUser == null) {
	service.registerUser(user);
	res.redirect("/login?r=1");
	halt();
} else {
	error = "The username is already taken";
}
</code></pre>

If everything is correct, the user is registered and redirected to the sign in page, with a flag set to show a message about the success of the operation.

If there's an error, a map with the error message and the values entered by the user is sent to the Freemarker template to present this information.

## Freemarker
Freemarker is used as the template mechanism to generate the HTML pages of the application.

Thanks to the <a href="https://github.com/perwendel/spark-template-engines/tree/master/spark-template-freemarker" target="_blank">Spark-Freemarker library</a>, we just have to wrap the template's parameters into a map and return a `ModelAndView` object pointing to the template file. For example, here's the code to present the login page:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/minitwit/18.login_route.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

The application only uses four templates:

* *login.ftl*  
  For the login page
* *register.ftl*  
  For the registration page
* *timeline.ftl*  
  For the public and user's timeline pages
* masterTemplate.ftl  
  Contains the main layout of the application

Most web applications use the same layout (for example, a header, a content area, and a footer) for all their pages. It would be impractical to repeat the code of this layout in all the pages (what if something changes? you'd have to replicate this change in all the pages). Many template libraries include some sort of extending or including mechanism so the layout information is contained in just one file.

In the case of Freemarker, it contains an <a href="http://freemarker.org/docs/ref_directive_include.html" target="_blank">include</a> directive, but it may not be appropiate in all cases. For this kind of job, a combination of the <a href="http://freemarker.org/docs/ref_directive_macro.html" target="_blank">macro</a> and <a href="http://freemarker.org/docs/ref_directive_import.html" target="_blank">import</a> directives is better suited.

Here's the content of `masterTemplate.ftl`:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/minitwit/19.master_template.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

A macro defines a template fragment that allows parameters with default values (like `${title}`) that can be used as a user-defined directive. It can contain a `nested` element that will be executed in the context where the macro is called.

To use this master template, we have to import it with the `import` directive and provide the content that will be replaced by the `nested` element. For example, here's the content of *login.ftl*:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/minitwit/20.login_template.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

See how the master template is imported and referenced with the identifier `layout`, and how the value of the `title` parameter is provided.

The HTML generated by this *user-directive* will be placed where the master template defines the `nested` element:

<pre><code class="language-markup">
...
&lt;/div&gt;
&lt;div class="body"&gt;
    &lt;#nested /&gt;
&lt;/div&gt;
&lt;div class="footer"&gt;
...
</code></pre>


## Conclusion

Now you know how the internals of the application work.

I hope this overview had helped you to understand how to integrate frameworks like Spring and Freemarker with Spark to build a complete web application. I'm sure you can now make modifications (like message pagination or using another database) by yourself without trouble.

I tried to follow good development practices, but this application shows only one approach to develop a web application with Spark. It may not be the simplest one (after all, the original <a href="http://github.com/mitsuhiko/flask/tree/master/examples/minitwit/" target="_blank">minitwit</a> was made in just one file, which is also possible to do with the Spark framework) but it is flexible and can be tested easily.

I hope this tutorial can serve as a starting point for your web development efforts. Have fun!

