package com.ktxdevelopment.mobiware.hilt


import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.db.MobileDatabase
import com.ktxdevelopment.mobiware.retrofit.RetrofitApi
import com.ktxdevelopment.mobiware.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, MobileDatabase::class.java, Constants.DB_NAME)
            .build()


    @Singleton
    @Provides
    fun provideDao(db: MobileDatabase) = db.getDao()


    @Provides
    fun provideSearchApi(): RetrofitApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL_1)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitApi::class.java)

    @Singleton
    @Provides
    fun provideAuth() = Firebase.auth

    @Singleton
    @Provides
    fun provideFirestore() = FirebaseFirestore.getInstance()
}