import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0-M2"
    kotlin("plugin.serialization") version "1.4.31"
    application
}

group = "me.matteowohlrapp"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.example.ServerKt")
}


repositories {
    mavenCentral()
    jcenter()
}

//val jar by tasks.getting(Jar::class) {
//    manifest {
//        attributes["Main-Class"] = "com.example.Application"
//    }
//}

dependencies {
    val exposedVersion = "0.30.1"
    val serializationVersion = "1.1.0"
    testImplementation(kotlin("test-junit"))
//    ktor
    implementation("io.ktor:ktor-server-core:1.5.3")
    implementation("io.ktor:ktor-server-netty:1.5.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
//    exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
//    postgres
    implementation("org.postgresql:postgresql:42.2.2")
    // serialization for easy conversion to JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}