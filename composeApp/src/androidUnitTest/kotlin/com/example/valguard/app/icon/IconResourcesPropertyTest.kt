package com.example.valguard.app.icon

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.checkAll
import java.io.File

/**
 * Property-based tests for app icon resources
 * Feature: app-icon-update, Property 2: Icon File Existence
 * Validates: Requirements 1.2, 5.2
 */
class IconResourcesPropertyTest : StringSpec({

    val projectRoot = System.getProperty("user.dir")
    val androidResPath = "$projectRoot/composeApp/src/androidMain/res"
    val densities = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")
    val iconFiles = listOf("ic_launcher.png", "ic_launcher_round.png", "ic_launcher_foreground.png")

    "Property 2: For any required Android icon density, the corresponding icon files SHALL exist" {
        checkAll(100, Arb.element(densities)) { density ->
            val mipmapDir = File("$androidResPath/mipmap-$density")
            
            // Directory should exist
            mipmapDir.exists() shouldBe true
            mipmapDir.isDirectory shouldBe true
            
            // All required icon files should exist
            iconFiles.forEach { iconFile ->
                val file = File(mipmapDir, iconFile)
                file.exists() shouldBe true
                file.isFile shouldBe true
                (file.length() > 0) shouldBe true // File should not be empty
            }
        }
    }

    "Property 2: Adaptive icon configuration files SHALL exist for API 26+" {
        val adaptiveIconDir = File("$androidResPath/mipmap-anydpi-v26")
        
        adaptiveIconDir.exists() shouldBe true
        adaptiveIconDir.isDirectory shouldBe true
        
        val launcherXml = File(adaptiveIconDir, "ic_launcher.xml")
        val launcherRoundXml = File(adaptiveIconDir, "ic_launcher_round.xml")
        
        launcherXml.exists() shouldBe true
        launcherRoundXml.exists() shouldBe true
    }

    "Property 2: Icon background color resource SHALL exist" {
        val valuesDir = File("$androidResPath/values")
        val backgroundColorFile = File(valuesDir, "ic_launcher_background.xml")
        
        backgroundColorFile.exists() shouldBe true
        backgroundColorFile.isFile shouldBe true
    }
})
