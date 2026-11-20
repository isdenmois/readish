plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.isdenmois.readish"
        minSdk = 28
        targetSdk = 31
        versionCode = 26
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/*",
                "META-INF/licenses/*",
                "**/attach_hotspot_windows.dll",
                "**/jansi.dll",
                "META-INF/*.kotlin_module",
                "**.kotlin_builtins",
                "**.kotlin_metadata",
                "/META-INF/{AL2.0,LGPL2.1}",
                "/org/fusesource**",
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.hilt.android) {
        exclude(module = "dagger-lint-aar")
    }
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.javax.annotation.api)

    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(project(":shared"))
    implementation(project(":feature:home"))
    implementation(project(":feature:transfers"))
}

configurations.named("implementation") {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    exclude(group = "androidx.lifecycle", module = "lifecycle-livedata-core")
    exclude(group = "androidx.lifecycle", module = "lifecycle-livedata")
}
