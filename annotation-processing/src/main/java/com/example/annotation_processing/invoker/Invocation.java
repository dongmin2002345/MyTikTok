package com.example.annotation_processing.invoker;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Invocation implements Serializable {
  private static final long serialVersionUID = -4920559937353697607L;
  @SerializedName("target")
  public InvokeMethod mTarget;
  @SerializedName("invoker")
  public InvokeMethod mInvoker;
}
