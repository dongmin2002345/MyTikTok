package com.example.annotation.inject;

import com.example.annotation.invoker.ForInvoker;
import com.google.common.base.Optional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

public class Injectors {
  public static final String INVOKER_ID = "Injectors";
  private static final Injector NOOP = new Injector() {
    @Override
    public void inject(Object target, Object accessible) {}

    @Override
    public Set<String> allNames() {
      return Collections.emptySet();
    }

    @Override
    public Set<Class> allTypes() {
      return Collections.emptySet();
    }

    @Override
    public void reset(Object target) {

    }
  };
  private static final Map<Class, Injector> sInjectors = new HashMap<>();

  public static void putAll(Map<Class, Injector> map) {
    sInjectors.putAll(map);
  }

  public static void put(Class clazz, Injector injector) {
    sInjectors.put(clazz, injector);
  }

  @Nonnull
  public static Injector injector(Class clazz) {
    return Optional.fromNullable(sInjectors.get(clazz)).or(NOOP);
  }

  @ForInvoker(methodId = INVOKER_ID)
  public static void init(){
  }
}
