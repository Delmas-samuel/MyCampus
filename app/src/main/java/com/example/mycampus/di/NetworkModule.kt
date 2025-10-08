// CHEMIN : app/src/main/java/com/example/mycampus/di/NetworkModule.kt
package com.example.mycampus.di

import com.example.mycampus.data.remote.NominatimApi
import com.example.mycampus.data.remote.NewsApi
import com.example.mycampus.data.remote.RoutingApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // OkHttp commun
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request()
                println("🌐 Requête API: ${request.url}")
                try {
                    val response = chain.proceed(request)
                    println("✅ Réponse: ${response.code} - ${request.url}")
                    response
                } catch (e: Exception) {
                    println("❌ Erreur réseau: ${e.message}")
                    throw e
                }
            }
            .build()
    }

    // Moshi avec KotlinJsonAdapterFactory
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())  // ← AJOUTEZ CETTE LIGNE
        .build()

    // Retrofit pour l'API interne News
    @Provides
    @Singleton
    @Named("newsRetrofit")
    fun provideNewsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.233.17.68:3000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(@Named("newsRetrofit") retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    // Retrofit pour Nominatim (User-Agent requis)
    @Provides
    @Singleton
    @Named("nominatimRetrofit")
    fun provideNominatimRetrofit(
        okHttp: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        val clientWithUA = okHttp.newBuilder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", "mycampus-app/1.0 (contact: support@mycampus.example)")
                    .build()
                chain.proceed(req)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(clientWithUA)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideNominatimApi(@Named("nominatimRetrofit") retrofit: Retrofit): NominatimApi {
        return retrofit.create(NominatimApi::class.java)
    }

    // Retrofit pour OpenRouteService
    @Provides
    @Singleton
    @Named("orsRetrofit")
    fun provideOrsRetrofit(
        okHttp: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideRoutingApi(@Named("orsRetrofit") retrofit: Retrofit): RoutingApi {
        return retrofit.create(RoutingApi::class.java)
    }
}
