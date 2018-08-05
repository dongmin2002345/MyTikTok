package com.example.annotation.inject;

import java.util.Set;

public final class ProviderHolder {
  private static ObjectProvider sObjectProvider;

  public static void setProvider(ObjectProvider objectProvider) {
    sObjectProvider = objectProvider;
  }

  public static <F, T> T fetch(F target, Class<T> tClass) {
    return sObjectProvider.fetch(target, tClass);
  }

  public static <F, T> T fetch(F target, String fieldName) {
    return sObjectProvider.fetch(target, fieldName);
  }

  public static <F> boolean have(F obj, String fieldName) {
    return sObjectProvider.have(obj, fieldName);
  }

  public static <F> boolean have(F obj, Class tClass) {
    return sObjectProvider.have(obj, tClass);
  }

  public static <F, T> void set(F obj, String fieldName, T value) {
    sObjectProvider.set(obj, fieldName, value);
  }

  public static <F, T> void set(F obj, Class tClass, T value) {
    sObjectProvider.set(obj, tClass, value);
  }

  public static Set<String> allFieldNames(Object obj) {
    return sObjectProvider.allFieldNames(obj);
  }

  public static Set<Class> allTypes(Object obj) {
    return sObjectProvider.allTypes(obj);
  }

  public static Set<Object> allFields(Object obj) {
    return sObjectProvider.allDirectFields(obj);
  }
}
