package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

object S2Service {

    // Copyright 2023 Ralf Stephan
    private const val TAG = "S2Service"

    object RetrofitHelper {

        private const val baseUrl = "https://api.semanticscholar.org/"
        private var logging = HttpLoggingInterceptor()

        fun getInstance(): Retrofit {
            logging.level = Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            return Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }
    }

    // Max supported fields: "paperId, externalIds, title, abstract, publicationTypes, tldr"
    @Serializable
    data class PaperDetails (
        val paperId: String? = "",
        var externalIds: Map<String, String>? = emptyMap(),
        val title: String? = "",
        val abstract: String? = "",
        var publicationTypes: List<String>? = emptyList(),
        var tldr: Map<String, String>? = emptyMap(),
    )

    interface SinglePaperApi {
        @GET("/graph/v1/paper/{paper_id}")
        suspend fun get(
            @Path("paper_id") paperId: String,
            @Query("fields") fields: String,
        ): Response<PaperDetails>
    }

    interface BulkPaperApi {
        @POST("/graph/v1/paper/batch")
        suspend fun postRequest(
            @Body ids: Map<String, @JvmSuppressWildcards List<Any>>,
            @Query("fields") fields: String,
        ): Response<List<PaperDetails>>
    }

    suspend fun getPaperDetails(paperId: String, fields: String): PaperDetails? {
        val singlePaperApi = RetrofitHelper.getInstance().create(SinglePaperApi::class.java)
        val result = singlePaperApi.get(paperId, fields)
        if (result.isSuccessful) {
            Logger.i(TAG, result.body().toString())
            return result.body()
        }
        Logger.i(TAG, "error code: ${result.code()}, msg: ${result.message()}")
        return null
    }
    @JvmSuppressWildcards
    suspend fun getBulkPaperDetails(ids: List<String>, fields: String): List<PaperDetails?>? {
        val bulkPaperApi = RetrofitHelper.getInstance().create(BulkPaperApi::class.java)
        val map = mapOf("ids" to ids)
        val result = bulkPaperApi.postRequest(map, fields)
        if (result.isSuccessful) {
            Logger.i(TAG, result.body().toString())
            return result.body()
        }
        Logger.i(TAG, "error code: ${result.code()}, msg: ${result.message()}")
        return null
    }

}
