package com.example.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessage(val message: String?) {

    companion object {
        fun of(message: String?): ResponseMessage {
            return ResponseMessage(message)
        }
    }

}
