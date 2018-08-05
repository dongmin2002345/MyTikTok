package com.example.annotation.inject;

import java.util.Set;

public abstract class ObjectProvider {
  public abstract <F, T> T fetch(F obj, String fieldName);

  public abstract <F, T> T fetch(F obj, Class<T> tClass);

  public abstract <F, T> void set(F obj, String fieldName, T value);

  public abstract <F, T> void set(F obj, Class tClass, T value);

  public abstract Set<String> allFieldNames(Object obj);

  public abstract Set<Class> allTypes(Object obj);

  public final <F> boolean have(F obj, String fieldName) {
    Set<String> names = allFieldNames(obj);
    return names != null && names.contains(fieldName);
  }

  public final <F> boolean have(F obj, Class tClass) {
    Set<Class> types = allTypes(obj);
    return types != null && types.contains(tClass);
  }

  public abstract Set<Object> allDirectFields(Object obj);
}
