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

We are going to run the database in a Docker container because it is a nice way to create a reproducible environement. Ok, we are doing that also to show off a bit. Using a container to run our database we akways sure to start from a given state: given we do not persist changes to our container previous tests run will not pollute the DB: we will always have a fresh copy at disposal for our tests.

Of course if you are not using Linux it means that you have to install Boot2docker. That would mean having to run a comple of extra commands here and there to start Boot2docker.

You should have all the code in the repository, you just need to run this to create the docker container (think about it as the image of an OS with just your DB installed:

<pre><code class="language-bash">
build_docker_container_for_functests.sh
</code></pre>

This should create a docker image named `blog_functests_db_container`. Let's double check that by typing `docker images`:

<img src="/img/posts/docker_images.png" alt="List of docker images">

### What we need to do before and after each test

In our tests we will have to start and stop both the database and the application and we are going to do that for each single test. To do so we are going to write Cucumber hooks. We create `functional_tests/features/support/hooks.rb` and write this inside:

<pre><code class="language-ruby">
#encoding: utf-8

require 'rest-client'

def create_clean_db
    started = system 'sh start_db.sh &'
    raise Exception.new('Unable to start DB') unless started
    attempts_left = 30
    while attempts_left > 0
        up_and_running = system 'sh db_is_up.sh'
        return if up_and_running
        puts "Waiting for db... (attemps left #{attempts_left})"
        sleep(1)
        attempts_left = attempts_left - 1
    end
    stop_db
    raise Exception.new('DB does not respond, giving up')
end

def stop_db
    system 'sh kill_all_functests_db_containers.sh'
end

def application_up_and_running?
    begin
        RestClient.get 'http://localhost:4567/alive'
        return true
    rescue Exception => e
        $stdout.puts "Error #{e}"
        return false
    end
end

def start_application
    res = system 'sh start_application.sh'    
    attempts_left = 30
    while attempts_left > 0
        up_and_running = application_up_and_running?
        return if up_and_running
        $stdout.puts "Waiting for the application... (attemps left #{attempts_left})"
        sleep(2)
        attempts_left = attempts_left - 1
    end
    stop_application
    raise Exception.new('The application does not respond, giving up')
end

def stop_application
    res = system 'sh stop_application.sh'
end

Before do |scenario|
    # in case there are leftovers
    stop_application
    stop_db

    create_clean_db
    start_application
end

After do |scenario|
    stop_application
    stop_db
end    
</code></pre>

So before each test start we ensure that the application is killed, if it was still running, and the database is shutdown. Note that this should not be necessary but it is better to exceed on the safe side. Then we start the db (`create_clean_db`) and the application (`start_application`).

Let's see in more details how we manage the db and the application.

### Managing the database

These are the functions we used to manage the DB:

<pre><code class="language-ruby">
def create_clean_db
    started = system 'sh start_db.sh &'
    raise Exception.new('Unable to start DB') unless started
    attempts_left = 30
    while attempts_left > 0
        up_and_running = system 'sh db_is_up.sh'
        return if up_and_running
        puts "Waiting for db... (attemps left #{attempts_left})"
        sleep(1)
        attempts_left = attempts_left - 1
    end
    stop_db
    raise Exception.new('DB does not respond, giving up')
end

def stop_db
    system 'sh kill_all_functests_db_containers.sh'
end
</code></pre>

We start the database using the `start_db.sh` script. It starts docker in daemon mode and return control to our Ruby function. At that point we have to wait until the database is up and running because otherwise our application would complain and fail.
We do that by calling `db_is_up.sh` every second for up to 30 seconds, after which we give up.

To check if the database is up we just try to connect to it. This is the content of `db_is_up.sh`:

<pre><code class="language-bash">
psql -h 127.0.0.1 -p 7500 -U blog_owner -d blog -c "select 1;"
</code></pre>

### Managing the application

This is the code we use to start and stop the application:

<pre><code class="language-ruby">
def application_up_and_running?
    begin
        RestClient.get 'http://localhost:4567/alive'
        return true
    rescue Exception => e
        $stdout.puts "Error #{e}"
        return false
    end
end

def start_application
    res = system 'sh start_application.sh'    
    attempts_left = 30
    while attempts_left > 0
        up_and_running = application_up_and_running?
        return if up_and_running
        $stdout.puts "Waiting for the application... (attemps left #{attempts_left})"
        sleep(2)
        attempts_left = attempts_left - 1
    end
    stop_application
    raise Exception.new('The application does not respond, giving up')
end

def stop_application
    res = system 'sh stop_application.sh'
end
</code></pre>

We start it by using Maven. `start_application_sh` looks like this:

<pre><code class="language-bash">
mvn -f ../pom.xml exec:java -Dexec.args="--db-port 7500" > log_app_out.txt 2> log_app_err.txt &
echo $! > .saved_pid
</code></pre>

We store the content of the output and error streams on file. However each run of the application override the same files so you may want to improve that. We start the application in background (so that the script can terminate) and save the PID identifying ther process. Later we use that PID to kill the application.

To verify that the application is up and running we try to contact it on a specific route that we defined for this purpose. That route returns just "ok", but we use the fact we are getting an answer to recognize when the application is ready to be used (and tested).

## Examples of tests

##Conclusions

I think that functional tests are important because they provide us the guarantee that the application is doing what is supposed to do.

There is a lot of space for improvements: it is very important to add proper logging functionalities so that when sometimes go wrong we know what's happened and we can fix it rapidly. If our tests fail we want to understand if it indicastes an issue with our application or our testing infrastructure: perhaps someone else is using a certain port or the database was not restarted properly. In such cases we want to find that out without having to tear apart every single piece of our testing infrastructure.

Writing the infrastructure of these tests could take some time (especially to get all the bits right) but the nice thing is that it can be reused across projects.

{% include authorTomassetti.html %}