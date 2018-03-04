package com.github.brycemcd.reading

import scalikejdbc._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.postgresql.util.PSQLException

class PersistIncomingMessage {
}

object PersistIncomingMessage {
  def SESMessagePersist(msg: SESMessageBody) : Boolean = {
    ConnectionPool.singleton(sys.env("DB_JDBC_URL"), sys.env("DB_USERNAME"), sys.env("DB_PASSWORD"))

    val sesMessage = msg.Message.toString
    val id = msg.MessageId.toString
    val subject = (parse(sesMessage) \\ "subject").values.toString

    // This does properly escape the variables to protect against SQL injection
    using(ConnectionPool.borrow()) { conn: java.sql.Connection =>
      val db: DB = DB(conn)

      db.localTx { implicit session =>
        try {
          sql"""
          INSERT INTO emails_received
          (ses_message_id, mail_subject, ses_message_json) VALUES
          ($id, $subject, $sesMessage::JSONB)
          -- ON CONFLICT DO NOTHING
          """.update.apply()
          true
        } catch {
          case psql : PSQLException =>
            println("I know this error")
            println(psql.getMessage )
            // returns boolean. Duplicate key is safe. Everything else is not
            psql.getMessage.startsWith("ERROR: duplicate key value violates unique constraint")
          case unknown : Throwable =>
            println("Got this unknown exception: " + unknown.getClass)
            false
        }
      }
    }
  }
}