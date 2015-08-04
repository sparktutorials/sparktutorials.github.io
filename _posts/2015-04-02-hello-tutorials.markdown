---
layout: post
title:  "Hello Tutorials!"
author: <a href="https://www.linkedin.com/in/davidaase" target="_blank">David Åse</a>
date:   2015-04-02 10:34:52
summary: >
 This website will be used to collect tutorials about Spark. We aim to provide one tutorial every month until summer:
 <ul>
   <li>April 2015: Spark for REST APIs: Using Spark, Lombok and Jackson to reduce Java boilerplate</li>
   <li>May 2015: Spark and data persistence: Integration with sql2o</li>
   <li>June 2015: Spark for websites: Working with Freemarker</li>
   <li>July 2015: Spark in the Cloud: AWS and Docker</li>
 </ul>
 
 If you have suggestions for tutorials, or if you want your tutorial posted on this site, please leave a comment or contact us on our 
 <a href="https://www.facebook.com/sparkjava" target="_blank">facebook page</a>.
---
 
 This website will be used to collect tutorials about Spark. We aim to provide one tutorial every month until summer:
 
 * April 2015: Spark for REST APIs: Using Spark, Lombok and Jackson to reduce Java boilerplate
 * May 2015: Spark and data persistence: Integration with sql2o
 * June 2015: Spark for websites: Working with Freemarker
 * July 2015: Spark in the Cloud: AWS and Docker
 
 If you have suggestions for tutorials, or if you want your tutorial posted on this site, please leave a comment or contact us on our facebook page.

##Getting started with Spark

Add the maven dependency:

<pre><code class="language-markup">
&lt;dependency&gt;
    &lt;groupId&gt;com.sparkjava&lt;/groupId&gt;
    &lt;artifactId&gt;spark-core&lt;/artifactId&gt;
    &lt;version&gt;2.2&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>


Set up your first route:

<pre><code class="language-java">
import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
</code></pre>


Run and view:
<pre><code class="language-bash">
http://localhost:4567/hello
</code></pre>
