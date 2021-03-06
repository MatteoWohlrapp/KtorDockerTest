import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0-M2"
    application
    kotlin("plugin.serialization") version "1.5.0"
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

dependencies {
    val exposedVersion = "0.30.1"
    val serializationVersion = "1.1.0"
    val ktorVersion = "1.5.3"
    // Current version
    val koin_version = "3.0.1"
    testImplementation(kotlin("test-junit"))
//    ktor
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:1.5.3")
    implementation("io.ktor:ktor-html-builder:1.5.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.ktor:ktor-locations:1.5.3")

//    exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
//    postgres
    implementation("org.postgresql:postgresql:42.2.2")
    implementation(kotlin("stdlib-jdk8"))


    // serialization for easy conversion to JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

    // Koin for Ktor
    implementation("io.insert-koin:koin-ktor:$koin_version")

}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}