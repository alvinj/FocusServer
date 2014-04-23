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

object Projects extends Controller with BaseControllerTrait {

  val projectForm: Form[Project] = Form(
      mapping(
          "name" -> nonEmptyText
      )
      ((name) => Project(0, 0, name, 0, null))
      ((p: Project) => Some((p.name)))
  )
  
  /**
   * Need to return data like this (or change the client):
   */
  def list = AuthenticatedAction { implicit request =>
      val uidOption = getUid(session)
      uidOption match {
          case None =>
              NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
          case Some(uid) =>
              val projects = Project.selectAll(uid, "a")
              val result = Json.toJson(projects)
              println("--- Projects::list Results ---")
              println(result)
              Ok(result)
      }
  }

  /**
   * I need to return something like this on success:
   *     { "success" : true, "msg" : "success, woo-hoo"}
   */
  def add = AuthenticatedAction { implicit request =>
      projectForm.bindFromRequest.fold(
          errors => {
              Ok(Json.toJson(Map(
                  "success" -> toJson(false), 
                  "msg" -> toJson("Form binding failed. Project is probably already in the database."))))
          },
          project => {
              val uidOption = getUid(session)
              println(s"in 'project add' action, uidOption = ${uidOption}")
              uidOption match {
                  case None =>
                      NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
                  case Some(uid) =>
                      attemptInsertAndReturnResult(uid, project)
              }
          }
      )
  }

  /**
   * If the uid is invalid, return an error.
   * If the uid is valid, insert the data and return a success message.
   * If the uid is valid but the insert fails, return an error.
   */
  private def attemptInsertAndReturnResult(uid: Long, project: Project) = {
      val autoIncrementIdOption = Project.insert(uid, project)
      autoIncrementIdOption match {
          case Some(autoIncrementId) =>
              Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(autoIncrementId))))
          case None =>
             Ok(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("SQL INSERT failed"), "id" -> toJson(-1))))
      }
  }

  /**
   * Delete the project that has the given id.
   */
  def delete(id: Long) = AuthenticatedAction { implicit request =>
      getUid(session) match {
          case None =>
              NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
          case Some(uid) => {
              val numRowsDeleted = Project.delete(uid, id)
              Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Project was deleted"), "id" -> toJson(numRowsDeleted))))
          }
      }
  }
  
  
  
  
}







