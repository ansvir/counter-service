package com.example.service

import com.example.model.counters.CountersRepository
import io.ktor.server.plugins.*

class CounterServiceImpl(private val countersRepository: CountersRepository) : CountersService {

    /**
     * Retrieves all counters.
     */
    override suspend fun getAll(): Map<String, Int> {
        return countersRepository.getAll()
    }

    /**
     * Retrieves value of counter by his name
     *
     * @throws NotFoundException if counter by name doesn't exist
     */
    override suspend fun read(name: String): Int {
        return countersRepository.read(name)
            ?: throw NotFoundException("Counter was not found by name.")
    }

    /**
     * Creates new counter by first pair in counter.
     *
     * @throws RuntimeException in case counter already exist or counter was not created according to server issues
     * @throws IllegalArgumentException in case counter contains less or more than 1 element.
     */
    override suspend fun create(counter: Map<String, Int>) {
        if (counter.size > 1) {
            throw IllegalArgumentException("Not more than 1 counter can be created.")
        }
        val first = counter.entries.firstOrNull()
            ?: throw IllegalArgumentException("There is no counter provided.")
        if (!countersRepository.create(first.toPair())) {
            throw RuntimeException("Counter already exist or internal server error occurred. Use CounterService#get(String) to check for counter existence.")
        }
    }

    /**
     * Deletes counter by name.
     *
     * @throws NotFoundException when counter was not found by name.
     */
    override suspend fun delete(name: String) {
        if (!countersRepository.delete(name)) {
            throw NotFoundException("Counter was not found by name.")
        }
    }

    /**
     * Increments counter by 1 and returns new counter.
     *
     * @throws NotFoundException in case counter was not found by name.
     */
    override suspend fun increment(name: String): Int {
        return countersRepository.increment(name)
            ?: throw NotFoundException("Counter was not found by name.")
    }

}