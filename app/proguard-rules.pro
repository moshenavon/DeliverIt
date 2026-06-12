# Gson deserializes via reflection. R8 full mode strips generic signatures
# and TypeToken subtypes unless explicitly kept, which crashes at runtime.
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.google.gson.reflect.TypeToken { *; }
-keep,allowobfuscation class * extends com.google.gson.reflect.TypeToken

# Keep classes with @SerializedName fields together with those fields.
# This must be a direct rule (not -if): nothing else references the fields,
# so they would otherwise be shrunk away before conditional rules are
# evaluated against the residual program.
-keepclasseswithmembers,allowobfuscation class ** {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Also pin their constructors. Without an instantiation root R8 marks these
# classes abstract and Gson fails with "Abstract classes can't be
# instantiated".
-if class ** { @com.google.gson.annotations.SerializedName <fields>; }
-keep,allowobfuscation class <1> {
  <init>(...);
}

# R8 full mode strips generic signatures from Retrofit suspend-function
# return types unless these are kept.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking class retrofit2.Response
