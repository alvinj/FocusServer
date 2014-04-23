//package controllers
//
//import play.api._
//import play.api.mvc._
//import play.api.data._
//import play.api.data.Forms._
//import play.api.data.validation.Constraints._
//import views._
//import models._
//import scala.collection.mutable.ArrayBuffer
//import play.api.libs.json._
//import play.api.libs.json.Json
//import play.api.libs.json.Json._
//
//object Stocks extends Controller with BaseControllerTrait {
//    
//  val stockForm: Form[Stock] = Form(
//    mapping(
//      "symbol" -> nonEmptyText.verifying("Doh - Stock already exists (1)!", Stock.findBySymbol(_) == 0),
//      "companyName" -> nonEmptyText)
//      ((symbol, companyName) => Stock(0, symbol, companyName))
//      ((s: Stock) => Some((s.symbol, s.companyName)))
//  )
//  
//  // needed to return async results
//  import play.api.libs.concurrent.Execution.Implicits.defaultContext
//  
//  /**
//   * Need to return data like this (or change the client):
//   * echo '{ "data": [ {"id": 1, "symbol": "AAPL", "companyName": "Apple"}, {"id": 2, "symbol": "GOOG", "companyName": "Google"}] }'
//   */
//  def list = AuthenticatedAction { implicit request =>
//      val uidOption = getUid(session)
//      uidOption match {
//          case None =>
//              NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
//          case Some(uid) =>
//              val stocks = Stock.getAll(uid)
//              Ok(Json.toJson(stocks))
//      }
//  }
//
//  /**
//   * The Sencha client will send me id, symbol, and companyName in a POST request.
//   * I need to return something like this on success:
//   *     { "success" : true, "msg" : "", "id" : 100 }
//   */
//  def add = AuthenticatedAction { implicit request =>
//      stockForm.bindFromRequest.fold(
//          errors => {
//              Ok(Json.toJson(Map(
//                  "success" -> toJson(false), 
//                  "msg" -> toJson("Form binding failed. Stock is probably already in the database."))))
//          },
//          stock => {
//              val uidOption = getUid(session)
//              println(s"in 'add' action, uidOption = ${uidOption}")
//              uidOption match {
//                  case None =>
//                      NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
//                  case Some(uid) =>
//                      attemptInsertAndReturnResult(uid, stock)
//              }
//          }
//      )
//  }
//  
//  /**
//   * If the uid is invalid, return an error.
//   * If the uid is valid, insert the data and return a success message.
//   * If the uid is valid but the insert fails, return an error.
//   */
//  private def attemptInsertAndReturnResult(uid: Long, stock: Stock) = {
//      val autoIncrementIdOption = Stock.insert(uid, stock)
//      autoIncrementIdOption match {
//          case Some(autoIncrementId) =>
//              Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(autoIncrementId))))
//          case None =>
//             Ok(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("SQL INSERT failed"), "id" -> toJson(-1))))
//      }
//  }
//
//  /**
//   * Delete the stock that has the given id.
//   */
//  def delete(id: Long) = AuthenticatedAction { implicit request =>
//      getUid(session) match {
//          case None =>
//              NotAcceptable(Json.toJson(Map("success" -> toJson(false), "msg" -> toJson("Could not find the UserID"))))
//          case Some(uid) => {
//              val numRowsDeleted = Stock.delete(uid, id)
//              Ok(Json.toJson(Map("success" -> toJson(true), "msg" -> toJson("Stock was deleted"), "id" -> toJson(numRowsDeleted))))
//          }
//      }
//  }
//  
//}
//
//
//
//
//
//
//
//
