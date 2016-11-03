---
layout: post
title: "Developing with the Spark Debug Tools (beta)"
author: <a href="https://www.linkedin.com/in/davidaase" target="_blank">David Åse</a>
date: 2016-03-24 11:11:11
comments: true
summary: >
 In this tutorial you will learn how to use the Spark Debug Tools for easier development.
---

## What are the Spark Debug Tools?
The Spark Debug Tools currently consists of only one tool, the DebugScreen:

<img src="/img/posts/sparkDebugTools/sparkDebugScreen.png" alt="Spark Debug Tool">

### Debug Screen Features:

- Highlights exception-throwing code and provides easy stacktrace navigation
- Controls for Googling exception and copying stacktrace
- Panel showing important information:
  - Headers
  - Route parameters
  - Query paramters
  - Request attributes
  - Sessions attributes
  - Cookies 
  - Environment details

## Setting up your project

The Spark Debug Tool work best if your project follows a <a href="https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html" target="_blank">standard Maven directory layout</a>, 
so step one should be to create a Spark Maven project <a href="/2015/04/02/setting-up-a-spark-project-with-maven.html" target="_blank">(→ Tutorial)</a>.

## Enabling the DebugScreen
To enable the debug tool, add the following dependency to your pom-file:
<pre><code class="language-markup">
    {% capture code %}{% include codeExamples/sparkDebugTools/pom.xml %}{% endcapture %}{{ code | xml_escape }}
</code></pre>
Then, add the line **enableDebugScreen()** to your Spark application. This code shows a full example:

<pre><code class="language-java">
    {% capture code %}{% include codeExamples/sparkDebugTools/sparkDebugExample.java %}{% endcapture %}{{ code | xml_escape }}
</code></pre>

The debug tool uses the **exception(exceptionClass, exceptionHandler)** method from Spark, which is used for mapping Exceptions to handlers. The debug screen maps **Exception.class** to a DebugScreen instance, which allows the debug screen to catch *all* uncaught exceptions and render a nice error page.

## Beta status, please report bugs

The tool is currently in beta status, so please report bugs in the comments below, or create an issue on <a href="https://github.com/perwendel/spark-debug-tools/issues" target="_blank">GitHub</a>.

## Suggestions welcome

If you have an idea for something that would be nice to include in the Debug Screen, or an idea for another debug tool, please write your suggestion in the comments, or create an issue on <a href="https://github.com/perwendel/spark-debug-tools/issues" target="_blank">GitHub</a>.
