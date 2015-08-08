package ca.jonrlouie

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

sealed trait RPS {
  def check(rps: RPS): Int
}
case object Rock extends RPS {
  def check(rps: RPS): Int = {
    rps match {
      case Rock => 2
      case Paper => 1
      case Scissors => 0
    }
  }
}
case object Paper extends RPS {
  def check(rps: RPS): Int = {
    rps match {
      case Rock => 0
      case Paper => 2
      case Scissors => 1
    }
  }
}
case object Scissors extends RPS {
  def check(rps: RPS): Int = {
    rps match {
      case Rock => 1
      case Paper => 0
      case Scissors => 2
    }
  }
}

case class Winner(winner: String)

class RPSGame extends Actor {
  
  var numOfAns = 0
  var answers = List(): List[PlayerAnswer]
  
  def receive = {
    case pa @ PlayerAnswer(playerNum, answer) => {
      numOfAns += 1
      answers ::= pa 
      if (numOfAns == 2) {
        val winner = compareAnswers(answers)
        numOfAns = 0
        sender() ! Winner(winner)
      }
    }
  }
  
  def compareAnswers(answers: List[PlayerAnswer]) = {
    val rpsAnswers = answers map ((p: PlayerAnswer) => p.answer) map
      ((s: String) => s match {
        case "rock" => Rock
        case "paper" => Paper
        case "scissors" => Scissors
      })
    val winner = rpsAnswers(0).check(rpsAnswers(1))
    winner match {
      case 0 => s"Player ${answers(0).playerNum} wins!"
      case 1 => s"Player ${answers(1).playerNum} wins!"
      case 2 => "Draw!"
    }
  }
}