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
object Server {
    @JvmStatic
    fun module(app: Application) {
        // app.install(CORS) {
        //     allowMethod(HttpMethod.Put)
        //     allowMethod(HttpMethod.Delete)
        // }
        app.install(DefaultHeaders) {
            this.header("X-Engine", "Ktor")
        }
        app.install(Compression) {
            this.gzip {
                this.priority = 1.0
            }
            this.deflate {
                this.priority = 10.0
                this.minimumSize(1024) // condition
            }
        }
        app.install(CachingHeaders) {
            this.options { call, outgoingContent ->
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
        app.install(PartialContent) {
            // Maximum number of ranges that will be accepted from a HTTP request.
            // If the HTTP request specifies more ranges, they will all be merged into a single range.
            this.maxRangeCount = 10
        }
        app.install(StatusPages) {
            this.exception<Throwable> { call, cause ->
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
            }
        }
        app.install(Resources)
        app.install(DoubleReceive)
        app.install(Routing)
        app.install(ContentNegotiation) {
            this.register(
                ContentType.Application.Json,
                KotlinxSerializationConverter(jsonGlobal)
            )
        }
        app.install(DataConversion)
        app.install(CallLogging) {
            this.level = Level.INFO
            this.filter { call ->
                call.request.path().startsWith("/")
            }
        }
        app.install(ShutDownUrl.ApplicationCallPlugin) {
            this.shutDownUrl = "/ktor/application/shutdown"
            this.exitCodeSupplier = {
                0
            }
        }
        app.install(WebSockets) {
            this.pingPeriod = Duration.ofSeconds(15)
            this.timeout = Duration.ofSeconds(60)
            this.maxFrameSize = Long.MAX_VALUE
            this.masking = false
            this.contentConverter = KotlinxWebsocketSerializationConverter(jsonGlobal)
        }
        app.routing {
            this.static {
                this.files(File("front/dist"))
                this.default(File("front/dist/index.html"))
            }
        }
        println("Server created.")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(
            Netty,
            port = 8088,
            host = "0.0.0.0",
            module = this::module
        ).start(wait = true)
    }
}
