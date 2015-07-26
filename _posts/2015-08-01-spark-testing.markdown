---
layout: post
title:  "Testing spark application: how to design testable web applications applications"
author: <a href="http://tomassetti.me" target="_blank">Federico Tomassetti</a>
date:   2015-08-01 10:34:52
summary: >
  Writing tests should simplify the development of web applications and give you the confidence to perform refactoring. However often it becomes a chore. Writing tests is not easy, as it is not easy to understand what to test and how to test it. In this tutorial we describe one possible approach to use a mix of unit and functional tests to keep development simple and agile, while having our back covered by solid tests.
---

##The plan

There are many different forms of tests which can be used to assure different properties of your applications are maintained over time. In this tutorial we focus exclusively on the functional aspects (i.e., we verify that the application does what is supposed to do), while we do not consider the non-functional aspects (i.e., how the application does it, so performance, load handling, etc.).

Our plan is to write two kinds of tests:

* unit tests, to verify that single classes or methods implement a piece of logic correctly
* functional tests, to ensure that the whole application implements correctly features

We are going to use two different approaches for implementig these tests:

* unit tests will be written in Java using JUnit. We will describe a pattern to make the logic more testable
* functional tests are going to be written using Cucumber. You will have to write some Ruby for that

We will start by understanding when to use one each testing approach and then we will see how to write unit tests. Functional tests instead will be discussed in a future post.

As for previous posts examples are presented based on our [Blog service application](https://github.com/sparktutorials/BlogService_SparkExample), with all the code available on GitHub.

##Logic and plumbing code

In my opinion code can be roughly divided in two parts: the **logic** and the **plumbing**. The **logic** is normally something specific to your application and dealing with the domain. For example calculating since how many days a post was published or if a user has the permission to publish a post. The **plumbing** has instead to deal with the technological aspects: verify that a certain header has a valid value, deal with an IO exception and so on. 

The logic is what you want absolutely to test and it is typically fairly easy to write unit tests for that part. The plumbing instead tend to be hard to test because it is strongly connected with low level libraries and it could require some complex state to be re-created in your test. In addition to the difficulty the benefits are often very low: you are basically testing the library you are using (for example an HTTP library) instead of testing your logic. 

Because of this my test strategy is based on two elements:

* separate logic and plumbing code
* test logic through unit tests while testing plumbing code through functional tests

We first start with the unit tests, which are probably familiar to more user. Then we will take courage and jump in the land on Cucumber & Ruby to write our functional tests. Don't be alarmed: functional tests are not going to be treated in this post, so you will have some time to learn Ruby first.

##The RequestHandler interface

As we have seen before a key objective to simplify testing is to separate logic and plumbing code. To do so I want to insulate the logic from the _spark specific_ bits. The logic should be as insulated as possible, so that we could one day replace Spark with something else and leave the logic untouched (of course no one of sound mind would do something as reckless as stop using Spark, it was just an hyphotetical example).

To do this instead of just implementing *Routes* in my code I create an interface which is project specific. I start by looking at what information I need to use to serve the different requests. Considering the application we have built in the previous tutorials you will see that for each request we:

* could read JSON code from the body of the request
* consider parameters encoded in the URL (e.g., the ID of the post)
* consider the header _Accept_ to establish if we need to return HTML or JSON objects

So for this project I could write this common interface for all my requests handlers and named it [RequestHandler](https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/RequestHandler.java):

<pre><code class="language-java">public interface RequestHandler&lt;V extends Validable&gt; {

    Answer process(V value, Map&lt;String, String&gt; urlParams, boolean shouldReturnHtml);

}</code></pre>

As you can see we expect the body of the request to be parsed and returned as a value of the generic type _V_. The type of the value object can be different depending on the requests. For example, when we receive the request to create a new post we will expect a [NewPostPayload](https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/handlers/NewPostPayload.java) object to be serialized in the body of the request. We will use a special value named [EmptyPayload](https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/handlers/EmptyPayload.java) for the cases in which we do not need to parse the body of the request.

Finally our handler will simply return an instance of [Answer](https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/Answer.java) which is a class with two simple fields:

* the HTTP code to return (200 = success, 404 = not found, etc., see the list [here](http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html))
* the body of the response: typically JSON or HTML code

Note that nothing present in the interface is Spark-specific or related to any particular frameworks. It will help us making all our handlers easily testable.

Now we need just to bridge our request handlers to Spark's routes. We could do that in a few different ways. I will go for creating a base class from which our request handlers should inherit. This class is [AbstractRequestHandler](https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/AbstractRequestHandler.java). 

<pre><code class="language-java">public abstract class AbstractRequestHandler<V extends Validable&gt; implements RequestHandler<V&gt;, Route {

    private Class<V&gt; valueClass;
    protected Model model;

    private static final int HTTP_BAD_REQUEST = 400;

    public AbstractRequestHandler(Class<V&gt; valueClass, Model model){
        this.valueClass = valueClass;
        this.model = model;
    }

    private static boolean shouldReturnHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }

    public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, data);
            return sw.toString();
        } catch (IOException e){
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }

    public final Answer process(V value, Map<String, String&gt; queryParams, boolean shouldReturnHtml) {
        if (!value.isValid()) {
            return new Answer(HTTP_BAD_REQUEST);
        } else {
            return processImpl(value, queryParams, shouldReturnHtml);
        }
    }

    protected abstract Answer processImpl(V value, Map<String, String&gt; queryParams, boolean shouldReturnHtml);


    @Override
    public Object handle(Request request, Response response) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        V value = objectMapper.readValue(request.body(), valueClass);
        Map<String, String&gt; queryParams = new HashMap<&gt;();
        Answer answer = process(value, queryParams, shouldReturnHtml(request));
        response.status(answer.getCode());
        if (shouldReturnHtml(request)) {
            response.type("text/html");
        } else {
            response.type("application/json");
        }
        response.body(answer.getBody());
        return answer.getBody();
    }

}</code></pre>

##How to write and use RequestHandlers

Now let's see how to use this class. Before we had our logic defined in anonymous classes implementing the _Route_ interface. For example:

<pre><code class="language-java">
        // insert a post (using HTTP post method)
        post("/posts", (request, response) -&gt; {
            ObjectMapper mapper = new ObjectMapper();
            NewPostPayload creation = mapper.readValue(request.body(), NewPostPayload.class);
            if (!creation.isValid()) {
                response.status(HTTP_BAD_REQUEST);
                return "";
            }
            UUID id = model.createPost(creation.getTitle(), creation.getContent(), creation.getCategories());
            response.status(200);
            response.type("application/json");
            return id;
        });
</code></pre>

After the change we will move the logic to a separate class, like [PostsCreateHandler](https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/handlers/PostsCreateHandler.java):

<pre><code class="language-java">public class PostsCreateHandler extends AbstractRequestHandler&lt;NewPostPayload&gt; {

    private Model model;

    public PostsCreateHandler(Model model) {
        super(NewPostPayload.class, model);
        this.model = model;
    }

    @Override
    protected Answer processImpl(NewPostPayload value, Map<String, String> urlParams, boolean shouldReturnHtml) {
        UUID id = model.createPost(value.getTitle(), value.getContent(), value.getCategories());
        return new Answer(200, id.toString());
    }
}</code></pre>


And we will use this class as our _Route_ (remember that _AbstractRequestHandler, which is extended by _PostsCreateHandler_, implements _Route_).

<pre><code class="language-java">
post("/posts", new PostsCreateHandler(model));
</code></pre>

##Unit tests

##Conclusions

{% include authorTomassetti.html %}