package com.houseofcipher.actors

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.xml.bind.DatatypeConverter

import akka.actor.{Actor, ActorLogging}
import com.houseofcipher.actors.DecryptionActor.DecryptionTask

import scala.util.{Failure, Success, Try}

/**
  * Created by summerlight on 1/14/17.
  */
object DecryptionActor {
  case class DecryptionTask(passphrase: String, cipherText: String)
  case object Done
}

class DecryptionActor extends Actor with ActorLogging {

  def receive = {
    case DecryptionActor.DecryptionTask(passphrase, cipherText) => {
      import javax.crypto.Cipher
      import javax.crypto.spec.SecretKeySpec

      val logMsg = s"Decrypting $cipherText ..."
      log.info(logMsg)

      val safeResult = Try {
        val cipherTextByte = DatatypeConverter.parseBase64Binary(cipherText)

        val salt: Array[Byte] = Array(-79, 114, -50, -54, -120, -95, 61, -49,
          -40, -103, 19, -120, -98, -42, 19, 121).map(a => a.toByte)

        val iterations = 2000

        val chars = passphrase.toCharArray
        val spec = new PBEKeySpec(chars, salt, iterations, 128)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key128 = skf.generateSecret(spec).getEncoded

        val aesKey = new SecretKeySpec(key128, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, aesKey)
        val decrypted = cipher.doFinal(cipherTextByte)
        decrypted.map(a => a.toChar).mkString("")
      }

      safeResult match {
        case Success(result) => println("Plain Text: " + result)
        case Failure(e) => log.error(e, s"Cannot Decrypt $cipherText")
      }

      context.stop(self)
    }
  }
}
