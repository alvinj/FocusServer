package models

import play.api.db._
import play.api.Play.current
import anorm.SQL
import anorm.SqlQuery

/**
 * The intent of this trait is to provide Anorm/SQL methods that are
 * common/reusable for other models.
 */
trait SqlTrait {

  /**
   * Runs this query: "delete from $tableName where id = {id}"
   * Returns the number of rows deleted.
   */
  def delete(id: Long, tableName: String): Int = {
      DB.withConnection { implicit c =>
          val query = s"delete from $tableName where id = {id}"
          val nRowsDeleted = SQL(query)
              .on('id -> id)
              .executeUpdate()
          nRowsDeleted
      }
  }

}