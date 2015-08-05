---
layout: post
title: "Setting up a Spark Project with Maven"
author: <a href="https://www.linkedin.com/in/davidaase" target="_blank">David Åse</a>
date: 2015-04-02 11:34:52
summary: >
 In this tutorial you will learn how to set up a Spark project using Maven. It is aimed at Java beginners, and will show you how to set up your project in IntelliJ IDEA, Eclipse, and GitHub Atom
---

##IDE Guides
<a href="#intellij">- Instructions for IntelliJ IDEA</a><br>
<a href="#eclipse">- Instructions for Eclipse</a><br>
<a href="#atom">- Instructions for GitHub Atom</a>
 
##About Maven
Maven is a build automation tool used primarily for Java projects. It addresses two aspects of building software: First, it describes how software is built, and second, it describes its dependencies.

Maven projects are configured using a 
<a href="https://en.wikipedia.org/wiki/Apache_Maven#Project_Object_Model">
    Project Object Model
</a>, which is stored in a pom.<a href="https://en.wikipedia.org/wiki/XML" target="_blank">xml</a>-file. <br>Here's a minimal example:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkMaven/pom.xml %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

<h2 id="intellij">Instructions for IntelliJ IDEA</h2>

<br>Click "File" and select "New project...":
<img src="/img/posts/mavenTut/idea1.png" alt="">

<br>Select "Maven" on the left hand menu and click "Next":
<img src="/img/posts/mavenTut/idea2.png" alt="">

<br>Enter GroupId, ArtifactId and Verison, and click "Next":
<img src="/img/posts/mavenTut/idea3.png" alt="">

<br>Give your project a name and click "Finish":
<img src="/img/posts/mavenTut/idea4.png" alt="">

<br>Paste the following XML snippet:

<pre><code class="language-markup">
{% capture code %}{% include codeExamples/sparkMaven/sparkMaven.xml %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

<br>Into the generated pom.xml:
<img src="/img/posts/mavenTut/idea5.png" alt="">


<br>Paste the following Java snippet:

<pre><code class="language-java">
{% capture code %}{% include codeExamples/sparkMaven/sparkStart.java %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

<br>Into a new Class "Main.java":
<img src="/img/posts/mavenTut/idea6.png" alt="">

<br>Now you can run your Spark Application!

<h2 id="eclipse">Instructions for Eclipse</h2>
<h2 id="atom">Instructions for GitHub Atom</h2>

<style>#intellij, #eclipse, #atom {padding-top: 100px;}</style>