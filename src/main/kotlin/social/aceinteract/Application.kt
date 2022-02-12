package social.aceinteract

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import social.aceinteract.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", watchPaths = listOf("classes")) {
        configureRouting()
    }.start(wait = true)
}
