---
layout: post
title:  "Spark and Testing - Part 2: Functional tests"
author: <a href="http://tomassetti.me" target="_blank">Federico Tomassetti</a>
date:   2015-10-01 10:34:52
comments: true
summary: >
  In this tutorial series you will learn an approach for writing functional tests for Spark applications from the ground up. In part one we discussed what and when to test, and wrote some unit tests. In this post we complete the discussion covering functional tests.
---

<div class="notification"><em>This is part two of a two-part tutorial series on testing in which we outline how to write a testable Spark application from the ground up. If you already have a finished application that you want to start testing using the approach described here, some refactoring will be required. 
<br/>
As always <del>batteries</del> <a href="https://github.com/sparktutorials/BlogService_SparkExample">code is included</a>.
</em></div>

##Goal of the functional tests

The terminology for test types is rather confused: different people use the same term to indicate different things and the same kind of tests can be indicated by different names, depending on who you are talking to. In this post we call functional tests tests which verify, at an high level, that functionalities needed by the user are implemented correctly.

These tests should not be check technical implementation details but only the behavior of the application as perceived by the user.

We do not consider neither non-functional featurs like response times, or the load that the application can handle.

Our functional tests should be readable for Project Managers and other stakeholders. In some way they capture functional requirements and help us to verify those requirements are met.

In this tutorial we will realize functional tests which:
 
 * start the application
 * perform some operation (like creating a post)
 * verify the results
 * shutdown the application

To write these tests we will use Cucumber.

##Cucumber

Our functional tests should be declarative and easy to read. For this reason we are going to use Cucumber. Cucumber is a well known and mature solution oriented to support Behaviour-Driven Development (BDD). It is available for several languages including Ruby and Java.

### Ruby? Why?

We are going to write are tests using Ruby for two reasons: on one hand Ruby is amazing for writing declareative and concise code and those are very good qualities for our tests. On the other end this enforces very effectively a strong separation between our tests and our application: we are going to interface with the code under tests by runnin the whole application, not by accessing single classes or methods. Given we are using a different language for tests there is not temptation to sneak in a call to a Java method (no, you are not allowed to doing so by using JRuby!).

Anyway do not worry I am going to hold your hand as we jump in the Ruby world. In the end you could even like it.

### Install it

First of all you need to install Ruby. You are a grown-up, you can do that without direction.

Once you have done that you could install cucumner by using gem, the dependencies management tool which comes with Ruby. You can install cucumber among your system libraries sinply by running:

<pre><code class="language-bash">
gem install cucumber.
</code></pre>

I suggest however to use bundler, a tool which permit to install dependencies locally. Let's start by creating a directory named `functional_tests` under the root of the project and then create a file named `Gemfile` (no extension):

<pre><code class="language-ruby">
source 'https://rubygems.org'
gem 'cucumber'
gem 'rest-client'
gem 'rspec'
gem 'rspec-expectations'
</code></pre>

And let's make bundler find the libraries and installing them for us:

<pre><code class="language-bash">
# to install bundler itself
gem install bundler
# now install everything, locally
bundle install --path vendor/bundle
</code></pre>

## Plan

Now, writing the functional tests per se will be simple but first we have to create all the infrastructure. This is where normally I reach a fairly high level of desperation. What we need to do is theoretically very simple:

 * create a clean database
 * run the application with that database
 * perform our steps
 * stop the application
 * destroy the database

In practice you need all sort of synchronization, because you do not want to start using your application before it is finished the initialization process, right? 

I find that in general spawn and manage processes seem to be much simpler and nicer in Ruby, so I am sorry for the ones that did not listen and went on writing their functional tests in Java :)

### Infrastructure needed

We are going to run the database in a Docker container because it is a nice way to create a reproducible environement. Ok, we are doing that also to show off a bit. 

Of course if you are not using Linux it means that you have to install Boot2docker. That would mean having to run a comple of extra commands here and there to start Boot2docker.

You should have all the code in the repository, you just need to run this to create the docker container (think about it as the image of an OS with just your DB installed:

<pre><code class="language-bash">
build_docker_container_for_functests.sh
</code></pre>

This should create a docker image named `blog_functests_db_container`. Let's double check that by typing `docker images`:

<img src="/img/posts/docker_images.png" alt="List of docker images">

##Examples of tests

##Conclusions

{% include authorTomassetti.html %}