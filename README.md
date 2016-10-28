
Cloud Contact - A Web App developed with Sparkjava, mongodb and Thinbus SRP
===========================================================================


Background
==========

Cloud contact is a platform that allows you to save your contacts in the cloud. So that you can access them from anywhere 
through a mobile phone or desktop or even from a cafe.

The Web App uses Sparkjava as a web framework. Sparkjava is a lightweight web framework in java that lets you get up
and running with few lines of code.
I believe you know about sparkjava or you want to know; that's why you are here. So take a quick look at
[sparkjava documentation]() to learn more about how it works in detail. Trust me, those guys value time and so have made their
documentation to be concise.

So, you're back from the [sparkjava]() website and you know what we are talking about. Let's see what it means to develop
web apps with sparkjava (and of course by now, you know that sparkjava is not the same as Apache Spark).


Tools
-----
For this project, I used:
 * [Handlebars] template Engine for java
 * the [thinbus srp protocol] implementation in java for authentication
 * gradle as my build tool
 * NetBeans IDE

Methodology
-----------

This is how I think we should best go about it,
 
* You will start by forking/cloning this project's repo on github ( https://github.com/SeunMatt/cloudcontact.git ) 
    into your NetBeans Project dir or anywhere else you so prefer.
* Then we will walk through the environment setup required, if you don't have it already
* And finally, I will explain in details every aspect of the program and how everything add up to form the cloud contact web app
* Lastly, you will ask questions or practice with your own app or just go grab a cup of coffee

Environment Setup
-----------------

1. First you will need to install and configure gradle on your local machine. So to do that, [here] () is a very short
  tutorial about it. With screenshots all along the way. [Follow this Link to install gradle]()

2. We will be using the NetBeans Gradle Support plugin. Kindly [go here] to do that. It won't take you up to 5 mins.

3. Now that you have setup Gradle on your system as well as install and config the NetBeans Gradle Support Plugin

4. Start NetBeans IDE

5. click File > Open Project. Navigate to the folder where you have cloned the repo and open it. If you have configured the
    Gradle Support plugin correctly, the folder will show up as a Gradle Project.

6. Now that you have opened the project, allow it to build and resolve dependencies.

7. If there are errors involving missing package, ensure you have internet connection and Build the project again
    To Build the project, click on the Hammer icon at the top of the window

8. If all is well, all you need to do is run the program by clicking on the run icon (looks like a play button) and check your
logs. Open your browser, goto localhost:8080; the site should be there shinny!

Now let's proceed to seeing how everything sums up.


EXPLANATION
===========

Creating a web app with sparkjava is very similar to creating apps for desktops with the exception that this one
runs on the web. SparkJava provides static methods that corresponds to HTTP verbs (GET, POST, PUT, DELETE . . .).
When you call this methods with the appropriate parameters they will respond to HTTP requests from the client's browser.

Dependencies
------------

Dependencies are external libraries required by your program to run successfully. 
Now, since we are using Gradle as our build tool, we have to specify all the dependencies that our app requires 
in the build.gradle file which is located at the root of the project's directory.

From the Projects viewer, just expand the Build Scripts folder, then expand the Project folder and you will 
see two files: build.gradle and settings.gradle. 

open the build.gradle and let's have a look at it.

I made it to be comment-rich and try to explain each section and what it does as much as possible

The dependencies section contains the dependencies that we used in this project. 

I commented out the dependency for sparkjava core because as at the time of writing this tutorial,
it contains an error. It doesn't print stack trace for server errors and you know how dare to a programmer's
life stack trace is. Since there exist a pull request already for it, I just cloned the repo and build my own jar
with the proposed changes.

Now the version of the sparkjava core we will be using is in a jar file in our libs folder.
That's why there's this statement in the dependencies section to compile all the jar files in the libs 
folder.

~~~
compile fileTree(include: ['*.jar'], dir: 'libs')
~~~

The libs folder is in the root dir of the project and contains other jar files as well
like jsoup and gson


Another important statement in our build.gradle file is the 
mainClassName="App" declaration. This tells Gradle where our main method is and where to start our application from

N|B: "App" is the name of our main class with no .java extension. 
If your main class is in a package, then you will supply the full package name and not just the class name


Other sections of the build.gradle file are well commented so you can just go through them

Project Structure and Flow
--------------------------

Cloud Contact will allow a user to register, then sign in and then can add, edit, view or delete contacts he has saved.

From NetBeans Project view, expand the Source Pakages[Java] node and you will see all the packages for this project.

The packages are organized based on function. All classes that are related to Contact handling - the Controller and the Model
are all placed in the same package. Same goes for other packages as well.

1. The default package contains the App.java which is the entry point of the app.
2. com.bitbucket.thinbus.srp6.js contains the Classes required for authentication using thinbus implementation of SRP6 Protocol
3. com.smatt.cc.auth contains the classes for required for handling the authentication process.
4. com.smatt.cc.contact contains classes that handle all contacts related works including CRUD operations
5. com.smatt.cc.db contains our Database Helper class which makes connection to the database via morphia
6. com.smatt.cc.index contains the IndexController which deals with request for the HomePage
7. com.smatt.cc.util contains utility classes like Path.java which contains all our constants used throughout the program

Now, let's examined the work flow of the Application based on the features of Cloud Contacts. 
Starting with the Entry point and how it relates to other parts. 

The entry point of the app is App.java located in the default package. It contains our routes handlers that
handles each request from the user via web browser.
If you open the App.java it will contain this section of code. The routes have also been grouped according to functions

~~~java

//		Handle homepage routes
		get(Path.Web.HOME, (req, res) -> IndexController.serveHomePage(req, res), new HandlebarsTemplateEngine());

//		handle auth routes
		get(Path.Web.GET_LOGIN_PAGE, (req, res) -> { return AuthController.serveLoginPage(req, res); }, new HandlebarsTemplateEngine());
		post(Path.Web.DO_LOGIN, (req, res) -> { return AuthController.handleLogin(req, res);} );
                post(Path.Web.DO_AUTH, (req, res) -> {return AuthController.handleAuth(req, res); } );
                get(Path.Web.GET_SIGN_UP, (req, res) -> { return AuthController.serveSignUpPage(req, res); }, new HandlebarsTemplateEngine());
		post(Path.Web.DO_SIGN_UP, (req, res) -> {return AuthController.handleSignUp(req, res);});
                get(Path.Web.LOGOUT, (req, res) -> { return AuthController.handleSignOut(req, res); });
		
		
//		handle CRUD routes
		get(Path.Web.DASHBOARD, (req, res) -> {return ContactController.serveDashboard(req, res);}, new HandlebarsTemplateEngine());
		delete(Path.Web.DELETE, (req, res)-> {return ContactController.handleDeleteContact(req, res);}, new JsonTransformer());
                put(Path.Web.UPDATE, "application/json", (req, res) -> {return ContactController.handleUpdateContact(req, res); });
                post(Path.Web.NEW, "application/json", (req, res) -> { return ContactController.handleNewContact(req, res);} );

~~~

The first routes defined handles a HTTP GET request for our home page. If you goto localhost:8080 this is the route
that will be invoked by sparkjava. 
The IndexController class contains a static method serveHomePage that tells sparkjava to show our home page.
That's pretty straight forward I think.

Now, let's examined the handle CRUD routes
These routes handle requests from the browser to perform certain actions.

 









