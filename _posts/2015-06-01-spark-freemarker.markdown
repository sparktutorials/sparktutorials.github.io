---
layout: post
title:  "Spark and Freemarker: Exposing HTML and JSON from the same service"
author: <a href="http://tomassetti.me" target="_blank">Federico Tomassetti</a>
date:   2015-06-01 10:34:52
comments: true
summary: >
  In previous posts we have seen how to develop a RESTful service using Spark and we have implemented a service to manage a blog. In this post we are going to create an HTML view of the blog, showing how the same data can be exposed through both JSON and HTML.
---

<em>The code discussed in this post is available on <a href="https://github.com/sparktutorials/BlogService_SparkExample" target="_blank">GitHub</a></em>

## Offering the same data as both JSON and HTML
To offer the same data as JSON or HTML we could use different strategies. The two simplest ones are:

* using different endpoints for JSON and HTML
* use the Accept header to determine what format of data to return

The first strategy is very simple to implement: you just have to create different routes. For example, we could offer the JSON data in response to the endpoints <em>/service/posts</em> and <em>/service/posts/:id</em>, while offering HTML data in response to the endpoints <em>/posts</em> and <em>/posts/:id</em>. This is because we typically want shorter URLs for the content intended for human beings. In this post we will focus on the Accept-header strategy though, which requires a bit of work.

## Using the Accept header to decide to return JSON or HTML
An HTTP request reaching our service brings a lot of information, including the URL (of which we can parse specific parts to derive parameters), query parameters, a body and <a href="http://en.wikipedia.org/wiki/List_of_HTTP_header_fields" target="_blank">headers</a>.<br>
An interesting header is Accept. It can be used to specify the format that the caller is able to process or the formats it prefers. A web browser typically set this header to contains <em>text/html</em>. Other applications prefer to work with formats like JSON or XML (for the young kids out there: XML is a weird markup language we had to work with before we got JSON).

The actual content of the header can be quite complex, for example, my browser sends:

<b>text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8</b>

This is a list of formats in the which they are preferred by the browser (see <a href="http://en.wikipedia.org/wiki/Content_negotiation" target="_blank">content negotiation</a> for details).

Let's see how use the Accept header to decide when to return HTML from Spark (spoiler alert: it is easy!)

<pre><code class="language-java">
private static boolean shouldReturnHtml(Request request) {
    String accept = request.headers("Accept");
    return accept != null && accept.contains("text/html");
}


// ...jumping down in the code, where we define routes

// get all post (using HTTP get method)
get("/posts", (request, response) -> {
    if (shouldReturnHtml(request)) {
        // produce HTML
    } else {
        // produce JSON
    }
});
</code></pre>

In this example we either get a request specifying <em>text/html</em> in the Accept header or we provide JSON in all other cases. It could make sense to return an error instead of always producing JSON: if someone is asking for XML they are not going to be able to process the JSON we are sending back to them.

## Producing HTML programmatically
Let's see how we could generate HTML from pure Java. We are going to use <a href="http://j2html.com" target="_blank">j2html</a>. j2html is a simple Jave to HTML builder made by <a href="https://linkedin.com/in/davidaase" target="_blank">David Åse</a>, the person responsible for the Spark websites (if you are interested in joining the Spark project, you can read about how David got involved in this <a href="http://tomassetti.me/interview-with-david-ase-from-the-spark-web-framework-project/">interview</a>).

Let's start by adding the dependency in our POM file:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkFreemaker/mavenDep.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

Now we can use j2html and some Java 8 magic to transform a list of posts into a page that we can render:
<pre><code class="language-java">
return body().with(
      h1("My wonderful blog"),
      div().with(
          model.getAllPosts().stream().map((post) ->
                div().with(
                        h2(post.getTitle()),
                        p(post.getContent()),
                        ul().with(post.getCategories().stream().map((category) ->
                              li(category)).collect(Collectors.toList())
                        )
                )
          ).collect(Collectors.toList())
      )
).render();
</code></pre>

Let's take a look at what is happening here:

* we start from the body of the page and we insert two elements in it: a header (h1) and a div which will contain all the posts
* we then take all the posts and map each post to the corresponding piece of HTML code
* each single post is mapped to a div which contains a header (h2) for the title a paragraph (p) for the content and a list (ul) for the categories
* each single category is mapped to a list element (li)

I think j2html is great when prototyping HTML pages because you don't have to deal with the hassle of setting up a template engine. In addition to that is very useful when you need to generate small pieces of HTML to be used to compose larger pages. However if you want to build complex layouts you may want to use templates to achieve a stronger separation between logic and presentation. In the next paragraph we take a look such a template engine.

The example using j2html can be found in the <a href="https://github.com/sparktutorials/BlogService_SparkExample" target="_blank">GitHub repository</a> under the tag j2html.

## Producing HTML using the FreeMarker template engine
For large HTML content you may want to use templates. The basic idea is the you can let a designer create a sample page and then replace the sample content with placeholders that are going to be dynamically replaced by the actual content at each request. 
<br>Spark has native support for a lot of template engines (see <a href="https://github.com/perwendel/spark-template-engines" target="_blank">here</a> for the complete list).

One thing that I like about FreeMarker is that it is quite flexible. For example we can use different strategies to find the templates. The two most common approaches are:

* pack the templates inside the application (as part of the jar/war produced and deployed)
* leave the templates outside the application

The first strategy makes deployment easier: The package contains everything, including the templates. The second one allows for modifying the templates without having to re-package and redeploy the application.

I prefer the first option (at least when templates are reasonably stable) so we are going to see how we can store the templates as resource files.

Let's start by creating a file named <em>posts.ftl</em> under the directory <em>src/main/resources</em>. The template will not contain dynamic parts just this simple content:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkFreemaker/simpleHtml.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

Now we have to configure FreeMarker to look for templates among the resource files. It is as simple as writing a few lines of code before you start defining your routes:

<pre><code class="language-java">
FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
Configuration freeMarkerConfiguration = new Configuration();
freeMarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(BlogService.class, "/"));
freeMarkerEngine.setConfiguration(freeMarkerConfiguration);
</code></pre>

Now we have to specify which template to use for a specific route

<pre><code class="language-java">
// get all post (using HTTP get method)
get("/posts", (request, response) -> {
    if (shouldReturnHtml(request)) {
        response.status(200);
        response.type("text/html");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("posts", model.getAllPosts());
        return freeMarkerEngine.render(new ModelAndView(attributes, "posts.ftl"));
    } else {
        response.status(200);
        response.type("application/json");
        return dataToJson(model.getAllPosts());
    }
});
</code></pre>

And this should be the result:

<img class="img-bordered" src="/img/posts/sparkFreemarker/marvellousBlog.png" alt="Screenshot of blog">

## How to pass data to the template

Cool, we are able to load and display a template but so far the template does not contain any dynamic content: it is just producing the same page whatever content we have in our blog. Let's correct this.

In the code to render the template we used a map:

<pre><code class="language-java">
Map attributes = new HashMap<>();
attributes.put("posts", model.getAllPosts());

return freeMarkerEngine.render(new ModelAndView(attributes, "posts.ftl"));
</code></pre>

This is the mechanism to pass data to the template. As you can see we are already passing a list of posts to the template, now let's see how to display them:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkFreemaker/freemarker.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

What's happening here?

* we start by iterating on the posts (posts is a key in the map we passed to the FreeMarker engine, so we can access it here)
* for each post we display title, content and publishing_date
* we iterate on the categories of the single post and display them

And this should be the result, displaying a couple of posts:

<img class="img-bordered" src="/img/posts/sparkFreemarker/marvellousBlog2.png" alt="Screenshot of blog">

The example using FreeMarker is present in the <a href="https://github.com/sparktutorials/BlogService_SparkExample" target="_blank">GitHub repository</a> under the tag freemarker.

## Conclusions
As we have seen in this post Spark can be easily integrated with template engines and FreeMarker is a decent choice. I personally think that Spark is great for RESTful services but it does a pretty good job also for common user-facing web applications and it can be easily used for mixed applications (RESTful service + HTML interface, as we have seen in this post).

There are improvements to be done to use templates in a large application. For example you may want to define a common structure (header, navigation column, footer) to be used in all the different templates, without having to maintain copies. To understand how to do this and more advanced topics I suggest to take a look at the FreeMarker documentation.

{% include authorTomassetti.html %}
