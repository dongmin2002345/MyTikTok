package com.example.annotation.inject;

import java.util.Set;

public interface Injector<T> {
  void inject(T target, Object accessible);

  Set<String> allNames();

  Set<Class> allTypes();

  void reset(T target);
}
