import org.gradle.kotlin.dsl.kotlin

plugins {
    //alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.androidApplication)
    //alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    //alias(libs.plugins.androidLibrary)
}

val myPackage =  providers.gradleProperty("myPackage").get()
val myAndroidId =  providers.gradleProperty("myAndroidId").get()
val myVersionCode = providers.gradleProperty("myVersionCode").get().toInt()
val myVersionName = providers.gradleProperty("myVersionName").get()

kotlin {
    android {

        namespace = "fr.iutlens.mmi.app"
        compileSdk {
            version = release(36)
            // compileSdkVersion(libs.versions.android.compileSdk.get().toInt())
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }


        defaultConfig {
            applicationId = myAndroidId
            minSdk = 24
            targetSdk = 36
            versionCode = myVersionCode
            versionName = myVersionName
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


        target {
            compileOptions {
                //jvmTarget.set(JvmTarget.JVM_11)
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }

        buildFeatures {
            compose = true
        }
        //kotlin { jvmToolchain(11) }

        buildToolsVersion = "36.1.0"
    }

    dependencies {
        implementation(projects.composeApp)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.graphics)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.material3)
        implementation(project(":composeApp"))

        debugImplementation(libs.androidx.compose.ui.tooling)
        debugImplementation(libs.androidx.compose.ui.test.manifest)
    }
}