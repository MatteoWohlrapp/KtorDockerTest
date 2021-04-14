import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    application
}

group = "me.matteowohlrapp"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.example.ApplicationKt")
}


repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val exposedVersion = "0.30.1"
    testImplementation(kotlin("test-junit"))
//    ktor
    implementation("io.ktor:ktor-server-core:1.5.3")
    implementation("io.ktor:ktor-server-netty:1.5.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
//    exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
//    postgres
    implementation("org.postgresql:postgresql:42.2.2")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}