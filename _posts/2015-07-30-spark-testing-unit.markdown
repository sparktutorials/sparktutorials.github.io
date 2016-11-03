---
layout: post
title:  "Spark and Testing - Part 1: Background and Unit Tests"
author: <a href="http://tomassetti.me" target="_blank">Federico Tomassetti</a>
date:   2015-07-30 10:34:52
comments: true
summary: >
  In this tutorial series you will learn an approach for writing testable Spark applications from the ground up. In part one we will discuss what and when to test, and write some unit tests. In part two we will cover functional tests.
---

<div class="notification"><em>This is part one of a two-part tutorial series on testing in which we will outline how to write a testable Spark application from the ground up. If you already have a finished application that you want to start testing using the approach described here, some refactoring will be required. </em></div>

## The plan
There are many different forms of tests that can be used to assure that the different properties of your applications are maintained over time. In these tutorials we will focus exclusively on application logic (we verify that the application does what is supposed to do). We will not consider non-functional aspects (like response time, load handling, etc.).

In the first tutorial we are going to focus on *unit tests*, while in the second tutorial we are going to focus on *functional tests*:

* *unit tests*, to verify that classes or methods are logically correct
* *functional tests*, to ensure that the whole application correctly implements our features

We are going to use two different approaches for implementing these tests:

* *unit tests* will be written in *Java using JUnit*. We will describe a pattern to make logic easy to test
* *functional tests* are going to be written using *Cucumber and Ruby*

We will start by examining when to use each testing approach, then we will see how to write unit tests.

The examples are based on our Blog service application, and all the code is available on <a href="https://github.com/sparktutorials/BlogService_SparkExample" target="_blank">GitHub.</a>

## Logic and plumbing code

In my opinion, code can be divided roughly into two parts: the **logic** and the **plumbing**: 

The **logic** is normally something specific to your application and domain. For example calculating how many days ago a post was published, or, if a user has the permission to publish a post.

The **plumbing** is more about the technological aspects. For example verifying that a certain header has a valid value, dealing with an IO exception, etc.

The logic is what you absolutely need to test, and it's usually fairly easy to write unit tests for it. Testing the plumbing tends to be harder. It's often strongly connected with low level libraries and usually requires complex states to be re-created in your test. In addition to the difficulty, the benefits are few: you are basically testing the library you are using (for example an HTTP library) instead of testing your own code and logic. 

Because of this, my test strategy can be divided in two steps:

1. separate logic and plumbing code
2. test logic through unit tests while testing plumbing code through functional tests

It's time to get started! As mentioned before, we will begin with unit tests. Functional tests (using Cucumber and Ruby) will have to wait for Part 2 of this tutorial series.

## The RequestHandler interface

To separate logic and plumbing code we want to insulate the logic from the *Spark specific* bits in our application. The logic should be as insulated as possible, so that we could one day replace Spark with something else and leave the logic untouched (of course, no one in their right mind would stop using Spark, it was a very hypothetical example!).

Instead of just implementing *Routes*, we will create an interface which is project specific. We start by looking at what information we need to use to serve the different requests. If you look at the application we built in the previous tutorials, you will see that for each request we:

* could read JSON code from the body of the request
* consider parameters encoded in the URL (e.g., the ID of the post)
* consider the header _Accept_ to establish if we need to return HTML or JSON objects

So for this project we could write a common interface for all our requests handlers and call it [RequestHandler](https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/RequestHandler.java):

<pre><code class="language-java">
public interface RequestHandler&lt;V extends Validable&gt; {

    Answer process(V value, Map&lt;String, String&gt; urlParams, boolean shouldReturnHtml);

}
</code></pre>

As you can see we expect the body of the request to be parsed and returned as a value of the generic type **V**. The type of the value object can be different, depending on the requests. For example, when we receive the request to create a new post we will expect a <a href="https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/handlers/NewPostPayload.java" target="_blank">NewPostPayload</a> object to be serialized in the body of the request. We will use a special value named <a href="https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/handlers/EmptyPayload.java" target="_blank">EmptyPayload</a> for the cases in which we do not need to parse the body of the request.

Finally, our handler will simply return an instance of <a href="https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/Answer.java" target="_blank">Answer</a> which is a class with two simple fields:

* the HTTP code to return (200 = success, 404 = not found, etc., see the list <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html" target="_blank">here</a>)
* the body of the response: typically JSON or HTML code

Note that nothing present in the interface is Spark-specific, or related to any one particular framework. This will help us making all our handlers easily testable.

Now, we need just to bridge our request handlers to Spark's routes. We could do that in a few different ways, and we will go for creating a base class from which our request handlers should inherit. This class is <a href="https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/AbstractRequestHandler.java" target="_blank">AbstractRequestHandler</a>. 

<pre><code class="language-java">
public abstract class AbstractRequestHandler&lt;V extends Validable&gt; implements RequestHandler&lt;V&gt;, Route {

    private Class&lt;V&gt; valueClass;
    protected Model model;

    private static final int HTTP_BAD_REQUEST = 400;

    public AbstractRequestHandler(Class&lt;V&gt; valueClass, Model model){
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

    public final Answer process(V value, Map&lt;String, String&gt; queryParams, boolean shouldReturnHtml) {
        if (!value.isValid()) {
            return new Answer(HTTP_BAD_REQUEST);
        } else {
            return processImpl(value, queryParams, shouldReturnHtml);
        }
    }

    protected abstract Answer processImpl(V value, Map&lt;String, String&gt; queryParams, boolean shouldReturnHtml);


    @Override
    public Object handle(Request request, Response response) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        V value = objectMapper.readValue(request.body(), valueClass);
        Map&lt;String, String&gt; queryParams = new HashMap&lt;&gt;();
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

}
</code></pre>

We will create subclasses of _AbstractRequestHandler_, instantiate them and use them as Spark's routes. Spark will then invoke the method _handle_. This method will use Jackson to parse the body of the request and pass it to _process_. Process will check if the value is valid (using the _isValid_ method) and if it is not it will return an HTTP code indicating that the request was not valid. If instead the request is valid the method _processImpl_ is invoked. This method will be implemented by the different subclasses.

The rest of the _handle_ method get the result from _process_ and instruct Spark to send the response.

## How to write and use RequestHandlers

Now, let's see how to use this class. Before we had our logic defined in anonymous classes implementing the _Route_ interface. For example:

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

After the change we will move the logic to a separate class, like <a href="https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/main/java/me/tomassetti/handlers/PostsCreateHandler.java" target="_blank">PostsCreateHandler</a>:

<pre><code class="language-java">
public class PostsCreateHandler extends AbstractRequestHandler&lt;NewPostPayload&gt; {

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
}
</code></pre>


And we will use this class as our _Route_ (remember that _AbstractRequestHandler_, which is extended by _PostsCreateHandler_, implements _Route_).

<pre><code class="language-java">
post("/posts", new PostsCreateHandler(model));
</code></pre>

## Unit tests

At this point we can simply test our logic by writing tests for our _RequestHandlers_. It's simple because we do not need to mock anything related to Spark. We just need to mock our _Model_, which represents the way we access the database. However it has a simple interface and mocking it is straightforward. Let's look at some examples.

Class <a href="https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/test/java/me/tomassetti/handlers/PostsCreateHandlerTest.java" target="_blank">PostsCreateHandlerTest</a>:

<pre><code class="language-java">public class PostsCreateHandlerTest {

    @Test
    public void anInvalidNewPostReturnsBadRequest() {
        NewPostPayload newPost = new NewPostPayload();
        newPost.setTitle(""); // this makes the post invalid
        newPost.setContent("Bla bla bla");
        assertFalse(newPost.isValid());

        Model model = EasyMock.createMock(Model.class);
        replay(model);

        PostsCreateHandler handler = new PostsCreateHandler(model);
        assertEquals(new Answer(400), handler.process(newPost, Collections.emptyMap(), false));
        assertEquals(new Answer(400), handler.process(newPost, Collections.emptyMap(), true));

        verify(model);
    }

    @Test
    public void aPostIsCorrectlyCreated() {
        NewPostPayload newPost = new NewPostPayload();
        newPost.setTitle("My new post");
        newPost.setContent("Bla bla bla");
        assertTrue(newPost.isValid());

        Model model = EasyMock.createMock(Model.class);
        expect(model.createPost("My new post", "Bla bla bla", Collections.emptyList())).andReturn(UUID.fromString("728084e8-7c9a-4133-a9a7-f2bb491ef436"));
        replay(model);

        PostsCreateHandler handler = new PostsCreateHandler(model);
        assertEquals(new Answer(200, "728084e8-7c9a-4133-a9a7-f2bb491ef436"), handler.process(newPost, Collections.emptyMap(), false));

        verify(model);
    }

}</code></pre>

As you can see, in _anInvalidNewPostReturnsBadRequest_ we simply prepare an invalid _NewPostPayload_, and we pass it to an instance of _PostsCreateHandler_. We then pass an empty map (no url params needed here), and we invoke the method both with the parameter _shouldReturnHtml_ false and true, to verify that we get the same behavior in both cases. To test the answer is very easy: we just check that we get the expected _Answer_. In this case we expect the HTTP code 400 to be returned, because the request is not valid: this is because the _newPost_ value was not valid.

In the second test (_aPostIsCorrectlyCreated_) we verify that _model.createPost_ is invoked passing the values we specified in our _NewPostPayload_ instance. Easy, eh?

We have other tests in <a href="https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/test/java/me/tomassetti/handlers/PostsIndexHandlerTest.java" target="_blank">PostsIndexHandlerTest</a>:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/sparkTestingUnit/postsIndexHandlerTest.java %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

Here we just test that the JSON and HTML returned in the body of the Answer are the one expected. Pretty straightforward.

## Conclusions

I hope this post helped you by showing one possible way to approach testing Spark applications. This approach tries to be simple and effective, in the spirit of Spark. I can see two possible disadvantages with this approach:

* we add a bit of extra complexity by introducing _AbstractRequestHandler_
* we do not have unit tests for the "plumbing bits" of the application. For that part we rely on functional tests

I have used this approach in practice, obtaining decent results, however, it's not always the best choice: sometimes you want to create very simple applications (just a few hundreds lines of code), and you do not want to go through the hassle of introducing separate handler classes. In other cases you want to have high unit tests coverage. Needs can be different, and testing approaches should be chosen accordingly. I hope this tutorial can serve as a starting point. Happy testing!

{% include authorTomassetti.html %}

