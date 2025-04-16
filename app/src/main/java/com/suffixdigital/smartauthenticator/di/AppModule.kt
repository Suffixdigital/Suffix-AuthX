package com.suffixdigital.smartauthenticator.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.suffixdigital.smartauthenticator.data.repository.AuthRepositoryImpl
import com.suffixdigital.smartauthenticator.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @ViewModelScoped
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(
        auth = auth
    )
}