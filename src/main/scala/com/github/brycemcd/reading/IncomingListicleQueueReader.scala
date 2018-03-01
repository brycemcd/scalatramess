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
  implicit val sqs= SQS.at(SQSRegion)
  lazy val msgQ = sqs.queue(SQSMessageQueueName)
  implicit val formats = DefaultFormats

  var i = 0

  // YOU ARE HERE
  // process messages in an infinite loop
  @tailrec
  final def msgsOnQ[T](fx : SESMessageBody => T) : Seq[T] = {
    println("getting message")
    println(SQSMessageQueueName)

    val msgs = msgQ.get.messages.map { msg =>
      fx( parse(msg.body).extract[SESMessageBody] )
    }

    i = i + 1


    if(msgs.isEmpty) Thread.sleep(1000)
    if(i < 6) msgsOnQ(fx) else msgs
  }
}

object IncomingListicleQueueReader {

  def allMessagesOnQ[T](fx: SESMessageBody => T) = {
    val ilqr = new IncomingListicleQueueReader()
    ilqr.msgsOnQ(fx)
  }
}

object Main extends App{

  def log(msg: SESMessageBody) = {
    println(msg)
  }

  override def main(args: Array[String]): Unit = {
    IncomingListicleQueueReader.allMessagesOnQ(log)
  }
}