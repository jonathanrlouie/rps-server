package ca.jonrlouie

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

case class AddConnection(connection: ActorRef)

class RPSServer extends Actor {
     
  import Tcp._
  import context.system
     
  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 9001))
  
  val handler = context.actorOf(Props[SimplisticHandler])
  
  def receive = {
    case b @ Bound(localAddress) =>
      // do some logging or setup ...
      println(s"Bound to port: ${localAddress}")
     
    case CommandFailed(_: Bind) => context stop self
     
    case c @ Connected(remote, local) =>
      val connection = sender()
      handler ! AddConnection(connection)
    
    case ConnectionAdded(connection) =>
      connection ! Register(handler)
  }
     
}
