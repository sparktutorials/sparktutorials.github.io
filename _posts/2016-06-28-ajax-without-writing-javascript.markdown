---
layout: post
title: "Creating an AJAX todo-list without writing JavaScript"
author: <a href="https://www.linkedin.com/in/davidaase" target="_blank">David Åse</a>
date: 2016-06-28 11:11:11
comments: true
summary: >
 Learn how to create a modern single page application in Spark and intercooler.js without writing JavaScript.
---

<div class="notification">
  <em>The source code for this tutorial can be found on <a href="https://github.com/tipsy/spark-intercooler" target="_blank">GitHub</a>. Please fork/clone and look while you read.
  <br>
  Big thanks to <a href="https://twitter.com/carson_gross" target="_blank">Carson Gross</a> for his <a href="https://github.com/carsongross/todomvc/commit/25e314742db6dcef8866ded1e795832d3ecc73ba" target="_blank">intercooler.js TodoMVC example</a>.</em>
</div>

## What You Will Learn
You will learn how to create a single page AJAX application that can create new todos, edit existing todos, filter todos on status, and more, all on the server side without writing any JavaScript. The tutorial uses a client side library, <a href="http://intercoolerjs.org/" target="_blank">intercooler.js</a>, which lets you write declarative AJAX applications using HTML attributes.

### Screenshot

<img src="/img/posts/sparkIntercooler/todoList.png" alt="Application Screenshot">

The todo-list is implemented in the style of <a href="http://todomvc.com/" target="_blank">TodoMVC</a>, which is a an app commonly used to evaluate frontend frameworks.

## Declaring your Spark routes
Normally in Spark, a route returns some string content in the response. When using intercooler, a lot of your routes will return a string created by the same code, so I created a void route-handler called ICRoute for the routes that alter state (post/put/delete) on the server side:
<pre><code class="language-java">
{% capture code %}{% include codeExamples/sparkIntercooler/TodoListMain.java %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

As you can see, if the response body is null, the request was handled by an ICRoute, and we should set the response body to contain the newly rendered todo-list view.  
The GitHub repo also has an <a href="https://github.com/tipsy/spark-intercooler/blob/master/src/main/java/BasicTodoList.java" target="_blank">example without ICRoute</a>, which returns a string in every route (no after-filter).

### Rendering the views
Almost every route returns the same template (in different states), and are handled by this render method:
<pre><code class="language-java">
{% capture code %}{% include codeExamples/sparkIntercooler/TodoListRender.java %}{% endcapture %}{{ code | xml_escape }}
</code></pre>
When you first GET the root, the request is not from intercooler, and the method renders index.vm (which also includes todoList.vm). When a PUT/POST/DELETE request is made with intercooler, the TodoList data object changes, and only todoList.vm needs to be re-rendered.

## The todo-list template
This is where the magic happens. The template contains a number of ic-attributes, which determine how your app functions:
<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkIntercooler/todoList.vm %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

You can see a form with the attribute **ic-post-to='/todos'**. This form contains the input field used to add new todos. Adding this attribute is literally all you need to do in order to enable AJAX form-posts with intercooler.

Each individual todo (inside the #foreach) have a checkbox, a label, and a button. These are also mapped directly to Spark routes using ic-attributes:
<pre><code class="language-java">
checkbox (toggle status)   ic-put-to='/todos/$todo.id/toggle_status'

button (delete todo)       ic-delete-from='/todos/$todo.id'

label (update todo-title)  ic-get-from='/todos/$todo.id/edit' ic-target='closest li'
                           ic-trigger-on='dblclick' ic-replace-target='true'
</code></pre>

The first two are pretty self explanatory, but the label one is a bit more complex. Since we want to allow inline editing of todos (double click to make the todo editable), we need to replace the content of the list with a form that we can submit. Intercooler GETs this form from '/todos/$todo.it/edit', and uses it to overwrite the current todo-item. The form looks like this:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkIntercooler/editTodo.vm %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

The form has a ic-put property which triggers on form submit, and a ic-get which triggers if the users exits edit mode (resetEscape). This bit actually requires a couple of lines of JavaScript (a key-listener).

## Conclusion
Creating an AJAX powered app in Spark and intercooler is extremely easy, and using HTTP verbs as HTML attributes feels very natural (PUT this here, POST this there, DELETE this). If you're used to writing full-fledged JavaScript applications you might feel that you're not in complete control of your application anymore. You can still use JavaScript to trigger intercooler functionality though, such as the resetEscape event in the above code.

Overall I think intercooler can be a very viable alternative for less complex AJAX apps, especially for developers suffering from JavaScript fatigue. Making an app non-js first then adding attributes to ajaxify it also seems to work well, at least for the simple cases I experimented with.

Please fork the example, play around with it, and let me know what you think in the comments!