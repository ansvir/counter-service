package com.example.service

interface CountersService {

    suspend fun getAll(): Map<String, Int>
    suspend fun read(name: String): Int
    suspend fun create(counter: Map<String, Int>)
    suspend fun delete(name: String)
    suspend fun increment(name: String): Int

}