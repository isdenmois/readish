buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.41")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        ignoreFailures.set(true)
        enableExperimentalRules.set(true)

        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
