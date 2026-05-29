plugins {
    kotlin("jvm") version "2.3.0"
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("signing")
}

group = "edu.kit.ifv.mobitopp"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("org.apache.commons:commons-statistics-inference:1.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.20.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
//    api("org.jetbrains.kotlinx:kotlin-statistics-jvm:0.2.1")
    implementation("it.unimi.dsi:fastutil:8.5.16")
    implementation("org.ejml:ejml-all:0.43")
    implementation("org.apache.spark:spark-mllib_2.13:3.5.4")
//    api("org.jetbrains.kotlin:kotlin-gradle-statistics:2.3.10")
//    api("org.jetbrains.kotlinx:kandy-lets-plot:0.8.3")
//    api("org.jetbrains.kotlinx:kandy-api:0.8.3")
//    api("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.11.2")
//    api("org.jetbrains.lets-plot:lets-plot-image-export:4.8.2")
//    api("org.jetbrains.lets-plot:lets-plot-batik:4.8.2")
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.24.3"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    implementation("org.apache.logging.log4j:log4j-1.2-api")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }

}

java {
    withSourcesJar()
    withJavadocJar()
}


if (checkProperty("doPublish")) {
    /* mobiTopp publishing process (see .gitlab-ci.yml)
        * Parameters such as "doPublish" must be passed in gradle command:
        *  - ./gradlew <TASKS> -PdoPublish=true -Pparam=value...
        * Lookup of parameters doPublish and isRelease returns true if they are specified and their value reads "true".
        * Other required parameters must be specified, otherwise an error is thrown.
        *
        * The pipeline build version is used as the published artifacts version string.
        *  - uses parameter: "buildVersion"
        *
        * Every merge on main is published to local repo: see deploy-job
        *  - checks: doPublish=true, isRelease=false
        *  - gradle task: publish
        *  - requires parameters: "localUrl", "localRepoUser" and "localRepoPassword"
        *
        * Public releases must be published manually:
        *  - checks: doPublish=true, isRelease=true
        *  - gradle tasks: publishToSonatype closeSonatypeStagingRepository
        *  - requires parameters: sonatypeUsername, sonatypePassword signing.keyId signing.password signing.secretKeyRingFile
        */

    project.version = requireProperty("buildVersion")
    println("Setup publishing configuration for ${group}:${project.name}:${version}.")

    publishing {

        val githubURL: String = "github.com/kit-ifv/populationsynthesis"
        val projectDescription: String = "A collection of pipulation synthesis algorithms for travel demand modeling."
        publications {

            create<MavenPublication>("mavenData") {
                from(components["java"])
                groupId = group.toString()
                artifactId = project.name
                version = project.version.toString()

                pom {
                    name.set(project.name)
                    description.set(projectDescription)
                    url.set("https://$githubURL")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://mit-license.org")
                        }
                    }

                    developers {
                        developer {
                            id.set("id")
                            name.set("name")
                            email.set("mail")
                        }
                    }

                    scm {
                        connection.set("scm:git:git:https://$githubURL.git")
                        developerConnection.set("scm:git:ssh://git@$githubURL.git")
                        url.set("https://$githubURL")
                    }
                }
            }

        }

        repositories {
            if (checkProperty("isRelease")) {
                println("Activate: publish public release!")

                signing {
                    sign(publishing.publications)
                }

                nexusPublishing {
                    repositories {
                        // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
                        sonatype {
                            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
                        }
                    }
                }

            } else {
                println("Activate: publish local build!")
                maven {
                    name = "LocalRepo"
                    url = uri(requireProperty("localUrl"))
                    credentials {
                        username = requireProperty("localRepoUser")
                        password = requireProperty("localRepoPassword")
                    }
                }
            }
        }

    }

}


fun requireProperty(property: String, orElse: String? = null): String =
    requireNotNull(project.findProperty(property) as? String ?: orElse) {
        "Could not find property '$property'. Please check the gradle command args. It should contain:\n" +
                "    ./gradlew ... -P$property=<VALUE> ..."
    }

fun checkProperty(property: String): Boolean = project.hasProperty(property) && project.property(property) == "true"
