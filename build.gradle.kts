import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.3.21"
    maven
}

group = "tomasvolker"
version = "0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/openrndr/openrndr/") }
}

val openrndrVersion = "0.3.30"

val openrndrOS = when (OperatingSystem.current()) {
    OperatingSystem.WINDOWS -> "windows"
    OperatingSystem.LINUX -> "linux-x64"
    OperatingSystem.MAC_OS -> "macos"
    else -> error("unsupported OS")
}

dependencies {
    api(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")

    api("org.openrndr:openrndr-core:$openrndrVersion")
    api("org.openrndr:openrndr-extensions:$openrndrVersion")

    testRuntime("org.openrndr:openrndr-gl3:$openrndrVersion")
    testRuntime("org.openrndr:openrndr-gl3-natives-$openrndrOS:$openrndrVersion")
    
}
