package io.github.x1111101101.dataset.capture.route

import io.github.x1111101101.dataset.capture.service.CaptureService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

internal fun Routing.routeDatasetApi() {
    val service = CaptureService
    route("api") {
        get("snapshot/{id?}") {
            val id = call.pathParameters["id"]
            if(id == null) {
                val response = service.getSnapshots().joinToString("\n")
                call.respondText(response)
                return@get
            }
            val uuid = UUID.fromString(id)
            val response = service.getSnapshot(uuid)
            if(response == null) {
                call.respond(HttpStatusCode.NoContent)
                return@get
            }
            call.respondText(response)
        }
        get("image/{id}") {
            val id = call.pathParameters["id"]
            if(id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val image = service.getImage(UUID.fromString(id))
            if(image == null) {
                call.respond(HttpStatusCode.NoContent)
                return@get
            }
            call.respondBytes(image)
        }
    }
}