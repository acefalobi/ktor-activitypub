package social.aceinteract

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import social.aceinteract.plugins.configureRouting

fun main() {
    val environment = applicationEngineEnvironment {
        connector {
            port = 80
            host = "0.0.0.0"
        }
        watchPaths = listOf("classes")
        module {
            configureRouting()
        }
    }
    embeddedServer(Netty, environment).start(wait = true)
}
