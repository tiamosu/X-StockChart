@file:Suppress("unused", "SpellCheckingInspection")

object Android {
    const val compileSdk = 33
    const val minSdk = 21
    const val targetSdk = 33

    const val versionName = "1.0"
    const val versionCode = 1
}

object Versions {
    const val butterknife = "10.2.1"
}

object Deps {
    const val appcompat = "androidx.appcompat:appcompat:1.6.1"
    const val material = "com.google.android.material:material:1.9.0"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.4"
    const val eventbus = "org.greenrobot:eventbus:3.1.1"

    const val butterknife = "com.jakewharton:butterknife:${Versions.butterknife}"
    const val butterknife_compiler = "com.jakewharton:butterknife-compiler:${Versions.butterknife}"
}