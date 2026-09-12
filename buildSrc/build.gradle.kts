plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test-junit"))
}
