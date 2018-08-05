package com.example.annotation_processing.invoker;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InvokeMethod implements Serializable {
  private static final long serialVersionUID = 3213094601630741209L;
  @SerializedName("class")
  public String className;
  @SerializedName("method")
  public String methodName;
}
