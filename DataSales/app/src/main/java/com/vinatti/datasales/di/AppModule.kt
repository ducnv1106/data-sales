package com.vinatti.datasales.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vinatti.datasales.BuildConfig
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.CallApiService
import com.vinatti.datasales.utils.AppUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val TIMEOUT = 60

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.apply {
            writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            addInterceptor(interceptor)
            addInterceptor(Interceptor { chain ->
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                requestBuilder.header("Accept", "application/json")
                requestBuilder.header("charset", "UTF-8")
                requestBuilder.addHeader("Authorization", "Basic " + "UGF5bmV0T25lOkdhdGV3YXlAMjEyMSFAIw==")
                chain.proceed(requestBuilder.build())
            })
        }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClientBuilder.build())
            .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().serializeNulls()
        .setLenient()
        .setPrettyPrinting()
        .registerTypeAdapterFactory(AppUtils.Companion.NullStringToEmptyAdapterFactory<Any?>()).create()


    @Provides
    fun provideCharacterService(retrofit: Retrofit): CallApiService = retrofit.create(CallApiService::class.java)

//    @Provides
//    fun provideCharacterServiceImage(retrofit: Retrofit): CallApiServiceImage = retrofit.create(CallApiServiceImage::class.java)

    @Provides
    fun provideApiController(apiService: CallApiService): ApiController = ApiController(apiService)



}