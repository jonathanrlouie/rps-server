package ca.jonrlouie

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

case class ConnectionAdded(connection: ActorRef)

case class Player(playerNum: Int, sentAns: Boolean)
case class PlayerAnswer(playerNum: Int, answer: String)

class SimplisticHandler extends Actor {
  val rpsGame = context.actorOf(Props[RPSGame], "rpsGame")
  
  import Tcp._
  
  var connections = Nil: List[ActorRef]
  
  var counter = 0;
  
  var users = Map(): Map[ActorRef, Player]
  
  def receive = {
    case AddConnection(connection) => 
      connections ::= connection
      counter += 1
      users += (connection -> Player(counter, false))
      sender() ! ConnectionAdded(connection)
      println("Connection from a player!")
      connection ! Write(ByteString(s"You are player $counter"))
      if (counter == 2) {
        context become {
          case Received(data) =>
            val playerSent: Player = users.getOrElse(sender, new Player(-1, true))
            if (!playerSent.sentAns) {
              sender ! Write(ByteString("Received your answer."))
              users += (sender -> playerSent.copy(sentAns = true))
              rpsGame ! PlayerAnswer(playerSent.playerNum, data.decodeString("UTF-8"))
            }
          case Winner(winner) =>
            for (c <- connections) {
              users += (c -> users(c).copy(sentAns = false))
              c ! Write(ByteString(winner))
            }
        }
      }
    case Received(data) => 
      sender ! Write(ByteString("Game has not started yet."))
    case PeerClosed => 
      counter -= 1
  }
}