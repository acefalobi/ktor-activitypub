package social.aceinteract

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.cors.routing.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import social.aceinteract.plugins.configureRouting
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*


fun main() {
    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
            host = "0.0.0.0"
        }
        watchPaths = listOf("classes")
        module {
            configureRouting()
            install(CORS) {
                anyHost()
            }
            install(CallLogging)
        }
    }
    embeddedServer(Netty, environment).start(wait = true)
//    makeRequest()
}

fun makeRequest() {
    "application/json; charset=utf-8".toMediaType()

    val message = File("json/message.json").readText()

    val serverTime = getServerTime()

    val messageDigest = MessageDigest.getInstance("SHA-256")
    val digest = String(Base64.getEncoder().encode(messageDigest.digest(message.toByteArray())), Charsets.UTF_8)

    val signature = getSignature(serverTime, digest)

    val client = OkHttpClient()

    val body = message.toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder()
        .url("https://aceinteract.social/inbox")
        .post(body)
        .header("Host", "aceinteract.social")
        .header("Date", serverTime ?: "")
        .header("Digest", "SHA-256=$digest")
        .header("Signature", "keyId=\"https:///aceinpink.social/actor\",headers=\"(request-target) host date digest\",signature=\"$signature\"")
        .build()
    val response = client.newCall(request).execute()
    println(response)
    println(response.body?.string())
}



fun getSignature(serverTime: String?, digest: String): String {
    val signString = "(request-target): post /inbox\nhost: aceinteract.social\ndate: $serverTime\ndigest: SHA-256=$digest"

    val privateKey = getPrivateKeyFromString(File("keys/private-pkcs8.pem").readText())
    val sign = Signature.getInstance("SHA256withRSA")

    sign.initSign(privateKey)
    sign.update(signString.toByteArray(charset("UTF-8")))

    return String(Base64.getEncoder().encode(sign.sign()), Charsets.UTF_8)
}

@Throws(
    SignatureException::class,
    NoSuchAlgorithmException::class,
    UnsupportedEncodingException::class,
    InvalidKeyException::class
)
fun verify(publicKey: PublicKey?, message: String, signature: String): Boolean {
    val sign = Signature.getInstance("SHA256withRSA")
    sign.initVerify(publicKey)
    sign.update(message.toByteArray(charset("UTF-8")))
    return sign.verify(Base64.getDecoder().decode((signature.toByteArray(charset("UTF-8")))))
}


@Throws(IOException::class, GeneralSecurityException::class)
fun getPrivateKeyFromString(key: String): RSAPrivateKey {
    var privateKeyPEM = key
    privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
    privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "")
    privateKeyPEM = privateKeyPEM.replace(Regex("\\s"), "")
    val encoded: ByteArray = Base64.getDecoder().decode(privateKeyPEM)
    val kf = KeyFactory.getInstance("RSA")
    val keySpec = PKCS8EncodedKeySpec(encoded)
    return kf.generatePrivate(keySpec) as RSAPrivateKey
}

@Throws(IOException::class, GeneralSecurityException::class)
fun getPublicKeyFromString(key: String): RSAPublicKey {
    var publicKeyPEM = key
    publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
    publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "")
    publicKeyPEM = publicKeyPEM.replace(Regex("\\s"), "")
    val encoded: ByteArray = Base64.getDecoder().decode(publicKeyPEM)
    val kf = KeyFactory.getInstance("RSA")
    return kf.generatePublic(X509EncodedKeySpec(encoded)) as RSAPublicKey
}

fun getServerTime(): String? {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(
        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US
    )
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return dateFormat.format(calendar.time)
}