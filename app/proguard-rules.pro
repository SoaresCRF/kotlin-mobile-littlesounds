# Mantém o conversor Gson do Retrofit
-keep class retrofit2.converter.gson.** { *; }

# Mantém os campos anotados com @SerializedName (importante para Gson)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Evita warnings irrelevantes das libs AndroidX e Retrofit
-dontwarn androidx.**
-dontwarn retrofit2.**
-dontwarn javax.annotation.**