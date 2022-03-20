package com.rohitjakhar.hashpass.di

import com.apollographql.apollo.ApolloClient
import com.rohitjakhar.hashpass.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ApolloModel {

    @Provides
    @Named("graphQLClient")
    fun providesOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method, original.body)
                builder.header("X-Hasura-Admin-Secret", BuildConfig.HASURA_KEY)
                chain.proceed(builder.build())
            }
            .build()

    @Provides
    fun provideApolloClient(
        @Named("graphQLClient") okHttpClient: OkHttpClient
    ): ApolloClient =
        ApolloClient.builder()
            .okHttpClient(okHttpClient)
            .serverUrl("")
            .build()
}
