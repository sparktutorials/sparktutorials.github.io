---
layout: post
title: "Deploying Spark on Heroku"
author: <a href="https://www.linkedin.com/in/davidaase" target="_blank">David Åse</a>
date: 2015-08-24 11:11:11
comments: true
summary: >
 In this tutorial you will learn how to deploy a Hello World application on Heroku!
---

##Setup
Before we get started, there are a few things we need to do:

* Create a free Heroku account <a href="https://signup.heroku.com/dc" target="_blank">(sign up)</a>
* Install <a href="https://toolbelt.heroku.com/" target="_blank">Heroku Toolbelt</a>
* Install <a href="https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html" target="_blank">Maven</a> 
* Set up the Spark Hello World example with Maven <a href="/2015/04/02/setting-up-a-spark-project-with-maven.html" target="_blank">(→ Tutorial)</a>

##Configuring Maven
This is actually where most of the work is done. In order to easily deploy a Java application anywhere, you have to create a jar file containing your application and all of its dependencies. Open the pom.xml of your Spark Maven project and add the following configuration under your dependencies:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/herokuDeploy/maven.xml %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

##Configuring Heroku
First of all we have to create a Heroku application. This can be done by running <samp>heroku create appname</samp>:

<pre><code class="language-bash">
heroku create spark-heroku-example
</code></pre>

Configuring Heroku deployment is pretty straightfoward using the Heroku Maven plugin. We specify the JDK version and the app-name, along with the launch config:
<pre><code class="language-markup">
{% capture code %}{% include codeExamples/herokuDeploy/heroku.xml %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

##Making Spark listen to the correct port
The only thing left is making sure Spark can handle your requests. Heroku assigns your application a new port every time you deploy it, so we have to get this port and tell Spark to use it.

<pre><code class="language-java">
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        get("/hello", (req, res) -> "Hello Heroku World");
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

}
</code></pre>

Now we can deploy our application using <samp>mvn heroku:deploy</samp>:

<pre><code class="language-bash">
mvn heroku:deploy
</code></pre>

That's it. Our application is now avilable at: <a href="https://spark-heroku-example.herokuapp.com/hello" target="_blank">https://spark-heroku-example.herokuapp.com/hello</a>

The full source code for this example can be found at <a href="https://github.com/tipsy/spark-heroku-example" target="_blank">GitHub</a>
