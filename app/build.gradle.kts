plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.diplom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.diplom"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // https://mvnrepository.com/artifact/com.squareup.picasso/picasso
    implementation("com.squareup.picasso:picasso:2.71828")




    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.yandex.android:maps.mobile:4.4.0-lite")
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.airbnb.android:lottie:3.4.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("ru.tinkoff.decoro:decoro:1.5.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}