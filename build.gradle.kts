plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.6"
}

group = "ac.grim.grimac"
version = "2.3.68"
description = "Libre simulation anticheat designed for 1.21 with 1.8-1.21 support, powered by PacketEvents 2.0."
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

// Set to false for debug builds
// You cannot live reload classes if the jar relocates dependencies
var relocate = true;

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://jitpack.io/") { // Grim API
        content {
            includeGroup("com.github.grimanticheat")
        }
    }
    maven("https://repo.viaversion.com") // ViaVersion
    maven("https://repo.aikar.co/content/groups/aikar/") // ACF
    maven("https://nexus.scarsz.me/content/repositories/releases") // Configuralize
    maven("https://repo.opencollab.dev/maven-snapshots/") // Floodgate
    maven("https://repo.opencollab.dev/maven-releases/") // Cumulus (for Floodgate)
    maven("https://repo.codemc.io/repository/maven-releases/") // PacketEvents
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    mavenCentral()
    // FastUtil, Discord-Webhooks
}

dependencies {
    implementation("club.minnced:discord-webhooks:0.8.0") // Newer versions include kotlin-stdlib, which leads to incompatibility with plugins that use Kotlin
    implementation("it.unimi.dsi:fastutil:8.5.13")
    implementation("github.scarsz:configuralize:1.4.0")

    //implementation("com.github.grimanticheat:grimapi:1193c4fa41")
    // Used for local testing: implementation("ac.grim.grimac:GRIMAPI:1.0")
    implementation("com.github.grimanticheat:grimapi:fc5634e444")

    implementation("org.jetbrains:annotations:24.1.0")
    implementation("org.geysermc.floodgate:api:2.0-SNAPSHOT")
    //implementation("com.viaversion:viaversion-api:4.9.4-SNAPSHOT")
    implementation("net.minestom:minestom-snapshots:dev")

}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

//publishing.publications.create<MavenPublication>("maven") {
//    artifact(tasks["shadowJar"])
//}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(sourceSets.getByName("main").allSource)

    archiveFileName.set("grimac-javadoc.jar")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)

    archiveFileName.set("grimac-sources.jar")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "grimac"
            artifact(tasks["shadowJar"])
            project.shadow.component(this)
        }
    }
}

tasks.shadowJar {
//    minimize()
    archiveFileName.set("${project.name}-${project.version}.jar")
//    if (relocate) {
//        relocate("io.github.retrooper.packetevents", "ac.grim.grimac.shaded.io.github.retrooper.packetevents")
//        relocate("com.github.retrooper.packetevents", "ac.grim.grimac.shaded.com.github.retrooper.packetevents")
//        relocate("co.aikar.commands", "ac.grim.grimac.shaded.acf")
//        relocate("co.aikar.locale", "ac.grim.grimac.shaded.locale")
//        relocate("club.minnced", "ac.grim.grimac.shaded.discord-webhooks")
//        relocate("github.scarsz.configuralize", "ac.grim.grimac.shaded.configuralize")
//        relocate("com.github.puregero", "ac.grim.grimac.shaded.com.github.puregero")
//        relocate("com.google.code.gson", "ac.grim.grimac.shaded.gson")
//        relocate("alexh", "ac.grim.grimac.shaded.maps")
//        relocate("it.unimi.dsi.fastutil", "ac.grim.grimac.shaded.fastutil")
//        relocate("net.kyori", "ac.grim.grimac.shaded.kyori")
//        relocate("okhttp3", "ac.grim.grimac.shaded.okhttp3")
//        relocate("okio", "ac.grim.grimac.shaded.okio")
//        relocate("org.yaml.snakeyaml", "ac.grim.grimac.shaded.snakeyaml")
//        relocate("org.json", "ac.grim.grimac.shaded.json")
//        relocate("org.intellij", "ac.grim.grimac.shaded.intellij")
//        relocate("org.jetbrains", "ac.grim.grimac.shaded.jetbrains")
//    }
}
