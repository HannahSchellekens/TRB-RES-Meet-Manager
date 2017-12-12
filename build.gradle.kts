import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val applicationName = "TRB-RES Meet Manager"
val gitHead = "c4c88fe"
version = "1.0-SNAPSHOT-$gitHead"

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.0"

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }
}

apply {
    plugin("java")
    plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    testCompile("junit", "junit", "4.12")

    // TornadoFX
    compile("no.tornado:tornadofx:1.7.13")

    // Jackson serialization
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

@Suppress("IMPLICIT_CAST_TO_ANY")
val fatJar = task("fatJar", type = Jar::class) {
    baseName = applicationName
    manifest {
        attributes["Implementation-Title"] = applicationName
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "nl.trbres.meetmanager.MeetManagerKt"
    }
    from(configurations.runtime.map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}