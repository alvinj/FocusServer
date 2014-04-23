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

  /**
   * 
   * TODO may end up getting the projectName from the Client, not projectId
   * 
   */
  val taskForm: Form[Task] = Form(
      mapping(
          "projectId" -> longNumber,
          "status" -> nonEmptyText,
          "description" -> nonEmptyText
      )
      ((projectId, status, description) => Task(0, 0, projectId, 0, description, status, null))  // form -> Task
      ((t: Task) => Some((t.projectId, t.status, t.description)))  // Task -> form
  )

  /**
   * 
   * TODO need to limit this by projectId or projectName (TBD on what the Client needs).
   * 
   */
  def list(projectId: String, page: String) = AuthenticatedAction { implicit request =>
//      println(s"*** content-type: ${request.contentType}")
//      println(s"*** headers: ${request.headers}")
//      println(s"*** body: ${request.body}")
//      println(s"*** query string: ${request.rawQueryString}")
      val uidOption = getUid(session)
      uidOption match {
          case None =>
              println("Tasks::list, result is None")
              NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
          case Some(uid) =>
              println("Tasks::list, result is Some")
//              attemptToGetProjectId(request) match {
              getProjectId(projectId) match {
                  case None =>
                      println("None: Could not get the projectId")
                      NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Bad ProjectID"))))
                  case Some(pid) =>
                      println(s"projectId = $pid")
//                      val tasks = Task.selectAll(uid, pid)
//                      tasks.foreach(println)  // debug
//                      val result = Json.toJson(tasks)
//                      println(result)
//                      Ok(result)

                        // 'crap' is here to try to get Sencha working
                        // see: http://www.playframework.com/documentation/2.2.x/ScalaJson
                        val json: JsValue = Json.obj(
                            "success" -> true,
                            "tasks" -> Json.arr(
                                Json.obj(
                                    "id" -> JsNumber(1),
                                    "description" -> JsString("Hello, world")
                                )
                              )
                        )
                        println(json)
                        Ok(json)

//                      Ok(Json.obj("id" -> 1, "projectId" -> 1, "description" -> "foo bar baz"))
//                      val output = """[{"id":1, "projectId":1, "description":"Get tabs working"}]"""
//                      Ok(output)
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
                  "msg" -> toJson("Form binding failed. Task is probably already in the database."))))
          },
          task => {
              val uidOption = getUid(session)
              println(s"in 'task add' action, uidOption = ${uidOption}")
              uidOption match {
                  case None =>
                      NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
                  case Some(uid) =>
                      attemptInsertAndReturnResult(uid, task)
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
      //
      // TODO need to use a projectId here
      //
      val autoIncrementIdOption = Task.insert(uid, 0, task)
      autoIncrementIdOption match {
          case Some(autoIncrementId) =>
              Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(autoIncrementId))))
          case None =>
             Ok(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("SQL INSERT failed"), "id" -> toJson(-1))))
      }
  }
  
  /**
   * 
   * TODO need the projectId or projectName
   * 
   */
  def updateStatus = AuthenticatedAction { implicit request =>
      taskForm.bindFromRequest.fold(
          errors => {
              Ok(Json.toJson(Map(
                  "success" -> toJson(false), 
                  "msg" -> toJson("Sorry, but the form binding failed."))))
          },
          task => {
              val uidOption = getUid(session)
              println(s"in 'task add' action, uidOption = ${uidOption}")
              uidOption match {
                  case None =>
                      NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
                  case Some(uid) =>
                      attemptStatusUpdate(uid, task.id, task.status)
              }
          }
      )
  }

  private def attemptStatusUpdate(uid: Long, taskId: Long, status: String) = {
      //
      // TODO need to use a projectId here???
      //
      val numRecsUpdated = Task.updateStatus(uid, taskId, status)  // should always be 1
      if (numRecsUpdated == 1) {
          Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Success!"))))
      } else {
         Ok(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("SQL INSERT failed"))))
      }
  }


}










