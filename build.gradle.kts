import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform") version "1.4.21"
    kotlin("native.cocoapods") version "1.4.21"
    id("org.jetbrains.dokka") version "0.10.1"
    id("maven-publish")
    id("com.android.library") version "3.5.3"
}

repositories {
    google()
    mavenCentral()
    jcenter()
}


group = "com.realeyes"
version = "0.0.2"

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
    android {
        publishLibraryVariants("release", "debug")
    }

    ios {
        binaries {
            sharedLib {

            }
        }

    }

    cocoapods {
        summary = "CocoaPods stce35-decoder library"
        homepage = "Link to a Kotlin/Native module homepage"
        ios.deploymentTarget = "0.0.2"
    }

    js {
//        browser {
//        }

        nodejs {
        }
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.Experimental")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-utils:1.5.0")
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

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.robolectric:robolectric:4.5-alpha-3")
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
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
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



tasks.register("debugFatFramework", FatFrameworkTask::class) {
    baseName = project.name

    // Framework is output here
    destinationDir = buildDir.resolve("fat-framework/debug")

    val targets = mutableListOf(
        kotlin.iosX64(),
        kotlin.iosArm64()
    )

    from(targets.map { it.binaries.getFramework(NativeBuildType.DEBUG) })
}

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

val packForXcode by tasks.creating(Sync::class) {
    val targetDir = File(buildDir, "xcode-frameworks")

    /// selecting the right configuration for the iOS
    /// framework depending on the environment
    /// variables set by Xcode build
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName: String? = System.getenv("SDK_NAME")
    val isiOSDevice = sdkName.orEmpty().startsWith("iphoneos")
    val framework = kotlin.targets
        .getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>(
            if (isiOSDevice) {
                "iosArm64"
            } else {
                "iosX64"
            }
        )
        .binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    from({ framework.outputDirectory })
    into(targetDir)

    /// generate a helpful ./gradlew wrapper with embedded Java path
    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText(
            "#!/bin/bash\n"
                + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
                + "cd '${rootProject.rootDir}'\n"
                + "./gradlew \$@\n"
        )
        gradlew.setExecutable(true)
    }
}
tasks.getByName("build").dependsOn(packForXcode)

