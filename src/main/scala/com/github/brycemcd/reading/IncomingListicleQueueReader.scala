package com.github.brycemcd.reading

import awscala._
import sqs._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.annotation.tailrec

// A data object for messages coming from email messages
case class SESMessageBody(MessageId: String,
                          Type: String,
                          TopicArn: String,
                          Subject: String,
                          Message: String,
                          Timestamp: String,
                          SignatureVersion: String,
                          Signature: String,
                          SigningCertURL: String,
                          UnsubscribeURL: String)

// NOTE: if the environment variable is not set, this class will explode
class IncomingListicleQueueReader(val SQSRegion: Region = Region.Oregon,
                                  val SQSMessageQueueName : String = sys.env("SQSMessageQueueName")) {
  private implicit val sqs: SQS = SQS.at(SQSRegion)
  private lazy val msgQ: Option[Queue] = sqs.queue(SQSMessageQueueName)
  private implicit val formats: DefaultFormats.type = DefaultFormats

  var i = 0

  @tailrec
  final def msgsOnQ(fx : SESMessageBody => Boolean) : Boolean = {
    print(".")
    val msgs = msgQ.get.messages.map { msg =>
      if(fx(parse(msg.body).extract[SESMessageBody])) deleteMsg(msg)
      msg
    }

    i = i + 1

    if(msgs.isEmpty) Thread.sleep(30000)
    // NOTE: this makes it an infinite loop
    if(i != -1) msgsOnQ(fx) else true
  }

  private def deleteMsg(msg: awscala.sqs.Message): Unit = msg.destroy()
}

object IncomingListicleQueueReader {

  def allMessagesOnQ(fx: SESMessageBody => Boolean): Boolean = {
    val ilqr = new IncomingListicleQueueReader()
    println(s"connecting to ${sys.env("SQSMessageQueueName")} queue and checking messages")
    ilqr.msgsOnQ(fx)
  }
}

