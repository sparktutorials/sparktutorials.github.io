---
layout: post
title:  "Getting started with Spark and Docker"
date:   2015-04-14 10:34:52
categories: jekyll update
author: Matthias Lübken from <a href="https://giantswarm.io/" target="_blank">GiantSwarm</a> 
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

<img src="/img/posts/sparkdocker.png" alt="Docker, Maven and Spark">

##The source and config Files

For our example you have to add three files:

* The Maven config: pom.xml
* A Java class: Hello.java
* The Dockerfile

The TL;DR readers who can’t wait, can just clone this repo: <a href="https://github.com/giantswarm/sparkexample">https://github.com/giantswarm/sparkexample</a>

##Setting up your POM file
The pom.xml contains a very basic Maven configuration. It configures the Spark dependencies using a Java 1.8 compiler and creates a fat jar with all the dependencies. I'm in no way a Maven expert so pull requests to make this example simpler and more streamlined are more than welcome.

<pre><code class="language-markup">
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
&lt;project xmlns=&quot;http://maven.apache.org/POM/4.0.0&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
  xsi:schemaLocation=&quot;http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd&quot;&gt;
  &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;

  &lt;groupId&gt;hellodocker&lt;/groupId&gt;
  &lt;artifactId&gt;hellodocker&lt;/artifactId&gt;
  &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;

  &lt;dependencies&gt;
    &lt;dependency&gt;
      &lt;groupId&gt;com.sparkjava&lt;/groupId&gt;
      &lt;artifactId&gt;spark-core&lt;/artifactId&gt;
      &lt;version&gt;2.0.0&lt;/version&gt;
    &lt;/dependency&gt;
  &lt;/dependencies&gt;
  &lt;build&gt;
    &lt;plugins&gt;
      &lt;plugin&gt;
        &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
        &lt;artifactId&gt;maven-jar-plugin&lt;/artifactId&gt;
        &lt;version&gt;2.4&lt;/version&gt;
        &lt;configuration&gt;
          &lt;finalName&gt;sparkexample&lt;/finalName&gt;
          &lt;archive&gt;
            &lt;manifest&gt;
              &lt;addClasspath&gt;true&lt;/addClasspath&gt;
              &lt;mainClass&gt;sparkexample.Hello&lt;/mainClass&gt;
              &lt;classpathPrefix&gt;dependency-jars/&lt;/classpathPrefix&gt;
            &lt;/manifest&gt;
          &lt;/archive&gt;
        &lt;/configuration&gt;
      &lt;/plugin&gt;
      &lt;plugin&gt;
        &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
        &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;
        &lt;version&gt;3.1&lt;/version&gt;
        &lt;configuration&gt;
          &lt;source&gt;1.8&lt;/source&gt;
          &lt;target&gt;1.8&lt;/target&gt;
        &lt;/configuration&gt;
      &lt;/plugin&gt;
      &lt;plugin&gt;
        &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
        &lt;artifactId&gt;maven-assembly-plugin&lt;/artifactId&gt;
        &lt;executions&gt;
          &lt;execution&gt;
            &lt;goals&gt;
              &lt;goal&gt;attached&lt;/goal&gt;
            &lt;/goals&gt;
            &lt;phase&gt;package&lt;/phase&gt;
            &lt;configuration&gt;
              &lt;finalName&gt;sparkexample&lt;/finalName&gt;
              &lt;descriptorRefs&gt;
                &lt;descriptorRef&gt;jar-with-dependencies&lt;/descriptorRef&gt;
              &lt;/descriptorRefs&gt;
              &lt;archive&gt;
                &lt;manifest&gt;
                  &lt;mainClass&gt;sparkexample.Hello&lt;/mainClass&gt;
                &lt;/manifest&gt;
              &lt;/archive&gt;
            &lt;/configuration&gt;
          &lt;/execution&gt;
        &lt;/executions&gt;
      &lt;/plugin&gt;
    &lt;/plugins&gt;
  &lt;/build&gt;
&lt;/project&gt;
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
FROM java:8 

# Install maven
RUN apt-get update
RUN apt-get install -y maven

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src
RUN ["mvn", "package"]

EXPOSE 4567
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "target/sparkexample-jar-with-dependencies.jar"]
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
