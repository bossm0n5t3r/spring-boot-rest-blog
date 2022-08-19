import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

group = "me.bossm0n5t3r"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

extra["testcontainersVersion"] = "1.17.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    implementation("commons-codec:commons-codec:1.15")
    implementation("com.google.guava:guava:31.1-jre")

    implementation("org.springdoc:springdoc-openapi-ui:1.6.10")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mariadb")

    // h2
    testImplementation("com.h2database:h2")

    // MockK
    testImplementation("io.mockk:mockk:1.12.5")

    // SpringMockK
    testImplementation("com.ninja-squad:springmockk:3.1.1")

    // Java Faker
    testImplementation("com.github.javafaker:javafaker:1.0.2") {
        // https://github.com/DiUS/java-faker/issues/733
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
