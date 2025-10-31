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
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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

    implementation(platform(libs.firebase.bom))

    implementation(libs.google.firebase.analytics)
    implementation (libs.google.firebase.authentication)

    //	Google Auth
    implementation (libs.play.services.auth.api.phone)

    implementation (libs.firebase.ui.auth)
    implementation (libs.gms.play.services.auth)


    implementation (libs.facebook.login)

    //Hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)


    implementation (libs.sdp.android)  // for dp sizes
    implementation (libs.ssp.android)  // for sp text sizes
}