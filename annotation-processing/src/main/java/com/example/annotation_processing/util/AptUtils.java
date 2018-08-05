package com.example.annotation_processing.util;

import com.example.annotation.StandFor;
import com.example.annotation.invoker.InvokeBy;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class AptUtils {
  public static String nameFrom(Element element) {
    return element.getSimpleName().toString();
  }

  public static String packageFrom(Elements elements, Element element) {
    return elements.getPackageOf(element).toString();
  }

  public static boolean isEmpty(String string) {
    return string == null || string.isEmpty();
  }

  public static boolean isEmpty(Object[] array) {
    return array == null || array.length == 0;
  }

  public static TypeMirror fromClass(Elements elements, Class clazz) {
    return elements.getTypeElement(clazz.getCanonicalName()).asType();
  }

  public static TypeMirror getInterface(TypeElement rootClass) {
    List<? extends TypeMirror> interfaces = ((TypeElement) rootClass).getInterfaces();
    return interfaces.size() == 1 ? interfaces.get(0) : null;
  }

  public static Element resovleStandFor(Element element, Elements elements) {
    StandFor standFor = element.getAnnotation(StandFor.class);
    if (standFor == null) {
      return element;
    }
    String className = standFor.forName();
    if (isEmpty(className)) {
      try {
        standFor.forClass();
      } catch (MirroredTypeException e) {
        className = e.getTypeMirror().toString();
      }
    }
    return elements.getTypeElement(className);
  }

  public static AnnotationSpec invokeBy(ClassName invokerClass, CodeBlock methodId) {
    return AnnotationSpec.builder(InvokeBy.class)
        .addMember("invokerClass", "$T.class", invokerClass)
        .addMember("methodId", "$L", methodId)
        .build();
  }
}
