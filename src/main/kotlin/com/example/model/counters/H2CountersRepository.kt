package com.example.model.counters

import com.example.db.CountersDAO
import com.example.db.CountersTable
import com.example.db.daoToModel
import com.example.db.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

object H2CountersRepository: CountersRepository {

    override suspend fun getAll(): Map<String, Int> = suspendTransaction {
        CountersDAO.all().associateTo(HashMap(), ::daoToModel)
    }

    override suspend fun read(name: String): Int? = suspendTransaction {
        CountersDAO
            .find { CountersTable.id eq name }
            .limit(1)
            .map { daoToModel(it).second }
            .firstOrNull()
    }

    override suspend fun create(counter: Pair<String, Int>): Boolean = suspendTransaction {
        val newCounter = CountersDAO.new(counter.first) {
            value = counter.second
        }
        newCounter.name.value == counter.first && newCounter.value == counter.second
    }

    override suspend fun delete(name: String): Boolean = suspendTransaction {
        val rowsDeleted = CountersTable.deleteWhere { id eq name }
        rowsDeleted == 1
    }

    override suspend fun increment(name: String): Int? = suspendTransaction {
        val counter = CountersTable.select { CountersTable.id eq name }.firstOrNull()
        if (counter != null) {
            val newValue = counter[CountersTable.value] + 1
            CountersTable.update({ CountersTable.id eq name }) {
                it[value] = newValue
            }
            newValue
        } else {
            null
        }
    }
}