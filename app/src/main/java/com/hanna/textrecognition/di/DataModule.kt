package com.hanna.textrecognition.di

import com.hanna.textrecognition.BuildConfig
import com.hanna.textrecognition.data.core.AppDispatchers
import com.hanna.textrecognition.data.core.AppDispatchersImpl
import com.hanna.textrecognition.data.repository.DistanceRepository
import com.hanna.textrecognition.data.repository.DistanceRepositoryImpl
import com.hanna.textrecognition.data.repository.FirebaseRepository
import com.hanna.textrecognition.data.repository.FirebaseRepositoryImpl
import com.hanna.textrecognition.data.service.FirebaseHelper
import com.hanna.textrecognition.data.service.GoogleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideAppDispatchers(): AppDispatchers {
        return AppDispatchersImpl()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder
            .addInterceptor(loggingInterceptor)
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideGoogleService(okHttpClient: OkHttpClient): GoogleService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GOOGLE_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(GoogleService::class.java)
    }

    @Provides
    @Singleton
    fun provideDistanceRepository(
        googleService: GoogleService
    ): DistanceRepository {
        return DistanceRepositoryImpl(googleService)
    }

    @Provides
    @Singleton
    fun provideFirebaseHelper(): FirebaseHelper {
        return FirebaseHelper()
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firebaseHelper: FirebaseHelper
    ): FirebaseRepository {
        return FirebaseRepositoryImpl(firebaseHelper)
    }
}