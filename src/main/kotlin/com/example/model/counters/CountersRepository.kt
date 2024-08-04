package com.example.model.counters

interface CountersRepository {

    suspend fun getAll(): Map<String, Int>
    suspend fun read(name: String): Int?
    suspend fun create(counter: Pair<String, Int>): Boolean
    suspend fun delete(name: String): Boolean
    suspend fun increment(name: String): Int?

}