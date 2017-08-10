package com.houseofcipher.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.houseofcipher.actors.InitEncrptionSystem.Passphrase

import scala.util.{Failure, Success, Try}
import scala.io.StdIn

object StdInLoop {
  case object End
  case class Continue(pp: Passphrase)
}

/**
  * User interface
  */

class StdInLoop extends Actor with ActorLogging {

  def receive = {

    case StdInLoop.Continue(pp) => {
      print("$ ")
      val input = StdIn.readLine()

      val safeInput = Try {
        val action = input.split(" ").head
        val text = input.split(" ")(1)
        (action, text)
      }

      safeInput match {
        case Success(r) => {
          if (r._1 == "encrypt" || r._1 == "en") {
            val encrptor = context.actorOf(Props[EncryptionActor])

            encrptor ! EncryptionActor.EncryptionTask(pp.phrase, r._2)
            self ! StdInLoop.Continue(pp)
          } else if (r._1 == "decrypt" || r._1 == "de") {
            val decryptor = context.actorOf(Props[DecryptionActor])

            decryptor ! DecryptionActor.DecryptionTask(pp.phrase, r._2)
            self ! StdInLoop.Continue(pp)
          } else
            self ! StdInLoop.End
        }

        case Failure(e) => {
          log.error(e, "Invalid Input.")
          self ! StdInLoop.Continue(pp)
        }
      }
    }

    case StdInLoop.End => {
      context.stop(self)
    }
  }
}

