package com.example.annotation.inject;

public class NamedParam {
  public Object mParam;
  public String mName;

  public NamedParam(String name, Object param) {
    mName = name;
    mParam = param;
  }

  public static NamedParam of(String name, Object param) {
    return new NamedParam(name, param);
  }
}
