---
layout: post
title:  "Testing spark application: how to design testable web applications applications"
author: <a href="http://tomassetti.me" target="_blank">Federico Tomassetti</a>
date:   2015-08-01 10:34:52
summary: >
  Writing tests should simplify the development of web applications and give you the confidence to perform refactoring. However often it becomes a chore. Writing tests is not easy, as it is not easy to understand what to test and how to test it. In this tutorial we describe one possible approach to use a mix of unit and functional tests to keep development simple and agile, while having our back covered by solid tests.
---

##The plan

There are many different forms of tests which can be used to assure different properties of your applications are maintained over time. In this tutorial we focus exclusively on the functional aspects (i.e., we verify that the application does what is supposed to do), while we do not consider the non-functional aspects (i.e., how the application does it, so performance, load handling, etc.).

In particular we are going to write two kinds of tests:
* unit tests, to verify that single classes or methods implement a piece of logic correctly
* functional tests, to ensure that the whole application implements correctly features

We are going to use two different approaches for implementig these tests:
* unit tests will be written in Java using JUnit. We will describe a pattern to make the logic more testable
* functional tests are going to be written using Cucumber. You will have to write some Ruby for that

Ok, now we have to understand when to use one each testing approach. We are going to see that in the next paragraph.

##Logic and plumbing code

In my opinion code can be roughly divided in two parts: the logic and the plumbing. The logic is normally something specific to your application and dealing with the domain. For example calculating since how many days a post was published or if a user has the permission to post a blog in a given section. The plumbing has instead to deal with the technological aspects: verify that a certain header has a valid value, deal with an IO exception and so on. The logic is what you want absolutely to test and it is tipically fairly easy to write unit tests for that part. The plumbing instead is tipically hard to test because it is strongly connected with low level libraries and it tipically require some complex state to be re-created in your test. In addition to the difficulty the benefits are often very low: you are basically testing the library you are using (for example an HTTP library) instead of testing your logic. So I suggest to do two things:

* separate logic and plumbing code
* test logic through unit tests while testing plumbing code through functional tests

We first start with the unit tests, which are probably familiar to more user. Then we will take courage and jump in the land on Cucumber & Ruby to write our functional tests.

##The Handler interface


##Unit tests

##Functional tests

##Conclusions

{% include authorTomassetti.html %}