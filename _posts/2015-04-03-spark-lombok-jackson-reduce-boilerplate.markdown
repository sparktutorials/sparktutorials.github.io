---
layout: post
title:  "Spark for REST APIs: Using Spark, Lombok and Jackson to reduce Java boilerplate"
author: <a href="http://tomassetti.me" target="_blank">Federico Tomassetti</a>
date:   2015-04-03 10:34:52
comments: true
summary: This tutorial will teach you how to create a minimal RESTful application for a blog, using JSON to transfer data. You will learn about setting up a complete Spark project, using Jackson and Lombok for awesome descriptive exchange objects. The tutorial is written by Federico Tomassetti and originally appear on his <a href="http://tomassetti.me/">blog</a>.
---

##Getting started with Spark:  Create a lightweight RESTful application in Java
Recently I have been writing a RESTful service using <a href="http://sparkjava.com" target="_blank">Spark</a>, a web framework for Java (which is not related to Apache Spark). When we planned to write this I was ready to the unavoidable Javaesque avalanche of interfaces, boilerplate code and deep hierarchies. I was very surprised to find out that an alternative world exists also for the developers confined to Java.

In this post we are going to see how to build a RESTful application for a blog, using JSON to transfer data. We will see:

* how to create a simple Hello world in Spark
* how to specify the layout of the JSON object expected in the request
* how to send a post request to create a new post
* how to send a get request to retrieve the list of posts

We are not going to see how to insert this data in a DB. We will just keep the list in memory (in my real service I have been using sql2o).

##A few dependencies
First of all, we need to set up a few dependencies. Java:
<pre><code class="language-java">
{% capture code %}{% include codeExamples/sparkLombok/javaDeps.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

We will be using Maven so I will start by creating a new pom.xml throwing in a few things. Basically:

* Spark
* Jackson
* Lombok
* Guava
* Easymock (used only in tests, not presented in this post)
* Gson

Let's take a lot at the POM:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkLombok/mavenDep.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

##Spark "Hello world!"

Now we can run it like this:

<pre><code class="language-bash">
mvn compile && mvn exec:java
</code></pre>

Let's open a browser and visit <em>http://localhost:4567/posts.</em> Here we want to do a simple get. For performing posts you could want to use the Postman plugin for your browser or just run <a href="http://en.wikipedia.org/wiki/CURL" target="_blank">curl</a>. Whatever works for you.

##Using Jackson and Lombok for awesome descriptive exchange objects
In a typical RESTful application we expect to receive POST requests with json objects as part of the payload. Our job will be to check the code is well-formed JSON, that it corresponds to the expected structure, that the values are in the valid ranges, etc. Kind of boring and repetitive. We could do that in different ways. The most basic one is to use <a href="https://code.google.com/p/google-gson/" target="_blank">gson</a>:

<pre><code class="language-java">
JsonParser parser = new JsonParser();
JsonElement responseData = parser.parse(response);
if (!responseData.isJsonObject()){
  // send an error like: "Hey, you did not pass an Object!
}
JsonObject obj = responseData.getAsJsonObject();
if (!obj.hasField("title")){
  // send an error like: "Hey, we were expecting a field name title!
}
JsonElement titleAsElem = obj.get("title");
if (!titleAsElem.isString()){
  // send an error like: "Hey, title is not an string!
}
// etc, etc, etc
</code></pre>

We probably do not want to do that. <br>
A more declarative way to specify what structure we expect is creating a specific class:

<pre><code class="language-java">
class NewPostPayload {
   private String title;
   private List categories;
   private String content;
   
   public String getTitle() { ... }
   public void setTitle(String title) { ... }
   public List getCategories() { ... }
   public void setCategories(List categories){ ... }
   public String getContent() { ... }
   public void setContent(String content) { ... }
}
</code></pre>

And then we could use Jackson:

<pre><code class="language-java">
try {
   ObjectMapper mapper = new ObjectMapper();
   NewPostPayload newPost = mapper.readValue(request.body(), NewPostPayload.class);
} catch (JsonParseException e){
   // Hey, you did not send a valid request!
}
</code></pre>

In this way Jackson check automatically for us if the payload has the expected structure. We could want to verify if additional constraints are respected. For example we could want to check if the title is not empty and at least one category is specified. We could create an interface just for validation:

<pre><code class="language-java">
interface Validable {
   boolean isValid();
}
 
class NewPostPayload implements Validable {
   private String title;
   private List categories;
   private String content;
 
   public String getTitle() { ... }
   public void setTitle(String title) { ... }
   public List getCategories() { ... }
   public void setCategories(List categories){ ... }
   public String getContent() { ... }
   public void setContent(String content) { ... }
 
   public boolean isValid() {
      return title != null && !title.isEmpty() && !categories.isEmpty();
   }
}
</code></pre>

Still we have a bunch of boring getters and setters. They are not very informative and just pollute the code. We can get rid of them using <a href="http://projectlombok.org/" target="_blank">Lombok</a>. Lombok is an annotation processor that add repetitive methods for you (getters, setters, equals, hashCode, etc.). You can think of it as a plugin for your compiler that looks for annotations (like <em>@Data</em>) and generates methods based on them. If you add it to your dependencies maven will be fine but your IDE could not give you auto-completion for the methods that Lombok adds. You may want to install a plugin. For Intellij Idea I am using Lombok Plugin version 0.9.1 and it works great.

Now you can revise the class NewPostPayload as:

<pre><code class="language-java">
@Data
class NewPostPayload {
   private String title;
   private List categories;
   private String content;
   
   public boolean isValid() {
       return title != null && !title.isEmpty() && !categories.isEmpty();
   }
}
</code></pre>

Much nicer, eh?

##A complete example

We need to do two things:

* insert a new post
* retrieve the whole list of posts

The first operation should be implemented as a POST (it has side effects), while the second one as a GET. Both of them are operation on the posts collection so we will use the endpoint /posts .

Let's start by inserting  post. First of all we will parse:

<pre><code class="language-java">
// insert a post (using HTTP post method)
post("/posts", (request, response) -> {
    try {
        ObjectMapper mapper = new ObjectMapper();
        NewPostPayload creation = mapper.readValue(request.body(), NewPostPayload.class);
        if (!creation.isValid()) {
            response.status(HTTP_BAD_REQUEST);
            return "";
        }
        int id = model.createPost(creation.getTitle(), creation.getContent(), creation.getCategories());
        response.status(200);
        response.type("application/json");
        return id;
    } catch (JsonParseException jpe) {
        response.status(HTTP_BAD_REQUEST);
        return "";
    }
});
</code></pre>

And then see how to retrieve all the posts:

<pre><code class="language-java">
// get all post (using HTTP get method)
get("/posts", (request, response) -> {
    response.status(200);
    response.type("application/json");
    return dataToJson(model.getAllPosts());
});
</code></pre>

And the final code is:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/sparkLombok/finalCode.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

##Using PostMan to test the application

You may want to use curl instead, if you prefer the command line. I like not having to escape my JSON and having a basic editor so I use PostMan (a Chrome plugin).

Let's insert a post. We specify all the fields as part of a Json object inserted in the body of the request. We get back the ID of the post created.

<img class="img-bordered" src="/img/posts/postman1.png" alt="Testing with Postman">

Then we can get the list of the posts. In this case we use a GET (no body in the request) and we get the data of all the posts (just the one we inserted above).

<img class="img-bordered" src="/img/posts/postman2.png" alt="Testing with Postman">

##Conclusion
I have to say that I was positively surprised by this project. I was ready for the worse: this is the kind of application that requires a basic logic and a lot of plumbing. I found out that Python, Clojure and Ruby do all a great jobs for this kinds of problems, while the times I wrote simple web applications in Java the logic was drown in boilerplate code. Well, things can be different. The combination of Spark, Lombok, Jackson and Java 8 is really tempting. I am very grateful to the authors of these pieces of software, they are really improving the life of Java developers. I consider it also a lesson: great frameworks can frequently improves things much more than we think.

{% include authorTomassetti.html %}