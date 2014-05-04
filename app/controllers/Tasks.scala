package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import views._
import models._
import scala.collection.mutable.ArrayBuffer
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json._

object Tasks extends Controller with BaseControllerTrait {

  val taskForm: Form[Task] = Form(
      mapping(
          "projectId" -> longNumber,
          "status" -> nonEmptyText,
          "task" -> nonEmptyText
      )
      ((projectId, status, task) => Task(0, 0, projectId, 0, task, status, null))  // form -> Task
      ((t: Task) => Some((t.projectId, t.status, t.description)))  // Task -> form
  )

  /**
   * 
   * TODO need to limit this by projectId or projectName (TBD on what the Client needs).
   * 
   */
  def list(projectId: Long) = AuthenticatedAction { implicit request =>
//      println(s"*** content-type: ${request.contentType}")
//      println(s"*** headers: ${request.headers}")
//      println(s"*** body: ${request.body}")
//      println(s"*** query string: ${request.rawQueryString}")
      println("ENTERED Tasks:list")
      println(s"projectId = $projectId")
      val uidOption = getUid(session)
      uidOption match {
          case None =>
              println("Tasks::list => None")
              NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
          case Some(uid) =>
              println("Tasks::list => Some")
              val tasks = Task.selectAll(uid, projectId)
              if (tasks.size == 0) {
                  NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Bad ProjectID"))))
              } else {
                  Ok(Json.toJson(Map("success" -> toJson(true), "tasks" -> Json.toJson(tasks))))
              }
      }
  }
  
  private def attemptToGetProjectId(request: controllers.AuthenticatedRequest[play.api.mvc.AnyContent]): Option[Long] = {
      try {
          val results: Seq[String] = request.body.asFormUrlEncoded.get("projectId")
          println("ENTERED attemptToGetProjectId")
          println(results)
          getProjectId(results)
      } catch {
          case e: Exception => None
      }
  }
  
  private def getProjectId(results: Seq[String]): Option[Long] = {
      if (results.size == 1) {
          try {
              Some(results(0).toInt)
          } catch {
              case e: Exception => None 
          }
      } else {
          None
      }
  }

  private def getProjectId(idAsString: String): Option[Long] = {
      try {
          Some(idAsString.toInt)
      } catch {
          case e: Exception => None 
      }
  }

  /**
   * 
   * TODO need the projectId or projectName
   * 
   */
  def add = AuthenticatedAction { implicit request =>
      taskForm.bindFromRequest.fold(
          errors => {
              Ok(Json.toJson(Map(
                  "success" -> toJson(false), 
                  "msg" -> toJson("Sorry, but the form binding failed."))))
          },
          task => {
              val uidOption = getUid(session)
              uidOption match {
                  case None =>
                      NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
                  case Some(uid) =>
                      attemptInsertAndReturnResult(uid, task)
              }
          }
      )
  }

  def updateStatus = AuthenticatedAction { implicit request =>
      taskForm.bindFromRequest.fold(
          errors => {
              Ok(Json.toJson(Map(
                  "success" -> toJson(false), 
                  "msg" -> toJson("Sorry, but the form binding failed."))))
          },
          task => {
              // TODO clean up this code
              val uidOption = getUid(session)
              uidOption match {
                  case None =>
                      NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
                  case Some(uid) =>
                      val taskIdOption = Task.getTaskId(uid, task.projectId, task.description)
                      taskIdOption match {
                          case None =>
                              NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the Task ID."))))
                          case Some(taskId) =>
                              attemptStatusUpdateAndReturnResult(uid, task.projectId, task.id, task.status)
                      }
                      
              }
          }
      )
  }

  /**
   * If the uid is invalid, return an error.
   * If the uid is valid, insert the data and return a success message.
   * If the uid is valid but the insert fails, return an error.
   */
  private def attemptInsertAndReturnResult(uid: Long, task: Task) = {
      val autoIncrementIdOption = Task.insert(uid, task)
      autoIncrementIdOption match {
          case Some(autoIncrementId) =>
              Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Success. Task was added."), "id" -> toJson(autoIncrementId))))
          case None =>
              Ok(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Bummer. INSERT failed."), "id" -> toJson(-1))))
      }
  }

  private def attemptStatusUpdateAndReturnResult(uid: Long, projectId: Long, taskId: Long, status: String) = {
      val numRecsUpdated = Task.updateStatus(uid, projectId, taskId, status)  // should always be 1
      if (numRecsUpdated == 1) {
          Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Success!"))))
      } else {
          Ok(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("SQL INSERT failed"))))
      }
  }


}










