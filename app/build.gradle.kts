import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Lee local.properties para overrides por máquina (ej. IP real para celular físico).
// Este archivo está en .gitignore y nunca se sube al repositorio.
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use(this::load)
}

android {
    namespace = "com.example.economix_android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.economix_android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        debug {
            val baseUrl = localProps.getProperty("ECONOMIX_BASE_URL_DEBUG")
                ?: (project.findProperty("ECONOMIX_BASE_URL_DEBUG") as String?)
                    ?.takeIf { it.isNotBlank() }
                ?: "http://192.168.1.73:8080/"
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        }
        release {
            isMinifyEnabled = false
            val baseUrl = localProps.getProperty("ECONOMIX_BASE_URL_RELEASE")
                ?: localProps.getProperty("ECONOMIX_BASE_URL_DEBUG")
                ?: (project.findProperty("ECONOMIX_BASE_URL_RELEASE") as String?)
                    ?.takeIf { it.isNotBlank() }
                ?: (project.findProperty("ECONOMIX_BASE_URL_DEBUG") as String?)
                    ?.takeIf { it.isNotBlank() }
                ?: "http://192.168.1.73:8080/"
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    implementation("androidx.navigation:navigation-fragment:2.9.5")
    implementation("androidx.navigation:navigation-ui:2.9.5")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")
    implementation("com.google.android.material:material:1.5.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}
