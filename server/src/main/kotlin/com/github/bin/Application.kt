package com.github.bin

import com.github.bin.util.jsonGlobal
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.slf4j.event.Level
import java.io.File
import java.time.Duration

/**
 *  @Date:2023/3/26
 *  @author bin
 *  @version 1.0.0
 */
fun Application.module() {
    // install(CORS) {
    //     allowMethod(HttpMethod.Put)
    //     allowMethod(HttpMethod.Delete)
    // }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }
    install(CachingHeaders) {
        options { call, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.Html,
                ContentType.Text.CSS,
                ContentType.Text.JavaScript,
                -> CachingOptions(CacheControl.MaxAge(
                    maxAgeSeconds = 24 * 60 * 60,
                    visibility = CacheControl.Visibility.Public
                ))
                else -> null
            }
        }
    }
    install(PartialContent) {
        // Maximum number of ranges that will be accepted from a HTTP request.
        // If the HTTP request specifies more ranges, they will all be merged into a single range.
        maxRangeCount = 10
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    install(Resources)
    install(DoubleReceive)
    install(Routing)
    install(ContentNegotiation) {
        register(
            ContentType.Application.Json,
            KotlinxSerializationConverter(jsonGlobal)
        )
    }
    install(DataConversion)
    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/")
        }
    }
    install(ShutDownUrl.ApplicationCallPlugin) {
        shutDownUrl = "/ktor/application/shutdown"
        exitCodeSupplier = {
            0
        }
    }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(60)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(jsonGlobal)
    }
    routing {
        static {
            files(File("front/dist"))
            default(File("front/dist/index.html"))
        }
    }
    println("Server created.")
}

fun main() {
    embeddedServer(
        Netty,
        port = 8088,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}
