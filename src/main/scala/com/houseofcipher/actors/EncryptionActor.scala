package com.houseofcipher.actors

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.xml.bind.DatatypeConverter

import akka.actor.Actor
import akka.actor.ActorLogging

import scala.util.{Failure, Success, Try}

object EncryptionActor {
  case class EncryptionTask(passphrase: String, plainText: String)
  case object Done
}

class EncryptionActor extends Actor with ActorLogging {

  def receive = {
    case EncryptionActor.EncryptionTask(passphrase, plainText) => {
      import javax.crypto.Cipher
      import javax.crypto.spec.SecretKeySpec

      val logMsg = s"Encrypting $plainText ..."
      log.info(logMsg)

      val safeResult = Try {
        /* Advanced feature: Salt */
        //val sr = SecureRandom.getInstance("SHA1PRNG")
        //var salt: Array[Byte] = new Array[Byte](16)
        //sr.nextBytes(salt)
        val salt: Array[Byte] = Array(-79, 114, -50, -54, -120, -95, 61, -49,
          -40, -103, 19, -120, -98, -42, 19, 121).map(a => a.toByte)

        val iterations = 2000

        val chars = passphrase.toCharArray
        val spec = new PBEKeySpec(chars, salt, iterations, 128)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key128 = skf.generateSecret(spec).getEncoded

        val aesKey = new SecretKeySpec(key128, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encrypted = cipher.doFinal(plainText.getBytes())
        DatatypeConverter.printBase64Binary(encrypted)
      }

      safeResult match {
        case Success(result) => println("Cipher Text: " + result)
        case Failure(e) => log.error(e, s"Cannot Encrypt $plainText")
      }

      context.stop(self)
    }
  }


}