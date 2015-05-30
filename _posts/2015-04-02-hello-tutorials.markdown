---
layout: post
title:  "Hello Tutorials!"
date:   2015-04-02 10:34:52
categories: jekyll update
summary: This website will be used to collect tutorials about Spark.
---

This website will be used to collect tutorials about Spark. We aim to provide one tutorial every month until summer:

* April 2015: Spark for REST APIs: Using Spark, Lombok and Jackson to reduce Java boilerplate
* May 2015: Spark and data persistence: Integration with sql2o
* June 2015: Spark for websites: Working with Freemarker
* July 2015: Spark in the Cloud: AWS and Docker

If you have suggestions for tutorials, or if you want your tutorial posted on this site, please leave a comment or contact us on our facebook page.

##Getting started with Spark

Add the maven dependency:
{% prism markup %}
<dependency>
    <groupId>com.sparkjava</groupId>
    <artifactId>spark-core</artifactId>
    <version>2.1</version>
</dependency>
{% endprism %}

Set up your first route:

{% prism java %}
import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
{% endprism %}

Run and view:
{% prism bash %}
http://localhost:4567/hello
{% endprism %}
