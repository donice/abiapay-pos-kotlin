# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep JavaScript interface methods
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep WebView related classes
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-dontwarn com.yourcompany.hydrogenbridgeapp.**
-keep class com.yourcompany.hydrogenbridgeapp.** { *; }
