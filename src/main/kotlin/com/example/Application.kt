package com.example

import com.example.model.counters.H2CountersRepository
import com.example.plugins.configureDatabases
import com.example.plugins.configureRouting
import com.example.service.CounterServiceImpl
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import java.io.File

fun main() {
    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("customCertificate") {
            password = "certPass"
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")

    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "customCertificate",
            keyStorePassword = { "123456".toCharArray() },
            privateKeyPassword = { "certPass".toCharArray() }) {
            port = 88
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }
    embeddedServer(Netty, environment = environment)
        .start(wait = true)
}

fun Application.module() {
    val countersService = CounterServiceImpl(H2CountersRepository)

    install(Routing) {
        configureRouting(countersService)
    }

    configureDatabases()
}
