package ca.jonrlouie

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

case class ConnectionAdded(connection: ActorRef)

class SimplisticHandler extends Actor {
  import Tcp._
    
  var connections = Nil: List[ActorRef]
  
  def receive = {
    case AddConnection(connection) => 
      connections ::= connection
      sender() ! ConnectionAdded(connection)
    case Received(data) => 
      println(data.decodeString("UTF-8"))
      connections.foreach(_ ! Write(data))
    case PeerClosed => context stop self
  }
}