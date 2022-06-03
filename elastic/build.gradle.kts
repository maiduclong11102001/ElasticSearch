plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    application
}

group = "com.spring"

application {
    mainClass.set("com.spring.elastic.ElasticApplication")
}

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    val prjPath = project.projectDir.path

    shadowJar {
        archiveFileName.set("ElasticSearch.jar")
        isZip64 = true
    }

    register("pathArgument") {
        dependsOn(shadowJar)

        if(project.hasProperty("jarpath")) {
            doLast {
                copy {
                    from("$prjPath/build/libs/ElasticSearch.jar", "$prjPath/build/libs/elastic.jar")
                    into(project.property("jarpath").toString())
                }
            }
        }
    }

    assemble {
        dependsOn("pathArgument")
    }
}

tasks.jar {
    enabled = false
}