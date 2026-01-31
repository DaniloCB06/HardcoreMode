plugins {
    id("java")
}

group = "com.example"
version = "3.0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("libs/HytaleServer.jar"))
    compileOnly(fileTree("libs") { include("tinymessage-*.jar") })
    compileOnly(fileTree("test-integration") { include("RPGLeveling-*.jar") })
    testCompileOnly(files("libs/HytaleServer.jar"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// Bundle the creature classification file so it is available on the classpath at runtime.
tasks.processResources {
    from("Category_Mobs.txt")
}
