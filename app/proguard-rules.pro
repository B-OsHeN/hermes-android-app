# Hermes Agent ProGuard rules
-keep class com.hermesagent.app.data.api.** { *; }
-keep class com.hermesagent.app.domain.model.** { *; }
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontwarn retrofit2.**