plugins {
    id("java")
}

group = "com.example"
version = "2.0.2"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("libs/HytaleServer.jar"))
    compileOnly(fileTree("libs") { include("tinymessage-*.jar") })
    compileOnly(fileTree("test-integration") { include("RPGLeveling-*.jar") })
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
