package com.hermesagent.app.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(
    @SerializedName("message") val message: String,
    @SerializedName("session_id") val sessionId: String? = null
)

data class ChatResponseChunk(
    @SerializedName("content") val content: String? = null,
    @SerializedName("done") val done: Boolean = false
)

interface HermesApi {
    @POST("chat")
    suspend fun sendMessage(@Body request: ChatRequest): retrofit2.Response<okhttp3.ResponseBody>
}