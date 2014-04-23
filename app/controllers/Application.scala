package controllers

import play.api._
import play.api.cache.Cache
import play.api.Play.current  // bring implicit Application into scope; needed for Cache
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import models.User

/**
 * This object provides basic login/logout behavior.
 * This code is assumed to work with some sort of JSON client; in my
 * current projects I'm using Sencha Touch (and ExtJS), and this server
 * side code works with those clients.
 */
object Application extends Controller with BaseControllerTrait {

  /**
    * the names you use in this mapping ('username', 'password') must match the names that will be
    * POSTed to your methods in JSON
    */
  val loginForm: Form[User] = Form(
      mapping(
          "username" -> nonEmptyText,
          "password" -> nonEmptyText
      )
      ((username, password) => User(0, username, password))  // userForm -> User
      ((u: User) => Some(u.username, u.password))  // User -> UserForm
  )

//  def index = Action {
//      Ok(views.html.index("Hello, world"))
//  }

  /**
   * Note: `login` is a regular Action, not an AuthenticatedAction.
   * This method is intended to work with a JSON client. This code was specifically
   * written for a Sencha ExtJS (JavaScript) client.
   */
  def login = Action { implicit request =>
      println("ENTERED login action")
      loginForm.bindFromRequest.fold(
          errors => {
              // data sent did not validate
              println("login::errors")
              NotFound(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Bad login data"), "id" -> toJson(0))))
          },
          user => {
              println("login - got a valid user")
              val uidOption = User.getUserId(user.username, user.password)
              uidOption match {
                  case None =>
                       println("uidOption match -> None")
                       NotAcceptable(Json.toJson(Map(
                           "success" -> toJson(false), 
                           "msg" -> toJson("Invalid username/password combo"),
                           "id" -> toJson(0))))
                  case Some(uid) =>
                       println("uidOption match -> Some")
                       // TODO i'm jumping thru some hoops here with a uuid because i can't easily get the session token
                       val uuid = getUuid
                       putUidInCache(uuid, uid)
                       Ok(Json.toJson(Map(
                           "success" -> toJson(true), 
                           "msg" -> toJson("Welcome"), 
                           "id" -> toJson(0))))
                           .withSession("username" -> user.username, "authenticated" -> "yes", "uuid" -> uuid)
              }
          }
      )
  }

  /**
   * @see http://www.playframework.com/documentation/2.2.x/ScalaSessionFlash
   * @note `withNewSession` destroys the old session
   * Clears the Cache, and destroys the session (by calling `withNewSession`)
   */
  def logout = Action { implicit request =>
      session.get("uuid") foreach { uuid =>
          Cache.remove(uuid)
      }
      Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("You are logged out")))).withNewSession
  }

}







