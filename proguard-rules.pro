# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

# For Android applications, we can specify -dontobfuscate to keep the stack
# traces readable. We can also keep the attributes for debugging even in
# the obfuscated code.

-dontobfuscate

# Keep application classes that use reflection, a common pattern for
# interfaces and base classes.

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Preserve line numbers for debugging stack traces.

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Remove logging calls.

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Preserve classes and methods for email
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }
-keep class com.sun.mail.** { *; }
