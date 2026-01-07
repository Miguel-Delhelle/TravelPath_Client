plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "fr.um.ico.mobile.travelpath"
    compileSdk = 36

    defaultConfig {
        applicationId = "fr.um.ico.mobile.travelpath"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Important pour JUnit 5
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 1. ACTIVER VIEWBINDING (Adieu findViewById)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // 2. CONFIGURATION JUNIT 5
    testOptions {
        unitTests.all {
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // NETWORKING
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // ARCHITECTURE COMPONENTS (MVVM)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation(libs.fragment)

    implementation(libs.play.services.location)
    implementation(libs.recyclerview)

    // UNIT TESTING (JUnit 5 + Mockito)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.3.1")

    // INSTRUMENTED TESTING
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}