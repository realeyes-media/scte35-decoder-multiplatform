import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform") version "1.9.22"
    kotlin("native.cocoapods") version "1.9.22"
    id("org.jetbrains.dokka") version "0.10.1"
    id("maven-publish")
    id("com.android.library") version "8.0.2"
}

repositories {
    google()
    mavenCentral()
}


group = "com.realeyes"
version = "0.0.1"

publishing {

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/realeyes-media/scte35-decoder-multiplatform")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    applyDefaultHierarchyTemplate ()

    iosX64()
    iosArm64()

    cocoapods {
        summary = "CocoaPods stce35-decoder library"
        homepage = "https://github.com/realeyes-media/scte35-decoder-multiplatform"
        ios.deploymentTarget = "15"
    }

    js {
//        browser {
//        }

        nodejs {
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-utils:2.3.7")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

android {
    namespace = "scte35.decoder.multiplatform"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }

}

//tasks.register("debugFatFramework", FatFrameworkTask::class) {
//    baseName = project.name
//
//    // Framework is output here
//    destinationDir = buildDir.resolve("fat-framework/debug")
//
//    val targets = mutableListOf(
//        kotlin.iosX64(),
//        kotlin.iosArm64()
//    )
//
//    from(targets.map { it.binaries.getFramework(NativeBuildType.DEBUG) })
//}

if (HostManager.hostIsMac) {
    val iosTest = tasks.register("iosTest", Exec::class) {
        val iosX64 = kotlin.iosX64()
        val device = project.findProperty("iosDevice")?.toString() ?: "iPhone 11"
        dependsOn(iosX64.binaries.getTest("DEBUG").linkTaskName)
        group = JavaBasePlugin.VERIFICATION_GROUP
        description = """Runs tests for target "ios" on iOS simulator"""
        executable = "xcrun"
        setArgs(
            listOf(
                "simctl",
                "spawn",
                "-s",
                device,
                iosX64.binaries.getTest(NativeBuildType.DEBUG).outputFile
            )
        )
    }

    val checkTask = tasks.named("check")
    checkTask.configure {
        dependsOn(iosTest)
    }
}
