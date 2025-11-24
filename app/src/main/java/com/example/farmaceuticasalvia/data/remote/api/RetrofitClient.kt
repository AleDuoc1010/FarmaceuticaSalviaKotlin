package com.example.farmaceuticasalvia.data.remote.api

import android.content.Context
import com.example.farmaceuticasalvia.data.local.storage.IpStorage
import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun createOkHttpClient(userPreferences: UserPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val token = runBlocking { userPreferences.authToken.first() }

                val requestBuilder = chain.request().newBuilder()
                if(!token.isNullOrEmpty()){
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private val publicOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    fun getExternalApi(context: Context): ExternalApiService {
        return Retrofit.Builder()
            .baseUrl("https://mindicador.cl/")
            .client(publicOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }

    private fun getBaseUrl(context: Context, port: String): String {
        val ipStorage = IpStorage(context)
        val ip = ipStorage.getBaseIp()
        return "http://$ip:$port/"
    }

    fun getUsuariosApi(context: Context): UsuariosApiService {
        val prefs = UserPreferences(context)
        return Retrofit.Builder()
            .baseUrl(getBaseUrl(context, "8081"))
            .client(createOkHttpClient(prefs))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuariosApiService::class.java)
    }

    fun getCatalogoApi(context: Context): CatalogoApiService {
        val prefs = UserPreferences(context)
        return Retrofit.Builder()
            .baseUrl(getBaseUrl(context, "8082"))
            .client(createOkHttpClient(prefs))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogoApiService::class.java)
    }
    fun getPedidosApi(context: Context): PedidosApiService {
        val prefs = UserPreferences(context)
        return Retrofit.Builder()
            .baseUrl(getBaseUrl(context, "8084"))
            .client(createOkHttpClient(prefs))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PedidosApiService::class.java)
    }
}