# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- CONFIGURACIÓN GENERAL ---
# Mantener atributos esenciales para que Retrofit y Gson funcionen
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# --- RETROFIT ---
# Mantener cualquier interfaz que use anotaciones de Retrofit (@GET, @POST, etc.)
-keep interface * { @retrofit2.http.* <methods>; }

# Ignorar advertencias comunes de librerías que usan partes de Java no disponibles en Android
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn retrofit2.Platform$Java8

# --- GSON & DTOs (CRÍTICO) ---
# Si R8 cambia el nombre de las variables (ej: 'nombre' -> 'a'), el JSON fallará.
# Esto protege todas las clases dentro de tu paquete de DTOs.
-keep class com.example.farmaceuticasalvia.data.remote.dto.** { *; }

# --- COIL (Imágenes) ---
-keep class coil.** { *; }

# --- ROOM (Si quedara algo residual) ---
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**