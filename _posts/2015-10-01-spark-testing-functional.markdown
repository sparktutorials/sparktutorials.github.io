---
layout: post
title:  "Spark and Testing - Part 2: Functional tests"
author: <a href="http://tomassetti.me" target="_blank">Federico Tomassetti</a>
date:   2015-10-01 10:34:52
comments: true
summary: >
  In this tutorial series you will learn an approach for writing functional tests for Spark applications from the ground up. In part one we discussed what and when to test, and wrote some unit tests. In this post we complete the discussion covering functional tests.
---

<div class="notification"><em>This is part two of a two-part tutorial series on testing in which we outline how to write a testable Spark application from the ground up. If you already have a finished application that you want to start testing using the approach described here, some refactoring will be required. </em></div>

##Goal of the functional tests

The terminology for tests is rather confused: different people use the same term to indicate different things and the same kind of tests is indicated by different names, depending on who you are talking to. In this post we call functional tests tests which verify that same high level functionality needed by the user is working correctly.

These tests should not be check technical implementation details but only the behavior of the application as perceived by the user.

Our functional tests should be readable for Project Managers and other stakeholders. In some way they capture functional requirements and verify those requirements are met.

In this tutorial we will realize functional tests which:
 
 * start the application
 * perform some operation (like creating a post)
 * verify the results
 * shutdown the application

To write these tests we will use Cucumber.

##Cucumber

##Infrastructure needed

##Examples of tests

##Conclusions

{% include authorTomassetti.html %}