package com.example.valguard.app.core.network

import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.io.IOException


suspend inline fun <reified T> safeCall(
    crossinline execute: suspend () -> HttpResponse
): Result<T, DataError.Remote> = withContext(Dispatchers.IO) {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        return@withContext Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: IOException){
        return@withContext Result.Failure(DataError.Remote.NO_INTERNET)
    } catch (e: Exception){
        currentCoroutineContext().ensureActive()
        println("Network error: ${e.message}")
        e.printStackTrace()
        return@withContext Result.Failure(DataError.Remote.UNKNOWN_ERROR)
    }

    return@withContext responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DataError.Remote>{
    return when(response.status.value){
        in 200..299 -> {
            try{
                Result.Success(response.body<T>())
            }
            catch (e: Exception){
                println("Parsing error: ${e.message}")
                e.printStackTrace()
                Result.Failure(DataError.Remote.UNKNOWN_ERROR)
            }
        }
        401 -> {
            println("Unauthorized - check API key")
            Result.Failure(DataError.Remote.UNKNOWN_ERROR)
        }
        408 -> Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Failure(DataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Failure(DataError.Remote.SERVER_ERROR)
        else -> {
            println("HTTP error: ${response.status.value}")
            Result.Failure(DataError.Remote.UNKNOWN_ERROR)
        }
    }
}