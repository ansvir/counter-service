package com.example

import com.example.db.CountersDAO
import com.example.db.CountersTable
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @BeforeTest
    fun init() {
        Database.connect(
            url = "jdbc:h2:mem:counter_db;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
        transaction {
            SchemaUtils.create(CountersTable)
        }
    }

    @AfterTest
    fun tearDown() {
        transaction {
            CountersTable.deleteAll()
        }
    }

    @Test
    fun when_getAll_expect_return3Counters() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        transaction {
            CountersDAO.new("counter1") { value = 0 }
            CountersDAO.new("counter2") { value = 20 }
            CountersDAO.new("counter3") { value = 50 }
        }
        val response = client.get("/GetAll")
        val results = response.body<Map<String, Int>>()

        assertEquals(OK, response.status)

        val expected = mapOf(
            "counter1" to  0,
            "counter2" to 20,
            "counter3" to 50
        )

        assertEquals(expected, results)
    }

    @Test
    fun when_getByCounterName_expect_correctCounterValueReturned() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val newCounter = client.post("/Create") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("counter2" to 20))
        }
        assertEquals(Created, newCounter.status)

        val response = client.get("/Get?counter=counter2")
        assertEquals(OK, response.status)

        val expectedValue = 20
        assertEquals(expectedValue, response.body())
    }

    @Test
    fun when_getByCounterNameParameterMissing_expect_badRequestStatus() = testApplication {
        application {
            module()
        }
        val response = client.get("/Get")
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun when_getNonExistentCounterByName_expect_notFoundStatus() = testApplication {
        application {
            module()
        }
        val response = client.get("/Get?counter=counter4")
        assertEquals(NotFound, response.status)
    }

    @Test
    fun when_createCounter_expect_createdStatus() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/Create") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("counter5" to 100))
        }
        assertEquals(Created, response.status)
    }

    @Test
    fun when_createCounterNoCounterProvided_expect_badRequestStatus() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/Create") {
            contentType(ContentType.Application.Json)
            setBody(mapOf<String, Int>())
        }
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun when_createCounter3CountersProvided_expect_badRequestStatus() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/Create") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "counter1" to  0,
                "counter2" to 20,
                "counter3" to 50
            ))
        }
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun when_deleteCounter_expect_CounterDeleted() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val newCounter = client.post("/Create") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("counter5" to 100))
        }
        assertEquals(Created, newCounter.status)

        val response = client.delete("/Delete?counter=counter5")
        assertEquals(OK, response.status)
    }

    @Test
    fun when_incrementCounter10Times_expect_newCounterValueCorrectlyReturned() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val newCounter = client.post("/Create") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("counter3" to 50))
        }
        assertEquals(Created, newCounter.status)

        coroutineScope {
            (0..9).map {
                client.put("/Increment?counter=counter3")
            }
        }.forEach {
            assertEquals(OK, it.status)
        }

        val expectedValue = 60
        val actualValue = client.get("/Get?counter=counter3")
        assertEquals(OK, actualValue.status)
        assertEquals(expectedValue, actualValue.body())
    }

    @Test
    fun when_incrementNonExistentCounter_expect_notFoundStatus() = testApplication {
        application {
            module()
        }
        val firstIncrementation = client.put("/Increment?counter=counter6")
        assertEquals(NotFound, firstIncrementation.status)
    }

    @Test
    fun when_incrementMissingCounterParameter_expect_badRequestStatus() = testApplication {
        application {
            module()
        }
        val firstIncrementation = client.put("/Increment")
        assertEquals(BadRequest, firstIncrementation.status)
    }
}
