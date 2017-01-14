package longevity.persistence.sqlite

import org.sqlite.SQLiteException
import java.util.UUID
import longevity.persistence.PState
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of SQLiteRepo.create */
private[sqlite] trait SQLiteCreate[P] {
  repo: SQLiteRepo[P] =>

  override def create(p: P)(implicit context: ExecutionContext) = Future {
    logger.debug(s"calling SQLiteRepo.create: $p")
    val id = if (hasPrimaryKey) None else Some(SQLiteId[P](UUID.randomUUID))
    val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
    val state = PState(id, rowVersion, p)
    blocking {
      try {
        bindInsertStatement(state).executeUpdate()
      } catch {
        case e: SQLiteException if e.getMessage.contains("UNIQUE constraint failed") =>
          throwDuplicateKeyValException(p, e)
      }
    }
    logger.debug(s"done calling SQLiteRepo.create: $state")
    state
  }

  private def bindInsertStatement(state: PState[P]) = {
    val insertStatement = connection.prepareStatement(insertSql)
    val bindings = updateColumnValues(state, isCreate = true)
    logger.debug(s"invoking SQL: $insertStatement with bindings: $bindings")
    bindings.zipWithIndex.foreach { case (binding, index) =>
      insertStatement.setObject(index + 1, binding)
    }
    insertStatement
  }

  private lazy val insertSql = {
    val names = updateColumnNames(isCreate = true)
    val columns = names.mkString(",\n  ")
    val substitutionPatterns = names.map(c => s":$c").mkString(",\n  ")

    s"""|
    |INSERT INTO $tableName (
    |  $columns
    |) VALUES (
    |  $substitutionPatterns
    |)
    |""".stripMargin
  }

}
