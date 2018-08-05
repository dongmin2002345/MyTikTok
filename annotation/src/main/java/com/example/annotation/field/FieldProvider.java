package com.example.annotation.field;

import java.util.Set;

public interface FieldProvider {
//  void init();

  <T> T get(String fieldName);

  <T> T get(Class<T> tClass);

  Set<Object> allFields();
}
