package com.example.annotation_processing.inject;

import com.example.annotation.inject.Injectors;
import com.example.annotation_processing.util.AptUtils;
import com.example.annotation_processing.util.ClassBuilder;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class InjectorHelperBuilder extends ClassBuilder {
  private final MethodSpec.Builder mInit;

  public InjectorHelperBuilder(String pkg, String className, AnnotationSpec generated) {
    mPackage = pkg;
    mClassName = className;
    mType = TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addAnnotation(generated);
    AnnotationSpec invokeBy = AptUtils.invokeBy(ClassName.get(Injectors.class),
        CodeBlock.of("$T.INVOKER_ID", Injectors.class));
    mInit =
        MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .addAnnotation(invokeBy);
  }

  public void onNewInjector(TypeName rootType, InjectorBuilder builder) {
    mInit.addStatement("$T.put($T.class, new $T())", Injectors.class,
        rootType, ClassName.get(builder.getPackage(), builder.getClassName()));
  }

  @Override
  public TypeSpec.Builder build() {
    return mType.addMethod(mInit.build());
  }
}
