package com.alphaStore.alphaS3.encryptionmaster

import java.io.*
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


object AESUtil {
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encrypt(algorithm: String?, input: String, key: SecretKey?, iv: IvParameterSpec?): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder()
            .encodeToString(cipherText)
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decrypt(algorithm: String?, cipherText: String?, key: SecretKey?, iv: IvParameterSpec?): String {
        val cipher = Cipher.getInstance(algorithm)

        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(
            Base64.getDecoder()
                .decode(cipherText)
        )
        return String(plainText)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun generateKey(n: Int): SecretKey {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(n)
        return keyGenerator.generateKey()
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getKeyFromPassword(password: String, salt: String): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 65536, 256)
        return SecretKeySpec(
            factory.generateSecret(spec)
                .encoded, "AES"
        )
    }

    fun generateIv(): IvParameterSpec {
        return IvParameterSpec(
            byteArrayOf(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0
            )
        )
    }

    @Throws(
        IOException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encryptFile(
        algorithm: String?, key: SecretKey?, iv: IvParameterSpec?,
        inputFile: File?, outputFile: File?
    ) {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val inputStream = FileInputStream(inputFile)
        val outputStream = FileOutputStream(outputFile)
        val buffer = ByteArray(64)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            val output = cipher.update(buffer, 0, bytesRead)
            if (output != null) {
                outputStream.write(output)
            }
        }
        val outputBytes = cipher.doFinal()
        if (outputBytes != null) {
            outputStream.write(outputBytes)
        }
        inputStream.close()
        outputStream.close()
    }

    @Throws(
        IOException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decryptFile(
        algorithm: String?, key: SecretKey?, iv: IvParameterSpec?,
        encryptedFile: File?, decryptedFile: File?
    ) {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val inputStream = FileInputStream(encryptedFile)
        val outputStream = FileOutputStream(decryptedFile)
        val buffer = ByteArray(64)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            val output = cipher.update(buffer, 0, bytesRead)
            if (output != null) {
                outputStream.write(output)
            }
        }
        val output = cipher.doFinal()
        if (output != null) {
            outputStream.write(output)
        }
        inputStream.close()
        outputStream.close()
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        IOException::class,
        IllegalBlockSizeException::class
    )
    fun encryptObject(
        algorithm: String?, `object`: Serializable?, key: SecretKey?,
        iv: IvParameterSpec?
    ): SealedObject {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        return SealedObject(`object`, cipher)
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        ClassNotFoundException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        IOException::class
    )
    fun decryptObject(
        algorithm: String?, sealedObject: SealedObject, key: SecretKey?,
        iv: IvParameterSpec?
    ): Serializable {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        return sealedObject.getObject(cipher) as Serializable
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun  encryptPasswordBased(plainText: String, key: SecretKey?, iv: IvParameterSpec?): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        return Base64.getEncoder()
            .encodeToString(cipher.doFinal(plainText.toByteArray()))
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decryptPasswordBased(cipherText: String?, key: SecretKey?, iv: IvParameterSpec?): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        return String(
            cipher.doFinal(
                Base64.getDecoder()
                    .decode(cipherText)
            )
        )
    }
}