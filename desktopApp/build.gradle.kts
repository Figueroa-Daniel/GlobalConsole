import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    
    // Koin - Inyección de dependencias
    implementation(libs.koin.core)

    // LWJGL 3 - Entrada de Mandos (GLFW)
    implementation(libs.lwjgl)
    implementation(libs.lwjgl.glfw)
    val osName = System.getProperty("os.name").lowercase()
    val lwjglNatives = when {
        osName.contains("win") -> "natives-windows"
        osName.contains("mac") -> "natives-macos"
        else -> "natives-linux"
    }
    runtimeOnly("org.lwjgl:lwjgl::${lwjglNatives}")
    runtimeOnly("org.lwjgl:lwjgl-glfw::${lwjglNatives}")

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutinesTest)
}

compose.desktop {
    application {
        mainClass = "org.example.globalconsole.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.globalconsole"
            packageVersion = "1.0.0"
        }
    }
}