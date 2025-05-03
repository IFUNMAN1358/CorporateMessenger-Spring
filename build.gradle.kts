plugins {
    id("java")
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.nagornov.CorporateMessenger"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // core
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    // postgres
    implementation("org.hibernate:hibernate-core:6.2.0.Final")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.liquibase:liquibase-core:4.20.0")
    // cassandra
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra")
    implementation("org.springframework.data:spring-data-cassandra:4.3.1")
    implementation("com.datastax.oss:java-driver-core:4.17.0")
    implementation("org.cognitor.cassandra:cassandra-migration:2.6.1_v4")
    // kafka
    implementation("org.springframework.kafka:spring-kafka")
    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    // minio
    implementation("io.minio:minio:8.5.10")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("software.amazon.awssdk:s3:2.20.0")
    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-crypto")
    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    // jakarta
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    // lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
    // mapstruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.bootJar {
    enabled = true
}

tasks.test {
    useJUnitPlatform()
}