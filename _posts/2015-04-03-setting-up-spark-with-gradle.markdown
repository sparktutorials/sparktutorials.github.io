---
layout: post
title: "Setting up Spark with Gradle"
author: <a href="https://github.com/leonanluppi" target="_blank">Leonan Luppi</a>
date: 2015-04-03 10:24:03
comments: true
summary: >
 We will learn how to set up a Spark application using Gradle. For that we will use Eclipse Luna IDE and Gradle 2.5.
---

## Gradle
 Gradle is a build automation tool that builds upon the concepts of Apache Ant and Apache Maven and introduces a Groovy-based domain-specific language (DSL) instead of the more traditional XML form of declaring the project configuration.

## Installation
First, we need to install Gradle IDE for Eclipse:

<br>Click "Window" and select "Eclipse Marketplace..."
<img src="/img/posts/settingup-spark-gradle/1.png" alt="">

<br>Search for "gradle" and select the first result ("Gradle IDE Pack XXX"), then install and restart Eclipse.
<img src="/img/posts/settingup-spark-gradle/2.png" alt="">

<br>Click "New" and select "Other", then select "Gradle Project..."
<img src="/img/posts/settingup-spark-gradle/3.png" alt="">

<br>Enter a project name, then select "Java Quickstart" in the sample project dropdown:
<img src="/img/posts/settingup-spark-gradle/4.png" alt="">

<br>Now you have a "build.gradle" file (similar to pom.xml in Maven). Let's look inside:
<img src="/img/posts/settingup-spark-gradle/5.png" alt="">

<br>We need to add spark-core dependencies to the **dependencies{}** object. We can leave **testCompile** as it is, and change the **compile** field to **compile 'com.sparkjava:spark-core:2.3'**.
<pre><code class="language-java">
dependencies {
    compile 'com.sparkjava:spark-core:2.3'
    testCompile group: 'junit', name: 'junit', version: '4.+'
}
</code></pre>
<img src="/img/posts/settingup-spark-gradle/6.png" alt="">

<br>Right click select "Gradle" then "Refresh Dependencies.."
<img src="/img/posts/settingup-spark-gradle/7.png" alt="">

<br>Now we can CODE!

<br>In "src/main/java" you can delete "org.gradle" package, and add a new Class "Main.java":
<pre><code class="language-java">
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
</code></pre>
<img src="/img/posts/settingup-spark-gradle/8.png" alt="">

<br>Now everything is ready for you to run your application. Enjoy!
