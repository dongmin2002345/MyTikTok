package com.example.annotation.inject;

import com.example.annotation.field.Fetchers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectProviderImpl extends ObjectProvider {
  @Override
  public final <F, T> T fetch(F obj, Class<T> tClass) {
    if (obj == null) {
      return null;
    }
    if (obj.getClass() == tClass) {
      return (T) obj;
    }
    T result = (T) Fetchers.fetcherNonNull(obj.getClass()).get(obj, tClass);
    if (result != null) {
      return result;
    }
    return null;
  }

  @Override
  public <F, T> T fetch(F obj, String fieldName) {
    if (obj == null) {
      return null;
    }
    T result = null;
    if (obj instanceof NamedParam && fieldName.equals(((NamedParam) obj).mName)) {
      return (T) ((NamedParam) obj).mParam;
    }
    if (obj instanceof Map && ((Map) obj).containsKey(fieldName)) {
      result = (T) ((Map) obj).get(fieldName);
    }
    if (result != null) {
      return result;
    }
    result = (T) Fetchers.fetcherNonNull(obj.getClass()).get(obj, fieldName);
    if (result != null) {
      return result;
    }
    return null;
  }

  @Override
  public <F, T> void set(F obj, String fieldName, T value) {
    if (obj == null) {
      return;
    }
    Fetchers.fetcherNonNull(obj.getClass()).set(obj, fieldName, value);
  }

  @Override
  public <F, T> void set(F obj, Class tClass, T value) {
    if (obj == null) {
      return;
    }
    Fetchers.fetcherNonNull(obj.getClass()).set(obj, tClass, value);
  }

  @Override
  public Set<String> allFieldNames(Object obj) {
    if (obj == null) {
      return Collections.emptySet();
    }
    if (obj instanceof NamedParam) {
      return Collections.singleton(((NamedParam) obj).mName);
    }
    if (obj instanceof Map) {
      return ((Map) obj).keySet();
    }
    return Fetchers.fetcherNonNull(obj.getClass()).allFieldNames(obj);
  }

  @Override
  public Set<Class> allTypes(Object obj) {
    if (obj == null) {
      return Collections.emptySet();
    }
    final Set<Class> result = new HashSet<>();
    result.addAll(Fetchers.fetcherNonNull(obj.getClass()).allTypes(obj));
    result.add(obj.getClass());
    return result;
  }

  @Override
  public Set<Object> allDirectFields(Object obj) {
    if (obj == null) {
      return Collections.emptySet();
    }
    return Fetchers.fetcherNonNull(obj.getClass()).allFields(obj);
  }
}
