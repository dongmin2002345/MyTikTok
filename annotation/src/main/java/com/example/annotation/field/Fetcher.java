package com.example.annotation.field;

import java.util.Set;

public interface Fetcher<I> {
  Fetcher<I> init();

  <T> T get(I target, Class tClass);

  <T> T get(I target, String field);

  <T> void set(I target, String field, T value);

  <T> void set(I target, Class<T> tClass, T value);

  default <T> void set(I target, T value) {
    // 必须强转一下，否则编译不过
    if (value == null) {
      return;
    }
    set(target, (Class<T>) value.getClass(), value);
  }

  Set<Object> allFields(I target);

  Set<String> allFieldNames(I target);

  Set<Class> allTypes(I target);
}
