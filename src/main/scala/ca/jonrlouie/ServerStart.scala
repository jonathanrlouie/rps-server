package ca.jonrlouie

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

object ServerStart extends App {
  val system = ActorSystem("ServerSystem")
  val server = system.actorOf(Props[RPSServer], "server")
}