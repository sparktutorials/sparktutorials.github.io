---
layout: post
title:  "Getting started with Spark and Docker"
date:   2015-04-14 10:34:52
author: Matthias Lübken from <a href="https://giantswarm.io/" target="_blank">GiantSwarm</a> 
comments: true
summary: > 
  This tutorial provides a short and simple example on how to get started with Java development on Docker. 
  <br>
  Spark was chosen for the tutorial because it can <em>"get you started with minimal overhead and upfront knowledge".
  </em> The tutorial is written by <a href="https://giantswarm.io/" target="_blank">GiantSwarm</a>'s&nbsp;</span>
  Matthias Lübken and originally appeared on their 
  <a href="http://blog.giantswarm.io/getting-started-with-java-development-on-docker" target="_blank">blog</a>.
---

##Background
This week Anna, Stephan, Timo and myself were at W-Jax a big conference in Munich on enterprise technologies and especially Java. The interest in Docker and Giant Swarm was astonishing. One question that came up quite often was: How do I get started with Java development on Docker? Personally, I'm a friend of small examples - leveraging a minimal framework with a couple of files and off you go. Unfortunately this is hard to find in the Java world, since most examples require some sort of IDE and deep knowledge in the appropriate web framework. With this article I try to fill this gap. It gets you started with Docker and Java with minimal overhead and upfront knowledge.

##The setup
There are tons of Java web stacks and we are not picking sides here. But we wanted a very minimal framework and chose Spark, a tiny Sinatra inspired framework for Java 8. If you look at the <a href="http://sparkjava.com/documentation.html" target="_blank">documentation</a> of Spark it uses Maven as its build tool. 
In our example we will leverage Maven and Docker’s layered file system to install everything from scratch and at the same time have small turn around times when recompiling changes.

So the prerequisites you need: no Java, no Maven, just Docker. Crazy, eh? ;-)

<img src="/img/posts/sparkDocker/sparkdocker.png" alt="Docker, Maven and Spark">

##The source and config Files

For our example you have to add three files:

* The Maven config: pom.xml
* A Java class: Hello.java
* The Dockerfile

The TL;DR readers who can’t wait, can just clone this repo: <a href="https://github.com/giantswarm/sparkexample">https://github.com/giantswarm/sparkexample</a>

##Setting up your POM file
The pom.xml contains a very basic Maven configuration. It configures the Spark dependencies using a Java 1.8 compiler and creates a fat jar with all the dependencies. I'm in no way a Maven expert so pull requests to make this example simpler and more streamlined are more than welcome.

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkDocker/mavenDep.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

##Hello.java
The assembly section of the pom.xml defines a main class that is called when the app starts: `sparkexample.Hello`. Lets create this file in the subdirectory `src/main/java/sparkexample/`:

<pre><code class="language-java">
package sparkexample;

import static spark.Spark.get;

public class Hello {

    public static void main(String[] args) {
        get("/", (req, res) -> {
            return "hello from sparkjava.com";
        });
    }

}
</code></pre>

As you can see this is modern Java code: it uses static imports and lambda expressions, which makes this example quite compact. The class contains a main method, with a response to a root request ("/"). As common with HelloWorld this response is just a simple string. Please consult the Spark <a href="http://sparkjava.com/documentation.html" target="_blank">documentation</a> for further information on expressing different routes.

##Dockerfile
Setting up your dockerfile is pretty straightforward:
<pre><code class="language-bash">
{% capture code %}{% include codeExamples/sparkDocker/dockerFile.html %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

The Dockerfile uses a plain Java image (<a href="https://registry.hub.docker.com/_/java/" target="_blank">java:8</a>) and starts with installing Maven. In the next step it only installs the project dependencies. We do this by adding the pom.xml and resolving the dependencies. As you will see, this allows Docker to cache the dependencies. In the next step we actually compile and package our app, and start it. 

If we now rebuild the app without any changes to the pom.xml, the previous steps are cached  and only the last steps are run. This makes turnaround times much faster.  

##Building and running
Once you have these three files in place, it is very easy to build the Docker image:

<pre><code class="language-bash">docker build -t giantswarm/sparkexample .</code></pre>

Note that this will take a while when you start it for the first time since it downloads and installs Maven and downloads all the project’s dependencies. Every subsequent start of this build will only take a few seconds, as again everything will be already cached.

Once the image is built, start it with:

<pre><code class="language-bash">docker run -d -p 4567:4567 giantswarm/sparkexample</code></pre>

And test it with:

<pre><code class="language-bash">curl localhost:4567</code></pre>

Now go ahead and change something in the source (like returning your own message) and rebuild this.... Isn't it great!?

##Conclusion
Although this a very basic example. I hope I got you started and hooked with Java development on Docker. I am looking forward to your comments and thoughts. What should we add to this example? Get your hands dirty, fool around with the files, and leave comments and pull requests with improvement suggestions.