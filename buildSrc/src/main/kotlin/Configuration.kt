@file:Suppress("unused", "SpellCheckingInspection")

object Android {
    const val compileSdkVersion = 30
    const val buildToolsVersion = "30.0.3"
    const val minSdkVersion = 15
    const val targetSdkVersion = 30

    const val versionName = "1.0"
    const val versionCode = 1
}

object Versions {
    const val kotlin = "1.4.31"
    const val appcompat = "1.2.0"
    const val material = "1.3.0"
    const val constraintlayout = "2.0.4"
    const val eventbus = "3.1.1"
    const val butterknife = "10.2.1"
}

object Publish {
    const val groupId = "com.github.tiamosu"
}

object Deps {
    const val androidx_appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintlayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    const val eventbus = "org.greenrobot:eventbus:${Versions.eventbus}"

    const val butterknife = "com.jakewharton:butterknife:${Versions.butterknife}"
    const val butterknife_compiler = "com.jakewharton:butterknife-compiler:${Versions.butterknife}"
}