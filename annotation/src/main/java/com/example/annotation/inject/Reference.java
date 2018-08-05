package com.example.annotation.inject;

public class Reference<T> {
  Object mProvider;
  String mName;

  public Reference(Object provider, String fieldName) {
    mProvider = provider;
    mName = fieldName;
  }

  public T get() {
    return (T) ProviderHolder.fetch(mProvider, mName);
  }

  public void set(T value) {
    ProviderHolder.set(mProvider, mName, value);
  }
}
