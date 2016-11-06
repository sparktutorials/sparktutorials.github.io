
Cloud Contact
=============



#A Complete Web App developed with SparkJava, Mongodb and Thinbus SRP Authentication Protocol

Check out the hosted app on heroku [here]( https://cloud-contact.herokuapp.com/ ) 

The complete source code for the tutorial is available on https://github.com/SeunMatt/cloudcontact.git 


#Author
Seun Matt (smatt382@gmail.com)

Published on Nov. 1st, 2016

Background
==========

Cloud contact is a platform that allows you to save your contacts in the cloud. So that you can access them from anywhere 
through a mobile phone or desktop or even from a cafe.

The Web App uses Sparkjava as a web framework. Sparkjava is a lightweight web framework in java that lets you get up
and running with few lines of code.

I believe you know about sparkjava or you want to know; that's why you are here. So take a quick look at
[sparkjava documentation]( http://sparkjava.com/documentation.html ) to learn more about how it works in detail. Trust me, those guys value time and so have made their
documentation to be concise.

So, you're back from the [sparkjava]( http://sparkjava.com/documentation.html ) website and you know what we are talking about. 

Let's see what it means to develop web apps with sparkjava (and of course by now, you know that sparkjava is 

not the same as Apache Spark).

REQUIREMENTS
------------

1. Seriously, I will advise that you go through a very basic and simple [sparkjava tutorial first]( https://sparktutorials.github.io/2015/04/02/hello-tutorials.html ). 
    So that you will understand the basics of how sparkjava handle routes, templates, filters, response and requests and so on.
    But if you are already familiar with Java SE, Web Application Development and have seen basic examples of sparkjava tutorials
    before then you can forge ahead with this one.

2. basic knowledge of jQuery and JavaScript

3. basic knowledge of [Mongodb for java]( https://www.tutorialspoint.com/mongodb/mongodb_java.htm ) 
    and it's java ORM - [Morphia]( http://mongodb.github.io/morphia/1.2/getting-started/ )

4. Kindly read also about thinbus SRP Protocol [here]( http://connect2id.com/products/nimbus-srp/usage )
    and [here as well]( https://bitbucket.org/simon_massey/thinbus-srp-js )

    Then What on earth is this? Is n't this suppose to teach that?

Well, am sorry. This is not a basic tutorial. It is more of a holistic tutorial that 
articulates major aspects of Web App development in java using sparkjava framework        



Tools
-----
For this project, I used:

 * [Handlebars]( https://github.com/perwendel/spark-template-engines/tree/master/spark-template-handlebars ) template Engine for java

 * the thinbus srp protocol implementation in java for authentication
 
 * gradle as my build tool

 * NetBeans IDE

Methodology
-----------

This is how I think we should best go about it:
 
* You will start by forking/cloning this project's [repo on github]( https://github.com/SeunMatt/cloudcontact.git ) 
    https://github.com/SeunMatt/cloudcontact.git  into your NetBeans Project dir or anywhere else you so prefer.

* Then we will walk through the environment setup required, if you don't have it already

* And finally, I will explain in details every aspect of the program and how everything add up to form the cloud contact web app

* Lastly, you will ask questions or practice with your own app or just go grab a cup of coffee

Environment Setup
-----------------

1. First you will need to [setup and install gradle on your local machine]( https://examples.javacodegeeks.com/core-java/gradle/gradle-hello-world-tutorial/ ). 

2. We will be using the NetBeans Gradle Support plugin. Kindly [go here]( https://examples.javacodegeeks.com/core-java/gradle/gradle-netbeans-example/ ) to do that. It won't take you up to 5 mins.

3. Now that you have setup Gradle on your system as well as install and config the NetBeans Gradle Support Plugin

4. Start NetBeans IDE

5. click File > Open Project. Navigate to the folder where you have cloned the [repo]( https://github.com/SeunMatt/cloudcontact.git ) and open it. If you have configured the
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
runs on the web. SparkJava provide methods that correspond to HTTP verbs (GET, POST, PUT, DELETE . . .).
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
it contains an error. It doesn't print stack trace for server errors and you know how dear to a programmer's
life stack trace is. Since there exist a pull request already for it, I just cloned the repo and build my own jar
with the proposed changes.

Now the version of the sparkjava core we will be using is in a jar file in our libs folder.
That's why there's this statement in the dependencies section to compile all the jar files in the libs 
folder.

~~~ Java

    compile fileTree(include: ['*.jar'], dir: 'libs')

~~~

The libs folder is in the root dir of the project and contains other jar files as well
like jsoup and gson


Another important statement in our build.gradle file is the 

mainClassName="App" declaration. This tells Gradle where our main method is and where to start our application from

N|B: "App" is the name of our main class with no .java extension. 

If your main class is in a package, then you should supply the full package name and not just the class name


Other sections of the build.gradle file are well commented so you can just go through them

Project Structure and Flow
--------------------------

Cloud Contact will allow a user to register, then sign in and then can add, edit, view or delete contacts he has saved.

From NetBeans Project view, expand the Source Pakages[Java] node and you will see all the packages for this project.

The packages are organized based on function. All classes that are related to Contact handling - 

the Controller and the Model are all placed in the same package. Same goes for other packages as well.

1. The default package contains the App.java which is the entry point of the app.
2. com.bitbucket.thinbus.srp6.js contains the Classes required for authentication using thinbus implementation of SRP6 Protocol
3. com.smatt.cc.auth contains the classes for required for handling the authentication process.
4. com.smatt.cc.contact contains classes that handle all contacts related works including CRUD operations
5. com.smatt.cc.db contains our Database Helper class which makes connection to the database via morphia
6. com.smatt.cc.index contains the IndexController which deals with request for the HomePage
7. com.smatt.cc.util contains utility classes like Path.java which contains all our constants used throughout the program

Now, let's examine the work flow of the Application based on the features of Cloud Contacts. 

Starting with the Entry point and how it relates to other parts. 

The entry point of the app is App.java located in the default package. It contains our routes handlers that

handle each request from the user via web browser.

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



HOMEPAGE
--------

The first routes defined handles a HTTP GET request for our home page. 

If you goto localhost:8080 from your browser, this is the route that will be invoked by sparkjava.
 
The IndexController class contains a static method serveHomePage that returns the requested home page.

That's pretty straight forward I think.

LOGIN
-----

The authentication protocol used in this project is the SRP Protocol. The SRP Protocol does not send the 

actual password; rather it uses it to generate a salt and verifier. It is the salt and verifier that is then

sent to the server and is stored alongside the username and other user details. 

This permits the use of username/password authentication over un-encrypted

channels like http (NOTE: The security of this protocol can be boosted by using it over https too).

There are several implementations of the SRP Protocol. But I found thinbus' java implementation suitable for my use.

Goto the links provided in the requirements section to know more in detail about

the operation-flow of thinbus SRP implementation. 

If you have any questions along the line, feel free to ask or create issues.

When the user supplies his/her login credentials, the following sequence of events take place:

1. The email will be sent over to the server
2. The server will use the email to fetch the saved salt and verifier of the user
3. The server will use the salt and verifier to generate a server challenge 
4. The salt and the server challenge is sent back to the client
5. The client will use the salt and server challenge to generate credentials, which is sent back to the server
6. The server will then use the received credentials to generate server evidence message, if this is successful
 without errors and exceptions thrown, then the user is authenticated.

the route below, in App.java, deals with step 1 - 4
    
    post(Path.Web.DO_LOGIN, (req, res) -> { return AuthController.handleLogin(req, res);} );
    
while, this one deals with step 5 - 6

    post(Path.Web.DO_AUTH, (req, res) -> {return AuthController.handleAuth(req, res); } );
                
After a successful login, the username, email and userId is stored in the session for use later 

SIGN UP
-------

The signup process involves the user supplying username, email and password 

(and any other user details you wish to have).

1. The Javascript library of thinbus SRP is used to generate a salt
2. The salt, email and password is then used to generate a verifier.
3. The verifier, salt, username and email are then sent to the server. NOTE: It is a crime to send the password! SO DON'T
4. The server receives the data in json format and use them to create a new user object that is stored in the database.
5. If the operation is successful or not, the client will display appropriate message as designed.

The path that handles the signup process is 

    post(Path.Web.DO_SIGN_UP, (req, res) -> {return AuthController.handleSignUp(req, res);});



FILTERS
-------

This portion of the App.java class

~~~Java

     //ensure user is logged in to have access to protected routes
                before("/*/", (req, res) -> {
                    Session session = req.session(true);
                    boolean auth = session.attribute(Path.Web.AUTH_STATUS) != null  ? 
                                    session.attribute(Path.Web.AUTH_STATUS) : false;
                    logger.info("auth status = " + auth);
                    if(!auth) {
                        logger.warn("Secured Area! Login is REQUIRED");
                        res.redirect(Path.Web.GET_LOGIN_PAGE);
                       halt(401);
                    }
                });
~~~

ensures that a user is logged in before accessing every paths that match the pattern "/*/". 

This patterns denotes our protected, logged-in-users only area of the site.

Attempting to access the urls will lead to redirection to the login page.

  
DASHBOARD
---------

When the user has logged in from the login page, 

the next page that will be served is the dashboard. 

The route that handles that is the 

    get(Path.Web.DASHBOARD, (req, res) -> {return ContactController.serveDashboard(req, res);}, new HandlebarsTemplateEngine());

(remember it is in the App.java class.) 

Now the serveDashboard method will be invoked from the ContactController class. The method will essentially fetch the 

userId, username and email from the current user's session. 

Remember, they have been placed there when the user logged in.

The serveDashboard method will add the username and email to the Model for use by the HandlebarsTemplateEngine.

The userId is passed to the prepareData method that will fetch all contacts saved in the database 

where userId equals the one supplied as parameter. 

The data returned from the query will be used to construct part of html table body and then added to the Model too.

Finally, the model is used to construct a ModelAndView object that is then returned. 

It is this ModelAndView object that the Template Engine will use to render our final DASHBOARD Page.

When it is rendered, you can see the username is used as part in the greeting and 

alongside with the email, is available when you profile is clicked.

Now, from the dashboard, the user can Add, Edit, View and Delete Contacts. 

Let's see how those operations are being handled by the app.


VIEW CONTACT
------------

When the dashboard is displayed on a desktop, there are two panels created - one containing a table and the

other containing the details of the currently selected contact.

Every table row has a view icon, which when pressed, load the full details of the contact on that row

into the contact details panel. The contact details panel will flash red to call attention to data changing.

On mobile, the Contact details is not visible; when the view button is clicked, the details are shown in a 

dialog modal that allows the user to view/edit the full details of the selected contact.

The viewing ability is handled entirely by javascript so no call is being made to the server.

Note that the table can also be sort and filtered


ADD NEW CONTACT
---------------

When the user click on NEW button, a dialog will show up with the fields to be filled by the user. 

The user must at least fill the First Name and Mobile number before a contact can be saved.

When the user click on SAVE button, using jQuery, an ajax 

request is sent to the url "/contact/" using http POST method with the new contact data organized in json format.

If the operation is successful the user will be alerted and if not, the user is also alerted.

The route that handles that request is 

    post(Path.Web.NEW, "application/json", (req, res) -> { return ContactController.handleNewContact(req, res);} );

the route is expecting a json formatted data.

When the route is called, it will invoke the ContactController.handleNewContact method that will 

perform the following operations

* It will first parse the raw data using Jsoup to remove any malicious html/script codes injected, it will then
    escape the resulting data to remove every character not defined in Path.Web.OK_PATTERN.

* It will use Jackson to map the data to a new Contact object.

* It will then get the userId from the session and then set it to the Contact object derived from mapping above

* It will then save the Contact object using morphia - which is our java mongodb ORM

* If all goes well, it will return http status code of 200 else it will return 500. 

* From the client's browser, we have implemented the success and error callback to handle each results accordingly

* Feel free to check out the javascript codes in the template files. Some methods are placed in js/utility.js file


UPDATE CONTACT
--------------

When the user click on the EDIT button from the browser, a dialog will appear, with the current values of the 
selected contact.

After the changes has been made by the user, jQuery's $.ajax is used to send a request to the server using http PUT method.

The data to be sent are also formatted in json  and sent to this url "/contact/id" where id is the id of the edited contact

The route that handles that is the 

      put(Path.Web.UPDATE, "application/json", (req, res) -> {return ContactController.handleUpdateContact(req, res); });

This route will call ContactController.handleUpdateContact to process the request.

The method then do the following:

* parse the raw data input like before and escape malicious codes and unwanted characters

* Map the escaped data to Contact object using Jackson

* get the userId from the session and set it to the mapped Contact object

* set the updatedAt property of the Contact object to the current time

*  set the Id of the Contact object and save it using morphia.

* If everything goes well, http status code 200 is returned else, 500 is returned.

* The front-end implements the success and error callback to handle each results accordingly.
  

DELETE CONTACT
--------------

Present on every table row is a delete button, that when pressed will show a confirmation dialog. If the user 

confirms the action then an ajax request will be sent to the server using via http DELETE method.

The route that handles the request in the App.java class is

    delete(Path.Web.DELETE, (req, res)-> {return ContactController.handleDeleteContact(req, res);}, new JsonTransformer());

When the request hits the route, the ContactController.handleDeleteContact method is called. The method does the following:

* It fetches the id of the contact to be deleted from the request param and escape it (parse it using Jsoup)

* It also get the userId from the user's session

* It then create a query using the id and userId fields and then delete the matched contact.

* If everything goes well then a http status code of 200 is returned else 500 is returned.

* The front end that initiates the ajax request also implements success and error callbacks that is invoked accordingly


DEPLOYING TO HEROKU PLATFORM
============================

1. [Create a free heroku account]( https://signup.heroku.com/ )

2. We need to place a Procfile file at the root dir of the project. Note that the name of 

    the file has no extension. It's just Procfile

3. The Procfile contains a single line of instruction telling Heroku it's a web app we are launching.
        
        web: ./build/install/cloudcontact/bin/cloudcontact

4. After this, you will need to [download heroku cli toolbelt]( https://devcenter.heroku.com/articles/heroku-command-line ) which will be used to 
   communicate with heroku via cmd.

5. Goto your dashboard on heroku and create a new project.

6. Add the mLab add on. This will create a free sandbox instance of mongodb that our app can use

7. The mLab add on will add a MONGODB_URI to the env variable that our app can use to connect to the mongodb 
    instance running on mLab

8. The initDatastore method in DatabaseHelper class first tries to use that env variable to connect to the mongodb
    when it has been deployed to heroku. But if the env variable is null, it will fall back to localhost and port.

9. You might want to add add ons like Papertrail for logging

10. Then after you created your account on heroku, downloaded the heroku toolbelt cli,
    created the project on heroku, and added the required mLab add on. 

11. Open your cmd and now cd into this project's root dir on your local system

12. run the following commands to deploy the app to heroku
    
        heroku git:remote -a your-heroku-project-name

        git push heroku master

13. You will log outputs in your cmd. When the process is complete successfully, you will see a BUILD SUCCESSFUL 
    message on the screen. Now you can goto http://your-heroku-project-name.herokuapp.com and your site will be live there

14. If you encounter error, you can simply run the command
        
        heroku logs
   
    or login to your heroku account, goto your app's dashboard. By the top-right you will see a more button click on
    it and you will see open logs option which you can use to view your logs.
    
    If you have added the Papertrail Add-on, you can just click on it from your app's overview and it will
    load all the logs in another tab for you. it's quite easier than the default log viewer
    



CONCLUSION
==========

This tutorial is designed to be an eye opener as to how several components can be brought together to form a 

functional real world web app. And at its core is the lightweight sparkjava web framework, 

mongodb and thinbus SRP authentication protocol.

This is a long tutorial, longer and complex than what I have in mind

when I started out. I hope I have been able to explain it to your understanding a bit. 

if you are seemingly not getting it the first time, try reading it again 

and checking the source code as you do so.

Fork and [clone the repo]( https://github.com/SeunMatt/cloudcontact.git ). Feel free to contribute and ask questions.

#TODO

Some aspects of the app are not implemented yet. 
 
* Forgot Password
* Uploading of contact Photo
* Updating of user's profile

These are left as features to be implemented in the future. You can start from there you know #contribution

Happy Coding, Happy Sparking!