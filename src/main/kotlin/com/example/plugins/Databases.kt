package com.example.plugins

import com.example.db.CountersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabases() {
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
