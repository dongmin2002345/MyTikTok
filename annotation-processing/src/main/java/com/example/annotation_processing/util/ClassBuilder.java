package com.example.annotation_processing.util;

import com.squareup.javapoet.TypeSpec;

/**
 * 这一层是没有任何跟 apt 相关的元素的，理论上可以复用到任何解析场景下，是输出用的 model.
 * 需要初始化三个 Field，因为 super 构造函数调用只能是第一行，所以没办法用代码固定这一点.
 */
public abstract class ClassBuilder {
  protected String mClassName;
  protected String mPackage;
  protected TypeSpec.Builder mType;

  public final String getClassName() {
    return mClassName;
  }

  public final String getPackage() {
    return mPackage;
  }

  public abstract TypeSpec.Builder build();
}
