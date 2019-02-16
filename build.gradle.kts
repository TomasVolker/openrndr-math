import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.3.21"
    maven
}

group = "tomasvolker"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val openrndrVersion = "0.3.30"

val openrndrOS = when (OperatingSystem.current()) {
    OperatingSystem.WINDOWS -> "windows"
    OperatingSystem.LINUX -> "linux-x64"
    OperatingSystem.MAC_OS -> "macos"
    else -> error("unsupported OS")
}

dependencies {
    implementation(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")

    compile("org.openrndr:openrndr-core:$openrndrVersion")
    compile("org.openrndr:openrndr-extensions:$openrndrVersion")
    //compile("org.openrndr:openrndr-ffmpeg:$openrndrVersion")

    runtime("org.openrndr:openrndr-gl3:$openrndrVersion")
    runtime("org.openrndr:openrndr-gl3-natives-$openrndrOS:$openrndrVersion")
    
}
