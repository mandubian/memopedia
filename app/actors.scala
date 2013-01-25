package actors

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

import models._

object Actors {
  // TODO: should be in global
  lazy val notifier = Akka.system.actorOf(Props[Events])
}

object Implicits {
  implicit val now = new java.util.Date
  implicit val dateFormat = new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
  implicit val timeout = Timeout(1 second)
}

case class Room(player1: (Long, Concurrent.Channel[JsValue]), player2: (Long, Concurrent.Channel[JsValue]), var scores: (Int, Int), var word: Option[Word] = None) {
  import Formats._
  private var current: Option[Word] = None

  def push(js: JsValue) = for(ch <- Seq(player1._2, player2._2)) ch.push(js)
  def checkAnswer(requestId: Long, ans: String) = {
    println("Checking answer: " + ans)
    if(ans == word.get.trad) {
      if(requestId == player1._1) scores = (scores._1 + 1, scores._2)
      if(requestId == player2._1) scores = (scores._1, scores._2 + 1)
    }
    push(Json.toJson(this))
  }
}
object Room {
  def apply(p1: (Long, Concurrent.Channel[JsValue]), p2: (Long, Concurrent.Channel[JsValue])) = new Room(p1, p2, (0,0))
}

object Formats {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val roomsWrite: Writes[Room] = (
    (__ \ "player1").write(
       (__ \ "id").write[Long] ~ (__ \ "score").write[Int] tupled
    ) ~
    (__ \ "player2").write(
        (__ \ "id").write[Long] ~ (__ \ "score").write[Int] tupled
    )){ room: Room => (room.player1._1 -> room.scores._1, room.player2._1 -> room.scores._2)}

  implicit val wordWrite: Writes[Word] = (
      (__ \ "word").write[String] ~ (__ \ "url").write[String]
    ){ word => word.word -> word.url }

}

class Events extends Actor {
  import Implicits._
  import Formats._
  import Events._

  var pending: Option[(Long, Concurrent.Channel[JsValue])] = None
  val rooms = new scala.collection.mutable.ArrayBuffer[Room]

  def newWord = {
    val words = "ordinateur" :: "carte" :: "avion" :: Nil // :: "bottes" :: "culturisme" :: "pince" :: "loutre" :: "choucroute" :: "abeille" :: "cravate" :: Nil
    Words.findWords(words, "fr", "en").map(_.head)
  }

  def receive = {

    case Join(requestid) => {
      val e = Concurrent.unicast[JsValue]{ c =>
        if(!pending.isDefined) {
          play.Logger.info(s"Adding $requestid to pending list")
          pending = Some(requestid -> c)
        } else {
           play.Logger.info(s"preparing new Room")
           val (id, channel) = pending.get
           pending = None
           val room = Room((id, channel), (requestid, c))

           rooms += room

           play.Logger.info(s"Starting room $room")
           room.push(Json.obj("start" -> room))

           newWord.map { w =>
             room.word = Some(w)
             room.push(Json.toJson(w)) }
        }
      }
      sender ! Connected(e)
    }

    case Answer(requestid, ans) => {
      println("ROOMS: " + rooms)
      rooms
        .find{ r =>
          println(requestid)
          println(r)
          r.player1._1 == requestid || r.player2._1 == requestid
        }
        .map(_.checkAnswer(requestid, ans))
    }

    case Quit(requestid) => {
      //TODO
    }
  }
}

object Events {
  case class Connected(enumerator: Enumerator[JsValue])
  case class Join(requestid: Long)
  case class Quit(requestid: Long)
  case class Answer(requestId: Long, answer: String)
}