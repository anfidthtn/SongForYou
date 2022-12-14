package com.hanyeop.songforyou.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hanyeop.songforyou.api.*
import com.hanyeop.songforyou.utils.BASE_URL
import com.hanyeop.songforyou.utils.KAKAO_BASE_URL
import com.hanyeop.songforyou.utils.WEATHER_BASE_URL
import com.hanyeop.songforyou.utils.XAccessTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {

    // KakaoRetrofit DI
    @Provides
    @Singleton
    @Named("kakaoRetrofit")
    fun provideKakaoRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KAKAO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit DI
    @Provides
    @Singleton
    @Named("retrofit")
    fun provideRetrofitInstance(gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    // WeatherRetrofit DI
    @Provides
    @Singleton
    @Named("weatherRetrofit")
    fun provideWeatherRetrofitInstance(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // OkHttpClient DI
    @Provides
    @Singleton
    fun provideOkHttpClient(xAccessTokenInterceptor: XAccessTokenInterceptor,
                            httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(xAccessTokenInterceptor)
            .build()
    }

    // Gson DI
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    // HttpLoggingInterceptor DI
    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // KakaoApi DI
    @Provides
    @Singleton
    fun provideKakaoApi(@Named("kakaoRetrofit") retrofit: Retrofit): KakaoApi {
        return retrofit.create(KakaoApi::class.java)
    }

    // Oauth2Api DI
    @Provides
    @Singleton
    fun provideOauth2Api(@Named("retrofit") retrofit: Retrofit): Oauth2Api {
        return retrofit.create(Oauth2Api::class.java)
    }

    // IbRecommendApi DI
    @Provides
    @Singleton
    fun provideIbRecommendApi(@Named("retrofit") retrofit: Retrofit): IbRecommendApi {
        return retrofit.create(IbRecommendApi::class.java)
    }

    // UbRecommendApi DI
    @Provides
    @Singleton
    fun provideUbRecommendApi(@Named("retrofit") retrofit: Retrofit): UbRecommendApi {
        return retrofit.create(UbRecommendApi::class.java)
    }

    // SbRecommendApi DI
    @Provides
    @Singleton
    fun provideSbRecommendApi(@Named("retrofit") retrofit: Retrofit): SbRecommendApi {
        return retrofit.create(SbRecommendApi::class.java)
    }


    // SongApi DI
    @Provides
    @Singleton
    fun provideSongApi(@Named("retrofit") retrofit: Retrofit): SongApi {
        return retrofit.create(SongApi::class.java)
    }

    // SongBoxApi DI
    @Provides
    @Singleton
    fun provideSongBoxApi(@Named("retrofit") retrofit: Retrofit): SongBoxApi {
        return retrofit.create(SongBoxApi::class.java)
    }

    // UserApi DI
    @Provides
    @Singleton
    fun UserApi(@Named("retrofit") retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    // WeatherApi DI
    @Provides
    @Singleton
    fun provideWeatherApi(@Named("weatherRetrofit") retrofit : Retrofit) : WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    // ReviewApi DI
    @Provides
    @Singleton
    fun provideReviewApi(@Named("retrofit") retrofit: Retrofit): ReviewApi {
        return retrofit.create(ReviewApi::class.java)
    }

    // MyPageApi DI
    @Provides
    @Singleton
    fun provideMyPageApi(@Named("retrofit") retrofit: Retrofit): MyPageApi {
        return retrofit.create(MyPageApi::class.java)
    }

    // UserStateApi DI
    @Provides
    @Singleton
    fun provideUserStateApi(@Named("retrofit") retrofit: Retrofit): UserStateApi {
        return retrofit.create(UserStateApi::class.java)
    }
}