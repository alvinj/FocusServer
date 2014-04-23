package models

import play.api.db._
import play.api.Play.current
import anorm.SQL
import anorm.SqlQuery

case class Project (
    var id: Long,
    var userId: Long,
    var name: String,
    var displayOrder: Int,
    var dateCreated: java.util.Date
)

object Project {

  import play.api.Play.current 
  import play.api.db.DB

  /**
   * Status is either 'a' (active) or 'r' (retired).
   */
  def selectAll(uid: Long, status: String): List[Project] = DB.withConnection { implicit connection => 
      val sqlQuery = SQL("SELECT * FROM projects WHERE user_id = {uid} and status = {status} ORDER BY date_created ASC")
          .on("uid" -> uid, "status" -> status)
      sqlQuery().map ( row =>
          Project(
              row[Long]("id"),
              row[Long]("user_id"),
              row[String]("name"),
              row[Int]("display_order"),
              row[java.util.Date]("date_created")
          )
      ).toList
  }

  /**
   * Get the id for the given userId and projectName.
   */
  def getProjectId(uid: Long, projectName: String): Option[Long] = DB.withConnection { implicit connection => 
      val rowOption = SQL("SELECT id FROM projects WHERE user_id = {uid} and name = {projectName}")
          .on("uid" -> uid, "projectName" -> projectName)
          .apply
          .headOption
      rowOption match {
          case Some(row) => Some(row[Long]("id"))
          case None => None
      }
  }

  /**
   * Returns the auto-increment id if the SQL INSERT is successful.
   */
  def insert(uid: Long, project: Project): Option[Long] = {
      DB.withConnection { implicit c =>
          SQL("""
              insert into projects (user_id, name, display_order) 
              values (
                  {userId},
                  {name},
                  {displayOrder}
              )
              """
          )
          .on(
              'userId -> uid,
              'name -> project.name,
              'displayOrder -> project.displayOrder
          ).executeInsert()
      }
  }
  
  def delete(uid: Long, id: Long): Int = {
      DB.withConnection { implicit c =>
          val nRowsDeleted = SQL("DELETE FROM projects WHERE id = {id} AND user_id = {userId}")
              .on("id" -> id, "userId" -> uid)
              .executeUpdate()
          nRowsDeleted
      }
  }
  
  /**
   * JSON Serializer Code
   * --------------------
   */
  import play.api.libs.json.Json
  import play.api.libs.json._
  import java.text.SimpleDateFormat

  implicit object ProjectFormat extends Format[Project] {

      // convert from Project object to JSON (serializing to JSON)
      def writes(project: Project): JsValue = {
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val projectSeq = Seq(
              "id" -> JsNumber(project.id),
              "userId" -> JsNumber(project.userId),
              "name" -> JsString(project.name),
              "displayOrder" -> JsNumber(project.displayOrder),
              "dateCreated" -> JsString(sdf.format(project.dateCreated)))
          JsObject(projectSeq)
      }

      // convert from a JSON string to a Project object (de-serializing from JSON)
      def reads(json: JsValue): JsResult[Project] = {
          val id = (json \ "id").as[Long]
          val userId = (json \ "userId").as[Long]
          val name = (json \ "name").as[String]
          val displayOrder = (json \ "displayOrder").as[Int]
          val dateCreated = (json \ "dateCreated").as[java.util.Date]
          JsSuccess(Project(id,userId,name,displayOrder,dateCreated))
      }
  }
  
  
}






