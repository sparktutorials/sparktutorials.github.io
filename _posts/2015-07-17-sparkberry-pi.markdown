---
layout: post
title:  "Spark on Raspberry Pi"
date:   2015-07-17 19:02:00
author: <a href="https://sites.google.com/a/athaydes.com/renato-athaydes/" target="_blank">Renato Athaydes</a>
comments: true
summary: > 
  This tutorial will show you how to turn your Raspberry Pi into a webserver in a matter of minutes, 
  using Spark and Groovy. Spark was chosen for the tutorial because "Spark is as easy as it gets".
  <br>
  </em> The tutorial is written by Renato Athaydes and originally appeared on his 
  <a href="https://sites.google.com/a/athaydes.com/renato-athaydes/posts/agroovywebserverrunningonraspberrypiinminutes" target="_blank">google site</a>.
---

## Background
I just bought a Raspberry Pi to have some fun. First with code, and hopefully with hardware later.

After setting it up (with the Raspian OS, a Linux distro for the Pi), making sure it could connect to the internet and that I could ssh into it from my laptop, the first thing I wanted to do was to get it to run a webserver.

I recently attended a <a href="http://www.meetup.com/Stockholm-Java-User-Group/events/218888072/" target="_blank">Java meetup</a> (in fact, the very first one in Stockholm) where one of the speakers demoed the Spark micro-framework, and it really caught my attention. Starting up a webserver and providing a simple REST API (or just plain HTML) with Spark is as easy as it gets, but is it also that easy on the Pi? <br>
Well, if you want to use Java, you would need to first get the required jars, then compile all sources before you can run anything. It's not fun to manually download jars, place the files in the right places so that you can give Java and javac the right classpath and so on, specially in an environment where an IDE is not a viable option... There should be a better way. And of course, there is!

## Getting groovy with it
I have previously written about how easy it is to set up a webserver using Groovy, so I was already inclined to use Groovy for this project... you see, using @Grab, all your dependencies will be downloaded automatically. No need to install Maven or Gradle! All you need, besides the JDK (which already comes installed in the Raspian image), is to have Groovy installed.

Now, you may ask, just how easy is it to install Groovy in the Raspberry Pi?

Well, here's what you need to type in the terminal to get the job done:

<pre><code class="language-bash">
curl -s get.gvmtool.net | bash
source "$HOME/.gvm/bin/gvm-init.sh"
gvm install groovy
</code></pre>

<em>Note: here, we use GVM to get Groovy. See <a href="http://gvmtool.net" target="_blank">http://gvmtool.net</a></em>

This should take less than a minute (the download of version 2.3.9 is about 32MB).

You can check that you have groovy installed by running:

<pre><code class="language-bash">
groovy -version
</code></pre>

Which should print something like:

<samp>Groovy Version: 2.3.9 JVM: 1.8.0 Vendor: Oracle Corporation OS: Linux</samp>

## Setting up Spark
The environment is set up now, and all you have to do is write some code. And that's the fun part! The code required to get started with Spark is ridiculously simple:

<pre><code class="language-java">
@Grab(group = 'com.sparkjava', module = 'spark-core', version = '2.3')
import static spark.Spark.*

get '/hello', { req, res -> 'Hello from your groovy Sparkberry Pi!' }
</code></pre>

Save this code in a file called <samp>Server.groovy</samp>, then run it:

<pre><code class="language-bash">
groovy Server.groovy
</code></pre>

Once you see some output from the application (which does take a little while in the Pi, unfortunately), hit the following URL with your browser (replace the IP address with your Raspberry Pi's IP):

<samp>http://192.168.1.18:4567/hello</samp>

Notice that, even though Spark was written with Java 8 closures in mind, with the latest versions of Groovy, Groovy closures will also work just fine. 

If you want to serve static files from a sub-directory (here "/public"), you just have to add this line:

<samp>staticFileLocation "public"</samp>

## Building HTML
If you fancy serving some dynamic HTML as well, you can write plain Groovy code using the <a href="http://groovy.codehaus.org/api/groovy/xml/MarkupBuilder.html" target="_blank">MarkupBuilder</a> class.

Create another file in the same directory as <samp>Server.groovy</samp> called <samp>Page.groovy</samp>:

<pre><code class="language-java">
import groovy.xml.MarkupBuilder as Builder

static String createPage(Map queryParams) {
  def writer = new StringWriter()
  new Builder(writer).html {
    h2 'A Dynamic Webpage!'
    div(id: 'query-params') {
      if (!queryParams) li 'No query parameters given'
      else ul {
        for (key in queryParams.keySet()) {
          li "Query param: $key -> ${queryParams[key]}"
        }
      }
    }
    p "Current date: ${new Date()}"
  }
  writer.toString()
}
</code></pre>

Now, your complete <samp>Server.groovy</samp> file should look like this:

<pre><code class="language-java">
@Grab(group = 'com.sparkjava', module = 'spark-core', version = '2.3')
import static spark.Spark.*

println "Configuring server..."

staticFileLocation "public"

get '/hello', { req, res -> 'Hello from Groovy!' }
get '/page',  { req, res -> Page.createPage(req.queryMap().toMap()) }
</code></pre>

Now just run it! Let Groovy know there's more than one Groovy file in the current directory by adding the classpath option as follows:

<pre><code class="language-bash">
groovy -cp ./ Server.groovy
</code></pre>

And that's it. You've got a hassle-free webserver running on your Raspberry Pi in just a few minutes!
