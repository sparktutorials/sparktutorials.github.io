---
layout: post
title: "Setting up Spark with Gradle"
author: <a href="https://www.facebook.com/leonan.luppi" target="_blank">Leonan Luppi</a>
date: 2015-08-23 10:24:03
comments: true
summary: >
 We will learn how to set up a Spark Application using Gradle. For that we will use Eclipse Luna IDEA and Gradle 2.5.
 --

##Gradle
 Gradle is a build automation tool build on top of concepts of Maven and Ant using groovy language instead of XML language. It can be use on Java, Groovy and Scala project, setting up tasks, dependencies and build order.

##Installation
First we need install gradle IDEA on Eclipse, so let's open our Eclipse IDEA:

<br>Click in "Window" and select "Eclipse Marketplace..."
<img src="/img/posts/settingup-spark-gradle/1.png" alt="">

<br>Search for "gradle" term and select the first one: "Gradle IDE Pack XXX" then install normally and restart your Eclipse.
<img src="/img/posts/settingup-spark-gradle/2.png" alt="">

<br>Click in "New" and select "Other" then select "Gradle Project..."
<img src="/img/posts/settingup-spark-gradle/3.png" alt="">

<br>Put a "Project Name" then select Java Quickstart in "Sample Project"
<img src="/img/posts/settingup-spark-gradle/4.png" alt="">

<br>Look a familiar file struct, but instead of "pom.xml" now you have "build.gradle" let's open your "build.gradle"
<img src="/img/posts/settingup-spark-gradle/5.png" alt="">

<br>We have a "dependencies{}" object, we need to add spark-core dependencies on our project. **So add: compile 'com.sparkjava:spark-core:2.2'**
<img src="/img/posts/settingup-spark-gradle/6.png" alt="">

<br>We have a "dependencies{}" object, we need to add spark-core dependencies on our project.
<pre><code class="language-markup">
dependencies {
    compile 'com.sparkjava:spark-core:2.2'
    testCompile group: 'junit', name: 'junit', version: '4.+'
}
</code></pre>
<img src="/img/posts/settingup-spark-gradle/6.png" alt="">

<br>Right click select "Gradle" then "Refresh Dependencies.."
<img src="/img/posts/settingup-spark-gradle/7.png" alt="">

<br> Now we can CODE!

<br> On "src/main/java" delete "org.gradle" package and add a new Class "Main.java". Put a simple hello world like...
<pre><code class="language-markup">
import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
</code></pre>
<img src="/img/posts/settingup-spark-gradle/8.png" alt="">

<br>Now everything is ready for you runn your application. Enjoy!

