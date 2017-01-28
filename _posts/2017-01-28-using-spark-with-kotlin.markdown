---
layout: post
title: "Using Spark with Kotlin to create a simple CRUD REST API"
author: <a href="https://www.linkedin.com/in/davidaase" target="_blank">David Åse</a>
date: 2017-01-28 11:11:11
comments: true
summary: >
 Learn how to use Kotlin with Spark by creating a simple CRUD REST API.
---

<div class="notification"><em>The source code for this tutorial can be found on <a href="https://github.com/tipsy/spark-kotlin" target="_blank">GitHub</a>.</em></div>

## What You Will Learn

* Setting up Kotlin with Maven
* Creating a Spark/Kotlin CRUD REST API (no database)
* Some neat Kotlin features

The instructions for this tutorial will focus on IntelliJ IDEA, as it's made by JetBains, the same people who make Kotlin. We recommend downloading the free <a href="https://www.jetbrains.com/idea/download" target="_blank">community edition</a> of IDEA while following this tutorial, but there is also Kotlin support in Eclipse.

## Setting up Kotlin with Maven (in IntelliJ IDEA)

 The good people over at <a href="https://www.jetbrains.com/" target="_blank">JetBrains</a> have an up-to-date <a href="https://maven.apache.org/guides/introduction/introduction-to-archetypes.html" target="_blank">archetype</a> for Kotlin. To use it, do as follows:
 
 * `File -> New -> Project`
 * `Maven -> Create from archetype -> org.jetbrains.kotlin:kotlin-archetype-jvm -> Next`
 * Follow the instructions and pick a project name
 * Create `src/main/kotlin/app/Main.kt`
 
 There is no `public static void main(String[] args)` in Kotlin, instead you have a `fun main(args: Array<String>)`.
 
~~~kotlin
 fun main(args: Array<String>) {
    println("Hello, world!")
}
~~~
 
 <div class="comment">You'll have to point to the file (not class) containing this main function (not method) from your pom.xml if you want to build a jar. Doing this is not necessary for this tutorial, but the code on GitHub demonstrates how to do it for those interested.</div>
 

## Using Spark with Kotlin

Since this is just a normal Maven project, we can add Spark as we always do:

~~~markup
<dependency>
    <groupId>com.sparkjava</groupId>
    <artifactId>spark-core</artifactId>
    <version>2.5.4</version>
</dependency>
~~~

And the Spark `Hello World` example in Kotlin becomes:

~~~kotlin
import spark.Spark.*

fun main(args: Array<String>) {
    get("/hello") { req, res -> "Hello World" }
}
~~~

Even smaller than before, and it looks pretty similar to Java8:
<br>
Java8: `get("/path", (req, res) -> { ... });`
<br>
Kotlin: `get("/path") { req, res -> ...}`. 

<p class="comment">The syntax <code>(){}</code> might look a little strange to Java programmers. Kotlin supports <a href="https://kotlinlang.org/docs/reference/lambdas.html#closures" target="_blank">trailing closures</a> and provides <a href="https://kotlinlang.org/docs/reference/grammar.html#semicolons" target="_blank">semicolon inference</a>. Simplified, this means you don't have to wrap closures in parentheses and end every statement with a semicolon.</p>

## Creating a Spark/Kotlin CRUD microservice

### Kotlin data-classes

Kotlin has a really neat feature called <a href="https://kotlinlang.org/docs/reference/data-classes.html" target="_blank">Data classes</a>. To create a data class you just have to write:

~~~kotlin
data class User(val name: String, val email: String, val id: Int);
~~~

... and you're done! If you declare all parameters as `val` you get an immutable class similar to the <a href="https://projectlombok.org/features/Value.html" target="_blank">Lombok @Value</a> annotation, only better.  Regardless of if you use `var` or `val` (or a mix) for your data class, you get toString, hashCode/equals, copying and destructuring included:

~~~kotlin
val alice = User(name = "Alice", email = "alice@alice.kt", id = 0)
val aliceNewEmail = alice.copy(email = "alice@bob.kt") // new object with only email changed

val (name, email) = aliceNewEmail // choose the fields you want
println("$name's new email is $email") // prints "Alice's new email is alice@bob.kt"
~~~

### Initializing some data
Let's initialize our fake user-database with four users:

~~~kotlin
val users = hashMapOf(
        0 to User(name = "Alice", email = "alice@alice.kt", id = 0),
        1 to User(name = "Bob", email = "bob@bob.kt", id = 1),
        2 to User(name = "Carol", email = "carol@carol.kt", id = 2),
        3 to User(name = "Dave", email = "dave@dave.kt", id = 3)
)
~~~

Kotlin has type inference and named paramters (we could have written our arguments in any order). It also has a nice standard library providing map-literal-like functions (so you won't have to include guava in every project).

### Creating a data access object
We need to be able to read out data somehow, so let's set up some basic CRUD functionality, with one added function for finding user by email:

~~~kotlin
class UserDao {

    val users = hashMapOf(
            0 to User(name = "Alice", email = "alice@alice.kt", id = 0),
            1 to User(name = "Bob", email = "bob@bob.kt", id = 1),
            2 to User(name = "Carol", email = "carol@carol.kt", id = 2),
            3 to User(name = "Dave", email = "dave@dave.kt", id = 3)
    )

    var lastId: AtomicInteger = AtomicInteger(users.size - 1)

    fun save(name: String, email: String) {
        val id = lastId.incrementAndGet()
        users.put(id, User(name = name, email = email, id = id))
    }

    fun findById(id: Int): User? {
        return users[id]
    }

    fun findByEmail(email: String): User? {
        return users.values.find { it.email == email }
    }

    fun update(id: Int, name: String, email: String) {
        users.put(id, User(name = name, email = email, id = id))
    }

    fun delete(id: Int) {
        users.remove(id)
    }

}
~~~

The `findByEmail` function shows of some neat features. In addition to the trailing closures that we saw earlier, Kotlin also has a very practical `find` function and a special `it` keyword, which replaces `user -> user` style declarations with just `it` (<a href="https://kotlinlang.org/docs/reference/lambdas.html#it-implicit-name-of-a-single-parameter" target="_blank">docs</a>).
The function also demonstrates that `==` is the structural equality operator for Strings in Kotlin (equvivalent to `.equals()` in Java). If you want to check for referential equality in Kotlin you can use `===`.
Another thing worth noticing is that the find-functions return `User?`, which means the function will return either a `User` or `null`. In Kotlin you have to specify the possibility of a null-return.

`findByEmail()`, Kotlin vs Java:

~~~kotlin

// Kotlin 
fun findByEmail(email: String): User? {
    return users.values.find { it.email == email }
}

// Java
public User findByEmail(String email) {
    return users.values().stream()
            .filter(user -> user.getEmail().equals(email))
            .findFirst()
            .orElse(null);
}
~~~

### Creating the REST API

Kotlin and Spark play very well together (in fact, Kotlin seems to play well with all Java dependencies).
<br>
We can use trailing closures and implicit return values to create extremely clean route declarations: 

~~~kotlin
val userDao = UserDao()

path("/users") {

    get("") { req, res ->
        jacksonObjectMapper().writeValueAsString(userDao.users)
    }

    get("/:id") { req, res ->
        userDao.findById(req.params("id").toInt())
    }

    get("/email/:email") { req, res ->
        userDao.findByEmail(req.params("email"))
    }

    post("/create") { req, res ->
        userDao.save(name = req.qp("name"), email = req.qp("email"))
        res.status(201)
        "ok"
    }

    patch("/update/:id") { req, res ->
        userDao.update(
                id = req.params("id").toInt(),
                name = req.qp("name"),
                email = req.qp("email")
        )
        "ok"
    }

    delete("/delete/:id") { req, res ->
        userDao.delete(req.params("id").toInt())
        "ok"
    }

}

// add "qp()" alias for "queryParams()" on Request object
fun Request.qp(key: String): String = this.queryParams(key)
~~~

In cases where `findById()` / `findByEmail()` returns null, Spark will return a 404 (null routes are by definition 'not found' in Spark). Unlike Java lambda expressions you don't specify return values in Kotlin, the last line of a lambda expression is the return value (Kotlin lambda <a href="https://kotlinlang.org/docs/reference/lambdas.html" target="_blank">docs</a>).

Our app references `req.qp()` a number of times, which is a method that doesn't exist natively on a Spark Request. This works because we have defined an <a href="https://kotlinlang.org/docs/reference/extensions.html" target="_blank">extension function</a> on the Request object (last line).

## Conclusion
I have only worked with Kotlin for a few hours while writing this tutorial, but I'm already a very big fan of the language. Everything just seems to make sense, and the interoperability with Java is great. IntelliJ will also automaticall convert Java code into Kotlin if you paste it into your project. Please clone the repo and give it a try!


<div class="notification"><em>The source code for this tutorial can be found on <a href="https://github.com/tipsy/spark-kotlin" target="_blank">GitHub</a>.</em></div>

[//]: # 
[//]: # Tutorial is offically over, but there are some neat code examples below
[//]: # 

## Addendum - Other great Kotlin features

While playing around with Kotlin I found a lot of great features which I didn't want to incorporate in the main tutorial, in order to keep it short and to the point. Keep reading if you want to learn more about Kotlin.

### Multiline strings and string interpolation

~~~kotlin
get("/multiline-interpolation-example") { req, res ->
    val name = "Alice"
    val email = "alice@alice.kt"
    """
        <h1>Hello $name</h1>
        <p>Your email is $email</p>
    """
}
~~~

### Default arguments
~~~kotlin
fun serverError(code: Int = 500, message: String = "Internal server error"): String {
    return """
        <h1 style="font-family:monospace">$message (Error $code)</h1>
        <p style="font-family:monospace">We're sorry, but our server messed up. Please back and try again.</p>
    """
}

get("/internal-server-error") { req, res ->
    serverError();
}

get("/not-implemented") { req, res ->
    serverError(code = 501, message = "Not implemented");
}
~~~

### When expression
`when` is similar to Java's `switch`, but it returns a value:

~~~kotlin
get("/when-example") { req, res ->
    when (req.queryParams("lang")) {
        "EN" -> "Hello!"
        "FR" -> "Salut!"
        "IT" -> "Ciao!"
        else -> "Sorry, I can't greet you in ${req.queryParams("lang")} yet"
    }
}
~~~

### Companion objects
This last one might be me dragging old Java habits into Kotlin. I'm not sure if this is a good idea at all when writing a Kotlin app, so please let me know in the comments:

~~~kotlin
// Inside fun main():
get("static-controller-example", CompanionController.controllerOne)
get("static-controller-two-example", CompanionController.controllerTwo)

// Inside CompanionController.kt
class CompanionController {
    companion object  {
        val controllerOne = { req: Request, res: Response ->
            "Hello controllerOne!"
        }
        val controllerTwo = { req: Request, res: Response ->
            "Hello controllerTwo!"
        }
    }
}
~~~

This is a technique I use for structuring webapps in Spark when using Java. I might change my approach as I learn more about Kotlin, but I'm happy to see that it works.