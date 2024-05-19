

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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Photo Editor"
include(":app")



include (":opencv")

val opencvsdk:String by settings
project(":opencv").projectDir = File(opencvsdk + "/sdk")

//project(":opencv").projectDir = File("C:/OpenCV-android-sdk" + "/sdk")


