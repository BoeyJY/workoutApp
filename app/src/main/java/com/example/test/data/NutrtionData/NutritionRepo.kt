package com.example.test.data.NutrtionData

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.test.data.Exercise
import com.example.test.data.ExerciseDB
import com.example.test.network.NetworkModule
import com.example.test.network.NutritionNetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NutritionRepo(private val context: Context) {

    private val dbName = "lethio-food-db.sqlite"
    private val dbFile: File get() = context.getDatabasePath(dbName)
    private val mutex = Mutex()

    private val dao = FoodDB.getInstance(context).NutritionDAO()

//    suspend fun ensureReady(onProgress: (Float) -> Unit = {}) = mutex.withLock {
//        if (isPopulated(dbFile)) return@withLock
//
//        val tmp = File(dbFile.parentFile, "$dbName.tmp")
//        dbFile.parentFile?.mkdirs()
//        tmp.delete()
//        try {
//            downloadTo(tmp, onProgress)
//            if (!isPopulated(tmp)) throw IOException("Downloaded DB is empty/corrupt")
//
//            dbFile.delete()               // only after tmp is fully written
//            if (!tmp.renameTo(dbFile)) {
//                tmp.copyTo(dbFile, overwrite = true)
//                tmp.delete()
//            }
//        } catch (t: Throwable) {
//            tmp.delete()
//            throw t
//        }
//    }
//
//    suspend fun isDatabaseReady(): Boolean =
//        withContext(Dispatchers.IO) { isPopulated(dbFile) }

//    private fun isPopulated(file: File): Boolean {
//        if (!file.exists() || file.length() < 4096) return false
//        return runCatching {
//            SQLiteDatabase.openDatabase(
//                file.path, null, SQLiteDatabase.OPEN_READONLY
//            ).use { d ->
//                d.rawQuery(
//                    "SELECT COUNT(*) FROM sqlite_master WHERE type='table' " +
//                            "AND name NOT LIKE 'sqlite_%' AND name != 'android_metadata'",
//                    null
//                ).use { c -> c.moveToFirst() && c.getInt(0) > 0 }
//            }
//        }.getOrDefault(false)
//    }

//    private suspend fun downloadTo(dest: File, onProgress: (Float) -> Unit) =
//        withContext(Dispatchers.IO) {
//            val body = NutritionNetworkModule.api.fetchFood()
//            val total = body.contentLength()
//            body.byteStream().use { input ->
//                FileOutputStream(dest).use { out ->
//                    val buf = ByteArray(64 * 1024)
//                    var read = 0L
//                    while (true) {
//                        val n = input.read(buf)
//                        if (n <= 0) break
//                        out.write(buf, 0, n)
//                        read += n
//                        onProgress(if (total > 0) read.toFloat() / total else -1f)
//                    }
//                    out.fd.sync()          // flush before rename
//                }
//            }
//        }

//    suspend fun refreshFoodFromNetwork() {
//        withContext(Dispatchers.IO) {
////            val existing = dao.getAllFood()
////            if (existing.isNotEmpty()) return@withContext
//
//            try {
//                val body: ResponseBody = NutritionNetworkModule.api.fetchFood()
//                // Define the target file in the app's internal storage
//                val dbFile = File(context.getDatabasePath("lethio-food-db.sqlite").absolutePath)
//
//                // Ensure the parent directories exist
//                dbFile.parentFile?.mkdirs()
//
//                // Write the response body to the file
//                body.byteStream().use { inputStream ->
//                    dbFile.outputStream().use { outputStream ->
//                        inputStream.copyTo(outputStream)
//                    }
//                }
//                dbFile // Return the downloaded file
//
//            } catch (e: Exception) {
//                println("Exception caught in repo")
//                e.printStackTrace()
//            }
//        }
//    }

    fun searchFoodByName(input: String): Flow<List<Food>>{
        return dao.searchFoodByName(input)
    }

    suspend fun getFoodServingSize(foodId: Long): Float{
        return dao.getFoodServingSize(foodId)
    }

}