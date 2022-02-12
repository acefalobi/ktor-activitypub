package social.aceinteract.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {

    // Starting point for a Ktor app:
    routing {
        get("/") {
            call.respondText(File("./").absolutePath)
        }
        get("/hell") {
            call.respondText("no to the no")
        }
        get("/.well-known/webfinger") {
            val webfingerResource = call.request.queryParameters["resource"]
            if (webfingerResource.equals("acct:aceinpink@aceinpink.social")) {
                call.respond(File("webfinger.json").readText())
            } else {
                call.respond(HttpStatusCode.NotFound, "")
            }
            call.respondText("no to the no")
        }
    }
    routing {
    }
}
