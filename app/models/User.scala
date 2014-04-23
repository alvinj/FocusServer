package models

/**
 * This case class and the object below are intended to work with the
 * `users` database table defined in the evolutions script.
 */
case class User (
    val id: Long, 
    var username: String,  // "alvin"
    var password: String  // "pass"
)

object User {
  
  import anorm._
  import anorm.SqlParser._
  import play.api.db._
  import play.api.Play.current

  /**
   * This is an Anorm 'parser' that transforms a row to a User value.
   * A 'row' is basically a dictionary, so the strings used here
   * ("id", "name", etc.) need to match the database table column names.
   * @see http://www.playframework.org/documentation/2.0/ScalaAnorm
   */
  val user = {
      get[Long]("id") ~ 
      get[String]("username") ~
      get[String]("password") map {
          case id~username~password => User(id, username, password)
      }
  }

  /**
   * Returns true if one record is found in the users table with the given username and password.
   */
  def userIsInDatabase(username: String, password: String): Boolean = DB.withConnection { implicit c =>
      val numRecs = SQL("select * from users where username = {username} and password = {password}")
          .on('username -> username, 'password -> password)
          .as(user *)
      (numRecs.size == 1)
  }

  /**
   * Returns the `uid` for the given username and password as an Option.
   * If nothing is found, a None is returned.
   */
  def getUserId(username: String, password: String): Option[Long] = DB.withConnection { implicit c =>
      // TODO handle the case where this returns nothing
      val rowOption = SQL("select id from users where username = {username} and password = {password} limit 1")
          .on('username -> username, 'password -> password)
          .apply
          .headOption
      rowOption match {
          case Some(row) => Some(row[Long]("id"))  // users.id is the uid
          case None => None
      }
  }
  

}










