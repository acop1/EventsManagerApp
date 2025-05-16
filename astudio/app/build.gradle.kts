plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.eventsmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eventsmanager"
        minSdk = 30
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
    }
}

configurations.all {
    resolutionStrategy {
        // Force all protobuf dependencies to use the same version
        force("com.google.protobuf:protobuf-javalite:3.25.5")

        // Exclude the old protobuf-lite from all configurations
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.car.ui.lib)
    implementation(libs.espresso.core)
    implementation(libs.ext.junit)
    implementation(libs.espresso.contrib) {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    implementation(libs.firebase.firestore)
    implementation(libs.appcompat)
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

}

