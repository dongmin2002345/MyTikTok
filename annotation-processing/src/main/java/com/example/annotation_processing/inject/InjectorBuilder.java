package com.example.annotation_processing.inject;

import com.example.annotation.inject.Injector;
import com.example.annotation.inject.ProviderHolder;
import com.example.annotation.inject.Reference;
import com.example.annotation_processing.util.ClassBuilder;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

public class InjectorBuilder extends ClassBuilder {
  private static final String sClassSuffix = "Injector";
  private static final String sFieldInjectNames = "mInjectNames";
  private static final String sFieldInjectTypes = "mInjectTypes";
  private static final String sParamTarget = "target";
  private static final String sParamAccessible = "accessible";

  private final MethodSpec.Builder mConstructor;
  private final MethodSpec.Builder mAllFields;
  private final MethodSpec.Builder mAllTypes;
  private final MethodSpec.Builder mInject;
  private final MethodSpec.Builder mReset;

  public InjectorBuilder(String pkg, String className, AnnotationSpec generated,
      TypeName rootType) {
    mPackage = pkg;
    mClassName = className.replace("$", "_") + sClassSuffix;
    TypeName fieldNamesType =
        ParameterizedTypeName.get(ClassName.get(Set.class), TypeName.get(String.class));
    TypeName fieldTypesType =
        ParameterizedTypeName.get(ClassName.get(Set.class), TypeName.get(Class.class));
    mType = TypeSpec.classBuilder(mClassName)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addAnnotation(generated)
        .addField(fieldNamesType, sFieldInjectNames, Modifier.PRIVATE, Modifier.FINAL)
        .addField(fieldTypesType, sFieldInjectTypes, Modifier.PRIVATE, Modifier.FINAL)
        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Injector.class),
            rootType));
    mConstructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$L = new $T<$T>()", sFieldInjectNames, HashSet.class, String.class)
        .addStatement("$L = new $T<$T>()", sFieldInjectTypes, HashSet.class, Class.class);
    mAllFields = MethodSpec.methodBuilder("allNames")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addStatement("return $L", sFieldInjectNames)
        .returns(fieldNamesType);
    mAllTypes = MethodSpec.methodBuilder("allTypes")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addStatement("return $L", sFieldInjectTypes)
        .returns(fieldTypesType);
    mInject = MethodSpec.methodBuilder("inject")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addParameter(rootType, sParamTarget)
        .addParameter(Object.class, sParamAccessible);
    mReset = MethodSpec.methodBuilder("reset")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addParameter(rootType, sParamTarget);
  }

  public void onByTypeField(String fieldName, boolean crashNoFound, boolean acceptNull,
      TypeName fieldType) {
    if (crashNoFound) {
      mConstructor.addStatement("$L.add($T.class)", sFieldInjectTypes, fieldType);
    }
    if (acceptNull) {
      mInject.beginControlFlow("if ($T.have($L, $T.class))", ProviderHolder.class, sParamAccessible,
          fieldType);
      mInject.addStatement("Object valFor$L = $T.fetch($L, $T.class)", fieldName,
          ProviderHolder.class, sParamAccessible, fieldType);
      mInject.addStatement("$L.$L = ($T)valFor$L", sParamTarget, fieldName, fieldType, fieldName);
      mInject.endControlFlow();
    } else {
      mInject.addStatement("Object valFor$L = $T.fetch($L, $T.class)", fieldName,
          ProviderHolder.class, sParamAccessible, fieldType);
      mInject.beginControlFlow("if (valFor$L != null)", fieldName)
          .addStatement("$L.$L = ($T)valFor$L", sParamTarget, fieldName, fieldType, fieldName)
          .endControlFlow();
    }
    addToReset(fieldName, fieldType);
  }

  public void onByNameField(String fieldName, boolean crashNoFound, boolean acceptNull,
      TypeName fieldType, String fieldKey, boolean isReference) {
    if (crashNoFound) {
      mConstructor.addStatement("$L.add($S)", sFieldInjectNames, fieldKey);
    }
    if (isReference) {
      mInject.beginControlFlow("if ($T.have($L, $S))", ProviderHolder.class, sParamAccessible,
          fieldKey);
      mInject.addStatement("$L.$L = new $T<>($L, $S)", sParamTarget, fieldName,
          ClassName.get(Reference.class), sParamAccessible, fieldKey);
      mInject.endControlFlow();
    } else {
      if (acceptNull) {
        mInject.beginControlFlow("if ($T.have($L, $S))", ProviderHolder.class, sParamAccessible,
            fieldKey);
        mInject.addStatement("Object valFor$L = $T.fetch($L, $S)", fieldName,
            ProviderHolder.class, sParamAccessible, fieldKey);
        mInject.addStatement("$L.$L = ($T)valFor$L", sParamTarget, fieldName, fieldType, fieldName);
        mInject.endControlFlow();
      } else {
        mInject.addStatement("Object valFor$L = $T.fetch($L, $S)", fieldName,
            ProviderHolder.class, sParamAccessible, fieldKey);
        mInject.beginControlFlow("if (valFor$L != null)", fieldName)
            .addStatement("$L.$L = ($T)valFor$L", sParamTarget, fieldName, fieldType, fieldName)
            .endControlFlow();
      }
    }
    addToReset(fieldName, fieldType);
  }

  private void addToReset(String fieldName, TypeName fieldType) {
    String resetValue;
    if (fieldType.isPrimitive() || fieldType.isBoxedPrimitive()) {
      fieldType = fieldType.unbox();
    }
    if (fieldType.equals(TypeName.INT)
        || fieldType.equals(TypeName.SHORT)
        || fieldType.equals(TypeName.BYTE)) {
      resetValue = "0";
    } else if (fieldType.equals(TypeName.LONG)) {
      resetValue = "0L";
    } else if (fieldType.equals(TypeName.FLOAT)) {
      resetValue = "0.0f";
    } else if (fieldType.equals(TypeName.DOUBLE)) {
      resetValue = "0.0";
    } else if (fieldType.equals(TypeName.CHAR)) {
      resetValue = "java.lang.Character.MIN_VALUE";
    } else if (fieldType.equals(TypeName.BOOLEAN)) {
      resetValue = "false";
    } else {
      resetValue = "null";
    }

    mReset.addStatement("$L.$L = $L", sParamTarget, fieldName, resetValue);
  }

  @Override
  public TypeSpec.Builder build() {
    return mType
        .addMethod(mConstructor.build())
        .addMethod(mAllFields.build())
        .addMethod(mAllTypes.build())
        .addMethod(mInject.build())
        .addMethod(mReset.build());
  }
}
