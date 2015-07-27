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

At this point we can simply test our logic by writing tests for our _RequestHandlers_. It is simple because we do not need to mock anything related to Spark. We just need to mock our _Model_, which represents the way we access the database. However it has a simple interface and mocking it is straightforward. Let's look at some examples.

Class (PostsCreateHandlerTest)[https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/test/java/me/tomassetti/handlers/PostsCreateHandlerTest.java]:

<pre><code class="language-java">ppublic class PostsCreateHandlerTest {

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

As you can see in _anInvalidNewPostReturnsBadRequest_ we simply prepare an invalid _NewPostPayload_ and we pass it to an instance of _PostsCreateHandler_. We then pass an empty map (no url params needed here), and we invoke the method both with the parameter _shouldReturnHtml_ false and true, to verify that we get the same behavior in both cases. To test the answer is very easy: we just check that we get the expected _Answer_. In this case we expect the HTTP code 400 to be returned, because the request is not valid: this is beucuase the _newPost_ value was not valid.

In the second test (_aPostIsCorrectlyCreated_) we verify that _model.createPost_ is invoked passing the values we specified in our _NewPostPayload_ instance. Easy, eh?

We have other tests in (PostsIndexHandlerTest)[https://github.com/sparktutorials/BlogService_SparkExample/blob/unit_tests/src/test/java/me/tomassetti/handlers/PostsIndexHandlerTest.java]:

<pre><code class="language-java">public class PostsIndexHandlerTest {

    @Test
    public void emptyListIsHandledCorrectlyInHtmlOutput() {
        Model model = EasyMock.createMock(Model.class);
        expect(model.getAllPosts()).andReturn(Collections.EMPTY_LIST);
        replay(model);

        PostsIndexHandler handler = new PostsIndexHandler(model);
        String expectedHtml = "<body><h1>My wonderful blog</h1><div></div></body>";
        assertEquals(new Answer(200, expectedHtml), handler.process(new EmptyPayload(), Collections.emptyMap(), true));

        verify(model);
    }

    @Test
    public void aNonEmptyListIsHandledCorrectlyInHtmlOutput() {
        Model model = EasyMock.createMock(Model.class);

        Post post1 = new Post();
        post1.setTitle("First post");
        post1.setContent("First post content");
        post1.setCategories(ImmutableList.of("Howto", "BoringPosts"));

        Post post2 = new Post();
        post2.setTitle("Second post");
        post2.setContent("Second post content");
        post2.setCategories(ImmutableList.of());

        expect(model.getAllPosts()).andReturn(ImmutableList.of(post1, post2));
        replay(model);

        PostsIndexHandler handler = new PostsIndexHandler(model);
        String expectedHtml = "<body><h1>My wonderful blog</h1><div><div><h2>First post</h2><p>First post content</p><ul><li>Howto</li><li>BoringPosts</li></ul></div><div><h2>Second post</h2><p>Second post content</p><ul></ul></div></div></body>";
        assertEquals(new Answer(200, expectedHtml), handler.process(new EmptyPayload(), Collections.emptyMap(), true));

        verify(model);
    }

    @Test
    public void emptyListIsHandledCorrectlyInJsonOutput() {
        Model model = EasyMock.createMock(Model.class);
        expect(model.getAllPosts()).andReturn(Collections.EMPTY_LIST);
        replay(model);

        PostsIndexHandler handler = new PostsIndexHandler(model);
        String expectedHtml = "[ ]";
        assertEquals(new Answer(200, expectedHtml), handler.process(new EmptyPayload(), Collections.emptyMap(), false));

        verify(model);
    }

    @Test
    public void aNonEmptyListIsHandledCorrectlyInJsonOutput() {
        Model model = EasyMock.createMock(Model.class);

        Post post1 = new Post();
        post1.setTitle("First post");
        post1.setContent("First post content");
        post1.setCategories(ImmutableList.of("Howto", "BoringPosts"));

        Post post2 = new Post();
        post2.setTitle("Second post");
        post2.setContent("Second post content");
        post2.setCategories(ImmutableList.of());

        expect(model.getAllPosts()).andReturn(ImmutableList.of(post1, post2));
        replay(model);

        PostsIndexHandler handler = new PostsIndexHandler(model);
        String expectedHtml = "[ {\n" +
                "  \"post_uuid\" : null,\n" +
                "  \"title\" : \"First post\",\n" +
                "  \"content\" : \"First post content\",\n" +
                "  \"publishing_date\" : null,\n" +
                "  \"categories\" : [ \"Howto\", \"BoringPosts\" ]\n" +
                "}, {\n" +
                "  \"post_uuid\" : null,\n" +
                "  \"title\" : \"Second post\",\n" +
                "  \"content\" : \"Second post content\",\n" +
                "  \"publishing_date\" : null,\n" +
                "  \"categories\" : [ ]\n" +
                "} ]";
        assertEquals(new Answer(200, expectedHtml), handler.process(new EmptyPayload(), Collections.emptyMap(), false));

        verify(model);
    }

}</code></pre>

Here we just test that the JSON and HTML returned in the body of the Answer are the one expected. Really straightforward.

##Conclusions

I hope this post helped you to see one possible way to approach testing Spark applications. This approach tries to be simple and effective, in the spirit of Spark. I can see two possible disadvantages with this approach:

* we add a bit of extra complexity by introducing _AbstractRequestHandler_
* we do not have unit tests for the "plumbing bits" of the application. For that part we rely only on functional tests

I have used this approach in practice, obtaining decent results. However this approach is not always applicable: sometimes you want to write very simple applications, just a few hundreds lines of code and you do not want to go through the hassle of introducing separate handler classes. In another cases you want to have an high unit tests coverage. Needs can be different and testing approaches could need to be tailored because of that, anyway I hope this can serve as a starting point.

We think testing is a very interesting and important topics so we will try to write again about testing.

Have fun with Spark!

{% include authorTomassetti.html %}