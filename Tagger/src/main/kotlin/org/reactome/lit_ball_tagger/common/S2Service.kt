package org.reactome.lit_ball_tagger.common

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

    // Max supported fields: "paperId, title, abstract, publicationTypes, tldr"

    data class PaperDetails (
        val paperId: String = "",
        val externalIds: Map<String, String> = emptyMap(),
        val title: String = "",
        val abstract: String = "",
        val publicationTypes: List<String> = emptyList(),
        val tldr: Map<String, String> = emptyMap(),
    )

    data class BulkPaperDetails (var list: List<PaperDetails> = emptyList())

    interface SinglePaperApi {
        @GET("/graph/v1/paper/{paper_id}")
        suspend fun get(
            @Path("paper_id") paperId: String,
            @Query("fields") fields: String,
        ): Response<PaperDetails>
    }

    interface BulkPaperApi {
        @FormUrlEncoded
        @POST("/graph/v1/paper/batch")
        suspend fun postRequest(
            @Body ids: Set<String>,
            @Field("fields") fields: String,
        ): Response<BulkPaperDetails>
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

    suspend fun getBulkPaperDetails(ids: Set<String>, fields: String): BulkPaperDetails? {
        val bulkPaperApi = RetrofitHelper.getInstance().create(BulkPaperApi::class.java)
        val result = bulkPaperApi.postRequest(ids, fields)
        if (result.isSuccessful) {
            Logger.i(TAG, result.body().toString())
            return result.body()
        }
        Logger.i(TAG, "error code: ${result.code()}, msg: ${result.message()}")
        return null
    }

}
