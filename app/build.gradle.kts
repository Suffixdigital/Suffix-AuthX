plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.suffixdigital.smartauthenticator"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.suffixdigital.smartauthenticator"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += arrayOf("-Xjvm-default=all")
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }


    buildToolsVersion = "36.0.0"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.lifecycle.viewmodel.ktx) // or the latest version

    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth-ktx")

    //	Google Auth
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    implementation ("com.google.android.gms:play-services-auth-api-phone:18.2.0")

    implementation (libs.firebase.ui.auth)
    implementation (libs.gms.play.services.auth)


    implementation (libs.facebook.login)

    //Hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)


    implementation (libs.sdp.android)  // for dp sizes
    implementation (libs.ssp.android)  // for sp text sizes
}