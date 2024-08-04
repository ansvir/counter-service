package com.example.plugins

import com.example.model.response.ResponseMessage
import com.example.service.CountersService
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureRouting(service: CountersService) {

    val paramNotFoundMessage = ResponseMessage.of("'counter' parameter was not specified.")
    val counterCreatedMessage = ResponseMessage.of("Counter successfully created.")
    val counterDeletedMessage = ResponseMessage.of("Counter successfully deleted.")

    install(ContentNegotiation) {
        json()
    }

    get("/GetAll") {
        val counters = service.getAll()
        call.defaultTextContentType(ContentType.Application.Json)
        call.respond(counters)
    }

    get("/Get") {
        val counter = call.parameters["counter"]
        if (counter == null) {
            call.respond(BadRequest, paramNotFoundMessage)
            return@get
        }
        try {
            val counterValue = service.read(counter)
            call.respondText(counterValue.toString())
        } catch (ex: NotFoundException) {
            call.respond(NotFound, ResponseMessage.of(ex.message))
        }
    }

    post("/Create") {
        try {
            val counter = call.receive<Map<String, Int>>()
            service.create(counter)
            call.respond(HttpStatusCode.Created, counterCreatedMessage)
        } catch (ex: Exception) {
            call.respond(BadRequest, ResponseMessage.of(ex.message))
        }
    }

    delete("/Delete") {
        val counter = call.parameters["counter"]
        if (counter == null) {
            call.respond(BadRequest, paramNotFoundMessage)
            return@delete
        }
        try {
            service.delete(counter)
        } catch (ex: NotFoundException) {
            call.respond(NotFound, ResponseMessage.of(ex.message))
        }
        call.respond(OK, counterDeletedMessage)
    }

    put("/Increment") {
        val counter = call.parameters["counter"]
        if (counter == null) {
            call.respond(BadRequest, paramNotFoundMessage)
            return@put
        }
        try {
            val newValue = service.increment(counter)
            call.respondText(newValue.toString())
        } catch (ex: NotFoundException) {
            call.respond(NotFound, ResponseMessage.of(ex.message))
        }
    }

}
