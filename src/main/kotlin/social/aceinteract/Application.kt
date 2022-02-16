package social.aceinteract

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import social.aceinteract.plugins.configureRouting
import java.io.File
import java.security.KeyStore

fun main() {
    val keyStoreFile = File("build/keystore.jks")
    val environment = applicationEngineEnvironment {
        connector {
            port = 80
            host = "0.0.0.0"
        }
        watchPaths = listOf("classes")
        sslConnector(
            keyStore = KeyStore.getInstance(keyStoreFile, "password".toCharArray()),
            keyAlias = "ktorActivitypubAlias",
            keyStorePassword = { "omolola".toCharArray() },
            privateKeyPassword = { "omolola".toCharArray() }) {
            port = 8443
            keyStorePath = keyStoreFile
        }
        module {
            configureRouting()
        }
    }
    embeddedServer(Netty, environment).start(wait = true)
}
