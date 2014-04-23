package controllers

import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import play.api.cache.Cache
import play.api.Play.current  // bring implicit Application into scope; needed for Cache

class AuthenticatedRequest[A](val username: String, request: Request[A]) extends WrappedRequest[A](request)

/**
 * This code assumes that a `uuid` is stored in the Play session.
 * Adapt the code to look for other things as needed (such as a username).
 */
object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {

  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
      request.session.get("uuid") match {
        case None =>
            println("AuthenticatedAction: NOT AUTHENTICATED")
            Future.successful(Forbidden)
        case Some(uuid) =>
            println("AuthenticatedAction: USER IS OKAY")
            block(new AuthenticatedRequest(uuid, request))
      }
  }

}



