package com.github.brycemcd.reading
import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class Reading extends ScalatraServlet with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }

  get("/foo") {
    FlowerData.all
  }
}

case class Flower(slug: String, name: String)

object FlowerData {

  /**
    * Some fake flowers data so we can simulate retrievals.
    */
  var all = List(
    Flower("yellow-tulip", "Yellow Tulip"),
    Flower("red-rose", "Red Rose"),
    Flower("black-rose", "Black Rose"))
}
