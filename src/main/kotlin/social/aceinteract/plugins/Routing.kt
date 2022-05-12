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
        get("/actor") {
            call.respond(File("json/actor.json").readText())
            call.respondText(File("json/actor.json").readText(), ContentType.parse("application/activity+json"))
        }
        get("/.well-known/webfinger") {
            val webfingerResource = call.request.queryParameters["resource"]
            if (webfingerResource.equals("acct:aceinpink@aceinpink.social")) {
                call.respond(File("json/webfinger.json").readText())
            } else {
                call.respond(HttpStatusCode.NotFound, "")
            }
        }
    }
    routing {
    }
}
