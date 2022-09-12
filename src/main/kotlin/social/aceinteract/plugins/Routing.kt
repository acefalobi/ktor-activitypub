package social.aceinteract.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
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
        head("/webid.ttl") {
            getWebID()
        }
        get("/webid.ttl") {
            getWebID()
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
}

suspend fun PipelineContext<Unit, ApplicationCall>.getWebID() {
    call.respondText(File("json/webid.ttl").readText(), ContentType.parse("text/turtle"))
}
