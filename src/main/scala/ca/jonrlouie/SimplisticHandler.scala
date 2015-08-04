package ca.jonrlouie

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

class SimplisticHandler extends Actor {
  import Tcp._
  def receive = {
    case Received(data) => 
      println(data.decodeString("UTF-8"))
      sender() ! Write(data)
    case PeerClosed => context stop self
  }
}