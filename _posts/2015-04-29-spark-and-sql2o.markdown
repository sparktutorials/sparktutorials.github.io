---
layout: post
title:  "Spark and Databases: Configuring Spark to work with Sql2o in a testable way"
author: <a href="http://tomassetti.me" target="_blank">Federico Tomassetti</a>
date:   2015-04-29 10:34:52
comments: true
summary: This tutorial will teach you how to use a database with your Spark application. We will discuss when it is appropriate to use an ORM and describe how you can organize your code to make it easily testable.
---

## What you will learn in this post
In my [last tutorial]({% post_url 2015-04-03-spark-lombok-jackson-reduce-boilerplate %}) we saw how to use Java 8 with Spark, Lombok and Jackson to create a lightweight REST service. One thing we did not examine was how to persist (store) data. 
In this post I would like to describe a way to organize the integration of the database layer with the rest of your Spark application.

In this post we will see

* how to understand if an <a href="http://en.wikipedia.org/wiki/Object-relational_mapping" target="_blank">ORM</a> is the right choice for you
* how <a href="http://www.sql2o.org/" target="_blank">Sql2o</a> (a framework to interface with a database) works
* how to integrate Sql2o with Spark

What we will not see in this post (but probably in a future one):

* how to test the controllers
* how to test the SQL code

Most of what we will learn could be adapted to other systems like myBatis or also ORMs like Hibernate. In my opinion the important thing is to come up with a design that is easily testable. Ideally you should write both unit tests and functional tests, and create a reliable REST service based on Spark.

## To ORM or not to ORM?
The first question is: "What kind of database library should we use?"
There are different approaches out there, with their pros and cons. We basically can divide them in two families:

* the Object-Relational mappers (ORM) approach (e.g., Hibernate)
* the pure SQL approach (e.g., myBatis, Sql2o)

The basic idea is that ORMs abstract the database and provide classes representing our tables. They tend to be database independent and that means you could use the same code to integrate with a MySQL or a PostgreSQL database. They also generate the SQL code for you. In the pure SQL approach instead basically we write the SQL code and wrap each query in a function.

While the idea of abstracting details is always tempting, the problem is that mapping a relational database into an object oriented schema only works up to a certain point. The abstraction tends to be leaky because you end up needing to do things that makes perfect sense into a relational schema, but do not fit into the object-oriented abstraction. While SQL insert statements map well to object instantiation, a delete maps to an object destruction, and an update maps to invoking some setter on an object, things like a SQL join or more complex queries simply have no reasonable equivalent in the object-oriented world. In addition to that, in the SQL world you have transactions, constraints and other mechanisms which are quite useful to guarantee the consistency of your data, but which can make the ORM code complicate.

The other approach instead consists in writing your own SQL code. The advantages of doing that is that you can easily access the nature of the database and let it do all the work. The disadvantage is that you end up writing a lot of SQL queries.

So which approach you should be using? I think it depends: 

* <b>do you want to support all kinds of databases?</b> An ORM can help you with that. 
* <b>how easy is to get started?</b> I think that an ORM tends to make things simpler at the beginning because you do not need to write much code
* <b>maintainability over time</b> as you find the need to do something particular (like a join) you start having to invest a lot of time to understand the internals of your ORM. Eventually you should factor out the effort needed to learn your ORM properly
* <b>using an ORM could give you performance problems</b> because the ORM tries to be smart about what to cache and when to persist the changes, you could get a few surprises.

In the end if I am sure that supporting one database (let's say <a href="http://www.postgresql.org/" target="_blank">PostgreSQL</a>) is enough my default choice is the lightweight approach. I have used myBatis years ago and I wanted to try Sql2o now.

## Designing the Model
Let's assume you have access to a database. During development I would suggest to use a PostgreSQL server running in a Docker container:

* if you are already using Docker do not miss the [tutorial on running Spark inside a Docker container]({% post_url 2015-04-14-getting-started-with-spark-and-docker %}) 
* if you are new to Docker you could check out a tutorial I wrote about <a href="http://tomassetti.me/getting-started-with-docker-from-a-developer-point-of-view-how-to-build-an-environment-you-can-trust/" target="_blank">getting started with Docker from a developer point of view</a> .

Docker or non Docker, I would assume you can now connect to your database.

Now let's start to setup the dependencies in Maven: In addition to the Spark dependencies, you should add Sql2o and the jdbc extension specific to the database you are going to connect to. In my case I chose PostgreSQL:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sql2o/mavenDep.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

This is the schema we are going to use:

<pre><code class="language-sql">
{% capture code %}{% include codeExamples/sql2o/tablesSetup.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

Our data model is based on our posts. A post is identified by a unique UUID, basically a large number. Each post can be part of many categories (or possibly zero). To each post we can add comments. Comments can be approved or not. Does it all make sense so far?

Now, let's start by defining a Model interface and one interface for each table of the database, with no references to the library we are going to use (Sql2o). The rest of your code will depend on this interface, not on the concrete implementation based on Sql2o that we are going to build. This will permit us to have testable code. For example we could use in our tests a dummy implementation storing data in memory.

<pre><code class="language-java">
{% capture code %}{% include codeExamples/sql2o/javaModel.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

## Implement the Model using Sql2o
Great! It is now time to write some code that could actually integrate with a real database. The code it is pretty straightforward, if you know your SQL:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/sql2o/sql2oModel.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

A few comments:

* when we want to execute several operations atomically (i.e., so if one fails no changes are persisted) we use a transaction, otherwise we just open a connection. We use the try-resource mechanism introduced in Java 8, so whatever happens transactions and connections are closed for us. Laziness wins :)
* our queries a micro-templates where values preceded by a colon are replaced by the values specified using addParameter
* when we do a select we can use executeAndFetch and Sql2o will auto-magically map the result, creating a list of objects of the given type and setting the fields corresponding to the names of the columns. Cool, eh? We can also map to simple types like a Strins when we have just one column
* when we create a post we generate a random UUID. It is very, very unlikely to generate the same UUID twice (<a href="http://en.wikipedia.org/wiki/Universally_unique_identifier" target="_blank">"In other words, only after generating 1 billion UUIDs every second for the next 100 years, the probability of creating just one duplicate would be about 50%"</a>)

This is probably all you need to know about Sql2o to write pretty complex services. Maybe you just need to refresh your SQL (do you remember how to a delete? Joins?)

## Controllers
At this point we just need to use our model. Let's see how to write the main method of our application:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/sql2o/controller.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

A few comments:

* We start by parsing our arguments. The class CommandLineOptions is not shown (but it is present on the <a href="https://github.com/ftomassetti/BlogService_SparkExample/blob/master/src/main/java/me/tomassetti/CommandLineOptions.java" target="_blank">GitHub repository</a>). Basically JCommander takes our command line parameters and map them to field of CommandLineOptions. In this way we can specify the address or the port of the database when launching the application.
* We configure Sql2o and instantiate our Model. Pretty straightforward.
* As we saw in our first tutorial we use Jackson to parse the body of our requests into Java beans. Each of them has a method called isValid. We use it to see if the request is acceptable.
* Once we have an object representing the data associated to the request, we basically pass the content to some call of the model.
* Possibly we return the result of some call to model. We convert the Java beans to JSONstrings using dataToJson (again, look it up on GitHub for the code).

## A complete example

We start the application by running:


<pre><code class="language-bash">
mvn exec:java -Dexec.args="--db-host myDBServer --db-port 5432"
</code></pre>

You will need to adapt the parameters to point to your database server. If everything works fine you should be able to invoke your service. To try your service I suggest to use the Chrome plugin <a href="https://chrome.google.com/webstore/detail/postman-rest-client/fdmmgilgnpjigdojojpjoooidkmcomcm?hl=en" target="_blank">Postman</a>.

A first get to <em>localhost:4567/posts</em> should return an empty list of posts

<img class="img-bordered" src="/img/posts/sparkSql2o/postman_2_1.png" alt="Testing with Postman">

Now we insert a post with a post to localhost:4567/posts specifying the values of the post in the body of the request. We should get back the UUID of the post created.

<img class="img-bordered" src="/img/posts/sparkSql2o/postman_2_2.png" alt="Testing with Postman">

Now asking again for the list of posts we should be able to get a list of one element: the post we just inserted

<img class="img-bordered" src="/img/posts/sparkSql2o/postman_2_3.png" alt="Testing with Postman">

Let's add a comment by sending a post to <em>localhost:4567/posts/b3f202dd-1d44-4612-81da-4ea65b49952f/comments</em>. You should change to the UUID of the post that you want to comment. Note that if you use an UUID of a not existing post you should get a 404 code back (meaning "not found").

In this case we get back the UUID of the comment created.

<img class="img-bordered" src="/img/posts/sparkSql2o/postman_2_4.png" alt="Testing with Postman">

Finally we should be able to get the list of comments for our post by using a get to <em>localhost:4567/posts//b3f202dd-1d44-4612-81da-4ea65b49952f/comments</em>

<img class="img-bordered" src="/img/posts/sparkSql2o/postman_2_5.png" alt="Testing with Postman">

## Conclusion
Our service is not complete: for example, we need endpoints to approve the comments (I am sure you can think of many other improvements). However, even if very limited, we have seen how it is possible to build a RESTful service, with persistent data in a few clear lines of Java. No long configuration files, no _auto-magic_ components that break all of a sudden (typically on a Friday evening). 

<b>Just a few simple, reliable, understandable lines of code.</b>

There are things we are missing: We do not properly catch exceptions which are thrown when the requests are invalid. We should definitely do that. You are a smart developer, you know what I mean and you will fix these things before deploying.

At this point we just need proper tests: We should have unit tests to verify that the controllers behave properly. To write them we could mock our Model. 
Testing the SQL code is more complex: I normally use functional tests. Cucumber is a nice tool and we could launch a docker container from inside our functional tests. I think that testing is the next big thing we need to focus on and probably you could soon read a specific tutorial on this website.

{% include authorTomassetti.html %}