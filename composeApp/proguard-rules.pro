# Valguard ProGuard Rules
# Keep this file updated as you add new libraries

# ==================== Kotlin ====================
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Kotlin Metadata
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# ==================== Coroutines ====================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==================== Serialization ====================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep @Serializable classes
-keep,includedescriptorclasses class com.example.valguard.**$$serializer { *; }
-keepclassmembers class com.example.valguard.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.valguard.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ==================== Ktor ====================
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn io.ktor.**

# Ktor Client
-keepclassmembers class io.ktor.** { volatile <fields>; }

# ==================== Room Database ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Room DAO
-keep interface * extends androidx.room.Dao { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep Room generated classes
-keep class com.example.valguard.app.core.database.** { *; }

# ==================== Koin ====================
-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.dsl.** { *; }

# Keep Koin modules
-keep class com.example.valguard.app.di.** { *; }

# ==================== Compose ====================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# Keep Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ==================== Lifecycle ====================
-keep class androidx.lifecycle.** { *; }
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ==================== Navigation ====================
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# ==================== Coil ====================
-keep class coil3.** { *; }
-dontwarn coil3.**

# ==================== App-Specific ====================
# Keep all ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep all data classes
-keep class com.example.valguard.**.domain.** { *; }
-keep class com.example.valguard.**.data.**.* { *; }

# Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# ==================== Debugging ====================
# Keep line numbers for stack traces
-keepattributes SourceFile,LineNumberTable

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-assumenosideeffects class kotlin.io.ConsoleKt {
    public static *** println(...);
    public static *** print(...);
}

# ==================== Enum Handling ====================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==================== Reflection ====================
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ==================== Optimization ====================
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

