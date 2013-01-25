package controllers

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

object Com extends Controller {
  import actors.Events._
  import actors.Actors._
  import actors.Implicits._

  def event = WebSocket.async[JsValue] { request  =>
    (notifier ? Join(request.id)).map {
      case Connected(enumerator) =>
        val iteratee = Iteratee.foreach[JsValue] { event =>
          println(event)
          // TODO: parse message and send to actor
        }.mapDone { _ =>
          notifier ! Quit(request.id)
        }
        (iteratee, enumerator)
      }
  }
}