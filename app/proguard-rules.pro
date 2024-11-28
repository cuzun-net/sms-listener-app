# Keep your application class if you have one
-keep class com.example.smsprojx.** { *; }

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepclassmembers class * implements javax.net.ssl.SSLSocketFactory {
    private final javax.net.ssl.SSLSocketFactory delegate;
}

# Keep JSON classes if you're using them
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Android runtime permissions related classes
-keep class androidx.core.app.** { *; }

# Keep SMS receiver
-keep class com.example.smsprojx.SmsReceiver { *; }

# General Android rules
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
