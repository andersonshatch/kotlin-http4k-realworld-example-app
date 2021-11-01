import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

val kotlinVersion = "1.3.21"
val http4kVersion = "3.132.0"
val log4jVersion = "2.11.2"
val jacksonVersion = "2.9.8"
val jaxbVersion = "2.3.0"

plugins {
    kotlin("jvm") version "1.3.21"
    id("com.github.ben-manes.versions") version "0.21.0"
    id("com.adarshr.test-logger") version "1.6.0"
    application
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = URI("http://dl.bintray.com/kotlin/exposed") }
}

dependencies {
    implementation("javax.xml.bind:jaxb-api:$jaxbVersion")
    implementation("com.sun.xml.bind:jaxb-core:$jaxbVersion")
    implementation("com.sun.xml.bind:jaxb-impl:$jaxbVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-server-jetty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    implementation("org.http4k:http4k-client-apache:$http4kVersion")
    implementation("org.jetbrains.exposed:exposed:0.13.6")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("com.h2database:h2:1.4.198")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:$jacksonVersion")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    testImplementation("io.mockk:mockk:1.9.3")
}



application {
    // Define the main class for the application.
    mainClassName = "conduit.MainKt"
    applicationDefaultJvmArgs = listOf("-javaagent:contrast.jar", "-Dcontrast.standalone.appname=Kotlin-http4k")
}

tasks {
    withType<Jar> {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }

        from(configurations.compile.get().map { if (it.isDirectory) it else zipTree(it) })
    }

}



val test by tasks.getting(Test::class) {
    jvmArgs = listOf("-javaagent:contrast.jar",
                    "-Dcontrast.env=qa",
                    "-Dcontrast.standalone.appname=Kotlin-http4k",
                    "-Dcontrast.override.appversion=" + System.getenv("CI_COMMIT_SHORT_SHA"),
                    "-Dcontrast.application.session_metadata=buildNumber=" + System.getenv("CI_PIPELINE_URL") + ",commitHash=" + System.getenv("CI_COMMIT_SHORT_SHA"),
                    "-Dcontrast.agent.logger.stdout=true"
                    )
    useJUnitPlatform { }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"
