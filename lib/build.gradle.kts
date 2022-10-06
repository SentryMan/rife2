plugins {
    idea
    java
    `java-library`
    antlr
}

base {
    archivesName.set("rife")
    version = 2.0
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.3")
    antlr("org.antlr:antlr4:4.11.1")
    implementation("org.antlr:antlr4-runtime:4.11.1")
    compileOnly("org.eclipse.jetty:jetty-server:11.0.12")
    compileOnly("org.eclipse.jetty:jetty-servlet:11.0.12")
    compileOnly("jakarta.servlet:jakarta.servlet-api:5.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.postgresql:postgresql:42.5.0")
    testImplementation("mysql:mysql-connector-java:8.0.30")
    testImplementation("org.hsqldb:hsqldb:2.7.0")
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.apache.derby:derby:10.16.1.1")
    testImplementation("org.apache.derby:derbytools:10.16.1.1")
}

tasks.generateGrammarSource {
    arguments = arguments + listOf(
        "-visitor",
        "-long-messages"
    )
    outputDirectory = File("${projectDir}/src/generated/java/rife/template/antlr")
}

tasks.clean {
    delete("${projectDir}/src/generated")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    environment("project.dir", project.projectDir.toString())
}

sourceSets.main {
    java.srcDirs("${projectDir}/src/generated/java/")
}

idea {
    module {
        sourceDirs.add(File("${projectDir}/src/generated/java"))
        generatedSourceDirs.add(File("${projectDir}/src/generated/java"))
    }
}