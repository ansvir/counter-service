package com.example.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object CountersTable : IdTable<String>("counters") {
    private val name: Column<String> = varchar("name", 255).uniqueIndex()
    val value: Column<Int> = integer("value")

    override val id: Column<EntityID<String>> = name.entityId()
}

class CountersDAO(id: EntityID<String>): Entity<String>(id) {
    companion object: EntityClass<String, CountersDAO>(CountersTable)

    var name by CountersTable.id
    var value by CountersTable.value
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)


fun daoToModel(dao: CountersDAO): Pair<String, Int> = dao.name.value to dao.value