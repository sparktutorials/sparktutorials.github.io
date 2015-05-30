---
layout: default
---

<div class="home">

<div class="sister-project" markdown="1">
SparkTutorials has moved to the official Spark webpage at [http://sparkjava.com/tutorials/](http://sparkjava.com/tutorials/)
<br>
I have stopped writing tutorials for Spark though, focusing on my new Java/Kotlin web framework [Javalin](https://javalin.io)
</div>
    
<hr>
    
<div class="posts">
    {% for post in site.posts %}
      <div class="post">
        <h2><a class="post-link" href="{{ post.url | prepend: site.baseurl }}">{{ post.title }}</a></h2>
        <div class="post-meta">
            {{ post.date | date: "%b %-d, %Y" }}
            {% if post.author %} • Written by {{ post.author }} {% endif %} • Spark Framework Tutorials
         </div>
        <p>{{ post.summary }}</p>
      </div>
    {% endfor %}
  </div>
</div>
