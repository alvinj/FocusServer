Play 2.2 Authentication Example
===============================

This is an example Play Framework 2.2 project. The main
feature of this project is that it shows one way to handle authentication
in an application.

Other features are:

* A basic database with a `users` table
* Code to handle the user login process
* Code to handle authentication within the application, including
  an AuthenticatedAction class
* Code to store the `uid` in the Play cache (I find that I need the
  `uid` to properly handle database code; I don't know that I'm 
  handling it really well right now, but at least I am handling it)

This project assumes that it works as the backend for a typical
"single page JavaScript client" application. I currently use
Sencha Touch and Sencha ExtJS to build those types of applications,
and use Play on the backend.


AuthenticatedAction
-------------------

The Stocks controller class has been completely commented-out, and
shows how to use the AuthenticatedAction in your Play project. It's
actually very simple: Just use AuthenticatedAction instead of Action
whenever you need to make sure a user is logged in when accessing an
Action.

