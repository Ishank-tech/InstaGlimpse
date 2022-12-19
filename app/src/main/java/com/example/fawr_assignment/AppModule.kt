package com.example.fawr_assignment

import com.example.fawr_assignment.repository.InstagramRepository
import com.example.fawr_assignment.repository.InstagramRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun providesOkHttpClientBuilder(
        interceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(2, TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .addInterceptor {

            var response: Response? = null

            try {
                val request: Request = it.request()
                response = it.proceed(request)
                if (response.code == 200) {
                    try {

                        val jsonObject = JSONObject(response.body?.string()!!)
                        val data = jsonObject.toString()
                        val contentType = response.body?.contentType()
                        val responseBody = data.toResponseBody(contentType)

                        return@addInterceptor response.newBuilder().body(responseBody).build()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
            }
            response!!
        }
        .addInterceptor(interceptor)
        .build()

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(okHttpClient)
    }

    @Singleton
    @Provides
    fun providesApiService(
        retrofitBuilder: Retrofit.Builder
    ): ApiInterface {
        val retrofit = retrofitBuilder.baseUrl("https://www.instagram.com/").build()
        return retrofit.create(ApiInterface::class.java)
    }

    @Singleton
    @Provides
    fun providesCallResultRepository(api: ApiInterface): InstagramRepository =
        InstagramRepositoryImpl(api)

}