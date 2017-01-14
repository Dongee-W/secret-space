package com.houseofcipher.actors

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Props, Terminated}
import com.houseofcipher.actors.InitEncrptionSystem.Passphrase

import scala.io.StdIn

/**
  * Created by summerlight on 1/14/17.
  */
object InitEncrptionSystem {
  case class Passphrase(phrase: String)
}

class InitEncrptionSystem extends Actor with ActorLogging {
  override def preStart() = {
    val ui = context.actorOf(Props[StdInLoop], "ui")
    print("Encryption Passphrase: ")
    val ek = StdIn.readLine()

    ui ! StdInLoop.Continue(Passphrase(ek))
    context.watch(ui)
  }

  override def receive: Receive = {
    case Terminated(ui) => {
      context.stop(self)
      log.info("System ended.")
    }
  }
}
