plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-parcelize")
    alias(libs.plugins.androidx.navigation.safe.args)
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.capstone.lensakulitku"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.capstone.lensakulitku"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Define Room schema directory for testing/debugging
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    // Retrofit Dependency
    implementation (libs.squareup.retrofit)
    implementation (libs.converter.gson)

    // OKhttp Logging interceptor dependency
    implementation (libs.logging.interceptor)

    // Lifecylce dependency
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)

    // Navigation Dependency
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    // Tap Target View Library
    implementation (libs.getkeepsafe.taptargetview)

    //JWT decode dependency
    implementation (libs.android.jwtdecode)

    // CameraX Core
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.camera2)

    // UCrop
    implementation (libs.ucrop.v2210)

    // Photo Picker
    implementation (libs.androidx.activity.ktx)

    // Lottie
    implementation (libs.lottie.v610)

    //Glide
    implementation (libs.glide)

    // Room
    val room_version = "2.6.1"

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)



    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation (libs.androidx.viewpager2)
    implementation (libs.lottie)
    implementation (libs.material)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}