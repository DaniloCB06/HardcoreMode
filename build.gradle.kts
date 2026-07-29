plugins {
    id("java")
}

group = "com.example"
version = "4.0.0"

val launcherHytaleServerJar = File(
    System.getenv("APPDATA"),
    "Hytale/install/release/package/game/latest/Server/HytaleServer.jar"
)
val workspaceHytaleServerJar = layout.projectDirectory.file("libs/HytaleServer.jar").asFile
val configuredHytaleServerJar = providers.gradleProperty("hytaleServerJar")
    .map(::File)
    .orNull
val hytaleServerJar = sequenceOf(
    configuredHytaleServerJar,
    workspaceHytaleServerJar,
    launcherHytaleServerJar
).firstOrNull { it != null && it.isFile }
    ?: error(
        "Could not find HytaleServer.jar. " +
            "Provide -PhytaleServerJar=<path>, install Hytale via the launcher, " +
            "or place the jar at libs/HytaleServer.jar."
    )

val compileLibsDir = layout.buildDirectory.dir("compile-libs")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files(hytaleServerJar))
    compileOnly(fileTree("test-integration") { include("RPGLeveling-*.jar") })
    compileOnly(fileTree("libs") { include("tinymessage-*.jar") })
    testCompileOnly(files(hytaleServerJar))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val prepareCompileLibs by tasks.registering(Sync::class) {
    from(hytaleServerJar)
    from(fileTree("libs") { include("tinymessage-*.jar") })
    into(compileLibsDir)
}

val compileJavaExternal by tasks.registering(Exec::class) {
    dependsOn(prepareCompileLibs)

    val sourceFiles = sourceSets.main.get().java
    val destinationDir = layout.buildDirectory.dir("classes/java/main")

    inputs.files(sourceFiles)
    inputs.files(fileTree(compileLibsDir))
    outputs.dir(destinationDir)

    doFirst {
        val libsDir = compileLibsDir.get().asFile
        val classpathEntries = buildList {
            add(libsDir.resolve(hytaleServerJar.name).absolutePath)
            libsDir.listFiles()
                ?.filter { it.name.startsWith("tinymessage-") && it.extension == "jar" }
                ?.sortedBy { it.name }
                ?.mapTo(this) { it.absolutePath }
        }

        val outputDir = destinationDir.get().asFile
        outputDir.mkdirs()

        val compiler = javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(25))
        }.get()

        executable = compiler.executablePath.asFile.absolutePath
        args = listOf(
            "-cp",
            classpathEntries.joinToString(File.pathSeparator),
            "-d",
            outputDir.absolutePath
        ) + sourceFiles.files.sortedBy { it.absolutePath }.map { it.absolutePath }
    }
}

tasks.named<JavaCompile>("compileJava") {
    enabled = false
}

tasks.named<JavaCompile>("compileTestJava") {
    enabled = false
}

tasks.named("classes") {
    dependsOn(compileJavaExternal)
}

tasks.test {
    enabled = false
}

// Bundle the creature classification file so it is available on the classpath at runtime.
tasks.processResources {
    from("Category_Mobs.txt")
}
