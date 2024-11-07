package me.perny.hitman.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

suspend fun waitForMultipleCompletableFutures(futures: List<CompletableFuture<*>>) {
    withContext(Dispatchers.IO) {
        val deferredResults = futures.map { future ->
            async {
                future.join()
            }
        }
        deferredResults.awaitAll()
    }
}