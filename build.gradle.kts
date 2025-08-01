plugins {
    id("com.android.application") version "8.12.0" apply false
    id("com.android.library") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.25" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
