package com.example.mibanca.network

import com.google.gson.Gson
import retrofit2.HttpException

data class ApiErrorBody(val error: String, val message: String)

class ApiException(
    val errorCode: String,
    override val message: String,
    val statusCode: Int
): Exception(message)

private val gson = Gson()

suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: HttpException) {
    val raw = e.response()?.errorBody()?.string()
    val apiError = try {
        gson.fromJson(raw, ApiErrorBody::class.java)
    } catch (ex: Exception) {
        ApiErrorBody("unknown_error", e.message ?: "Error desconocido")
    }
    Result.failure(ApiException(apiError.error, apiError.message, e.code()))
} catch (e: Exception) {
    Result.failure(e)
}