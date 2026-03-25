pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                // Reference the secret token from local.properties
                val localProperties = java.util.Properties()
                val localPropertiesFile = rootDir.resolve("local.properties")
                if (localPropertiesFile.exists()) {
                    localProperties.load(localPropertiesFile.inputStream())
                }
                password = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN") ?: ""
            }
        }
    }
}

rootProject.name = "LocalMarketplace"
include(":app")
