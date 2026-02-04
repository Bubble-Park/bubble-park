import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
}

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://jitpack.io") }
}


val myPackage =  providers.gradleProperty("myPackage").get()
val myVersionName = providers.gradleProperty("myVersionName").get()
val myBaseName = providers.gradleProperty("myBaseName").get()
val myBaseNameWasm = providers.gradleProperty("myBaseNameWasm").get()


kotlin {
    android {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        namespace = myPackage

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            minSdk = 24
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }
    
    jvm("desktop"){
        compilerOptions{
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = myBaseName
            isStatic = true
        }
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = myBaseNameWasm

        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "$myBaseNameWasm.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static(rootDirPath)
                    static(projectDirPath)
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.common)
            implementation(libs.androidx.media3.datasource)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.multiplatform.settings.no.arg)
            //androidRuntimeClasspath(libs.androidx.compose.ui.tooling)
            //androidRuntimeClasspath(libs.compose.uiTooling)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.jlayer)
        }
    }
}

compose.desktop {


    application {
        mainClass = "$myPackage.MainKt"

        jvmArgs("--enable-native-access=ALL-UNNAMED")


        buildTypes.release.proguard {
            version.set("7.3.0")
            isEnabled = false
            configurationFiles.from(file("proguard-rules.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = myPackage
            packageVersion = myVersionName
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = myPackage
    generateResClass = always
}

