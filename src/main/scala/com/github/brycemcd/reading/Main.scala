package com.github.brycemcd.reading

object Main extends App {

  def saveToDB(msg: SESMessageBody) = {
    println(msg)
    PersistIncomingMessage.SESMessagePersist(msg)
  }

  override def main(args: Array[String]): Unit = {
    IncomingListicleQueueReader.allMessagesOnQ(saveToDB)
  }
}
