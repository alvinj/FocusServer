package models

import play.api.db._
import play.api.Play.current
import anorm.SQL
import anorm.SqlQuery

case class Task (
    var id: Long,
    var userId: Long,
    var projectId: Long,
    var parentId: Long,
    var description: String,
    var status: String,
    var dateCreated: java.util.Date
)

object Task {

  import play.api.Play.current 
  import play.api.db.DB

  /**
   * Returns all the tasks for the given user and project.
   */
  def selectAll(uid: Long, projectId: Long): List[Task] = DB.withConnection { implicit connection => 
      val sqlQuery = SQL("SELECT * FROM tasks WHERE user_id = {uid} AND project_id = {projectId} ORDER BY id ASC")
          .on("uid" -> uid, "projectId" -> projectId)
      sqlQuery().map ( row =>
        Task(
            row[Long]("id"),
            row[Long]("user_id"),
            row[Long]("project_id"),
            row[Long]("parent_id"),
            row[String]("description"),
            row[String]("status"),
            row[java.util.Date]("date_created"))
      ).toList
  }

  /**
   * Insert a Task for the given userId and projectId.
   * Currently assumes this Task does not have a parent, and lets the database
   * insert a default parent_id of 0.
   */
  def insert(userId: Long, task: Task): Option[Long] = {
      DB.withConnection { implicit c =>
          SQL("""
              INSERT INTO tasks (user_id, project_id, description) 
              VALUES ({userId}, {projectId}, {description})
              """
          )
          .on(
              'userId -> userId,
              'projectId -> task.projectId,
              'description -> task.description
          ).executeInsert()
      }
  }

  /**
   * Change the status of the specified taskId.
   * The result of this method call is the number of rows that were updated;
   * this value should be 1 if one record is updated, and 0 if no records were
   * found by the query (meaning that the taskId/userId did not find a record).
   */
  def updateStatus(uid: Long, taskId: Long, status: String): Int = {
      DB.withConnection { implicit c =>
          val nRowsUpdated = SQL(
              """
                  UPDATE tasks SET status = {status} 
                  WHERE id = {taskId} 
                  AND user_id = {userId}
              """)
              .on("status" -> status, "id" -> taskId, "userId" -> uid)
              .executeUpdate()
          nRowsUpdated
      }
  }
  
  
  /**
   * JSON Serializer Code
   * --------------------
   */
  import play.api.libs.json.Json
  import play.api.libs.json._
  import java.text.SimpleDateFormat

  implicit object TaskFormat extends Format[Task] {

      // convert from Task object to JSON (serializing to JSON)
      def writes(task: Task): JsValue = {
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val taskSeq = Seq(
              "id" -> JsNumber(task.id),
              "userId" -> JsNumber(task.userId),
              "projectId" -> JsNumber(task.projectId),
              "parentId" -> JsNumber(task.parentId),
              "description" -> JsString(task.description),
              "status" -> JsString(task.status),
              "dateCreated" -> JsString(sdf.format(task.dateCreated)))
          JsObject(taskSeq)
      }

      // convert from a JSON string to a Task object (de-serializing from JSON)
      def reads(json: JsValue): JsResult[Task] = {
          val id = (json \ "id").as[Long]
          val userId = (json \ "userId").as[Long]
          val projectId = (json \ "projectId").as[Long]
          val parentId = (json \ "parentId").as[Long]
          val description = (json \ "description").as[String]
          val status = (json \ "status").as[String]
          val dateCreated = (json \ "dateCreated").as[java.util.Date]
          JsSuccess(Task(id,userId,projectId,parentId,description,status,dateCreated))
      }
  }
  
}











