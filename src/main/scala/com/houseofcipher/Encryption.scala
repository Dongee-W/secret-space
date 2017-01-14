package com.houseofcipher

import com.houseofcipher.actors.InitEncrptionSystem

/**
  * Personal Encrytion System
  *
  * Actor Graph:
  *
  * InitEncriptionSystem ---> StdInLoop ---> 1. EncryptionActor
  *                                     ---> 2. DecryptionActor
  * 
  */
object Encryption {

  def main(args: Array[String]): Unit = {
    val initialActor = classOf[InitEncrptionSystem].getName

    akka.Main.main(Array(initialActor))
  }

}
