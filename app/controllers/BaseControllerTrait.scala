package controllers

import play.api._
import play.api.cache.Cache
import play.api.Play.current  // bring implicit Application into scope; needed for Cache
import play.api.mvc._

trait BaseControllerTrait {

  /**
   * The Cache will contain a map of (uuid -> uid).
   * To retrieve it, (a) get the uuid from the session (as a String), 
   * then (b) get the uid from the cache like this:
   * 
   * val uidOption: Option[Long] = Cache.getAs[Long](uuid)
   * val uid = Cache.getOrElse[Long](uuid){-1}
   * val uid = Cache.getOrElse[Long](uuid)(-1)  // not sure about syntax
   * 
   */
  def putUidInCache(uuid: String, uid: Long) {
      Cache.set(uuid, uid)
  }

  /**
   * Do something to keep the cache alive.
   * In this case I'm just rewriting the uuid/uid pair.
   * I don't know if this is a good idea, or if this code should be somewhere
   * else, like the AuthenticatedAction, but the cache keeps dying on me.
   */
  def keepCacheAlive(uuid: String, uid: Long) {
      putUidInCache(uuid, uid)
  }

  /**
   * Attempt to get the `uid` from the Play cache, based on the 
   * `uuid` in the Play session.
   */
  def getUid(session: Session): Option[Long] = {
      // get the uuid from the session, then get the uid from the cache
      //println("*getting the uid from the session*")
      session.get("uuid") match {
          case None => None
          case Some(uuid) => 
            val uidOption = Cache.getAs[Long](uuid)  // the uuid from the session maps to the uid
            uidOption.foreach(uid => keepCacheAlive(uuid, uid))
            uidOption
      }
  }

  def getUuid = java.util.UUID.randomUUID.toString

}










