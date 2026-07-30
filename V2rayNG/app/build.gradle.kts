import java.util.Properties

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.jaredsburrows.license")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.v2ray.ang"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.v2ray.ang"
        minSdk = 24
        targetSdk = 36
        versionCode = 306
        versionName = "2.2.4"
        multiDexEnabled = true

        buildConfigField(
            "String",
            "REVENUECAT_API_KEY",
            "\"${localProperties["REVENUECAT_API_KEY"]}\"",
        )

        buildConfigField(
            "String",
            "ADMOB_INTERSTITIAL_ID",
            "\"${localProperties["ADMOB_INTERSTITIAL_ID"]}\""
        )

        manifestPlaceholders["ADMOB_APP_ID"] = localProperties["ADMOB_APP_ID"] as String

        val abiFilterList = (properties["ABI_FILTERS"] as? String)?.split(';')
        splits {
            abi {
                isEnable = true
                reset()
                if (abiFilterList != null && abiFilterList.isNotEmpty()) {
                    include(*abiFilterList.toTypedArray())
                } else {
                    include(
                        "arm64-v8a",
                        "armeabi-v7a",
                        "x86_64",
                        "x86"
                    )
                }
                isUniversalApk = abiFilterList.isNullOrEmpty()
            }
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
        }

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

    flavorDimensions.add("source")
    flavorDimensions.add("distribution")

    productFlavors {
        create("v2rayng") {
            dimension = "source"
            applicationId = "com.v2ray.ang"
            resValue("string", "app_name", "v2rayNG")
            buildConfigField("String", "SOURCE", "\"v2rayng\"")
        }
        create("1vpn") {
            dimension = "source"
            applicationId = "com.one.vpnapp"
            resValue("string", "app_name", "1VPN")
            buildConfigField("String", "SOURCE", "\"1vpn\"")
        }

        create("fdroid") {
            dimension = "distribution"
            applicationIdSuffix = ".fdroid"
            buildConfigField("String", "DISTRIBUTION", "\"F-Droid\"")
        }
        create("playstore") {
            dimension = "distribution"
            buildConfigField("String", "DISTRIBUTION", "\"Play Store\"")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
        getByName("1vpn") {
            java.srcDirs("src/1vpn/java")
            res.srcDirs("src/1vpn/res")
        }
    }


    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    applicationVariants.all {
        val variant = this
        val isFdroid = variant.productFlavors.any { it.name == "fdroid" }
        if (isFdroid) {
            val versionCodes =
                mapOf(
                    "armeabi-v7a" to 2,
                    "arm64-v8a" to 1,
                    "x86" to 4,
                    "x86_64" to 3,
                    "universal" to 0
                )

            variant.outputs
                .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
                .forEach { output ->
                    val abi = output.getFilter("ABI") ?: "universal"
                    output.outputFileName = "v2rayNG_${variant.versionName}-fdroid_${abi}.apk"
                    if (versionCodes.containsKey(abi)) {
//                        output.versionCodeOverride =
//                            (100 * variant.versionCode + versionCodes[abi]!!).plus(5000000)
                    } else {
                        return@forEach
                    }
                }
        } else {
            val versionCodes =
                mapOf(
                    "armeabi-v7a" to 4,
                    "arm64-v8a" to 4,
                    "x86" to 4,
                    "x86_64" to 4,
                    "universal" to 4
                )

            variant.outputs
                .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
                .forEach { output ->
                    val abi = if (output.getFilter("ABI") != null)
                        output.getFilter("ABI")
                    else
                        "universal"

                    output.outputFileName = "v2rayNG_${variant.versionName}_${abi}.apk"
                    if (versionCodes.containsKey(abi)) {
//                        output.versionCodeOverride =
//                            (1000000 * versionCodes[abi]!!).plus(variant.versionCode)
                    } else {
                        return@forEach
                    }
                }
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

}

dependencies {
    // 1VPN
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.revenuecat.purchases)
    implementation(libs.play.services.ads)
    implementation(libs.guava)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Core Libraries
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))

    // AndroidX Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.fragment)

    // UI Libraries
    implementation(libs.material)
    implementation(libs.toasty)
    implementation(libs.editorkit)
    implementation(libs.flexbox)

    // Data and Storage Libraries
    implementation(libs.mmkv.static)
    implementation(libs.gson)
    implementation(libs.okhttp)

    // Reactive and Utility Libraries
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Language and Processing Libraries
    implementation(libs.language.base)
    implementation(libs.language.json)

    // Intent and Utility Libraries
    implementation(libs.quickie.foss)
    implementation(libs.core)

    // AndroidX Lifecycle and Architecture Components
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Background Task Libraries
    implementation(libs.work.runtime.ktx)
    implementation(libs.work.multiprocess)

    // Multidex Support
    implementation(libs.multidex)

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.org.mockito.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
