# Gson serialization — App is serialized to/from SharedPreferences for auto-start
-keepclassmembers class tech.ula.model.entities.App {
    <fields>;
}

# Moshi codegen adapters kept automatically by @JsonClass(generateAdapter=true)
# Room generated code kept automatically by room compiler
# Sentry kept automatically by Sentry Android SDK

-keep public class * extends androidx.fragment.app.Fragment
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep public class androidx.navigation.NavType { *; }
-keep public class androidx.navigation.NavType$* { *; }

-keep class tech.ula.model.entities.App { *; }
-keep class tech.ula.model.entities.Session { *; }
-keep class tech.ula.model.entities.Filesystem { *; }

-dontwarn org.brotli.**
-dontwarn org.tukaani.**
