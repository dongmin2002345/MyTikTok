package com.example.annotation_processing.inject;

import com.example.annotation.inject.Inject;
import com.example.annotation.inject.Reference;
import com.example.annotation_processing.util.BaseProcessor;
import com.example.annotation_processing.util.SortedElement;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.example.annotation_processing.inject.InjectorProcessor.CLASS_NAME;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.example.annotation.inject.Inject"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(CLASS_NAME)
public class InjectorProcessor extends BaseProcessor {
  public static final String CLASS_NAME = "injectorInterfaceName";
  private boolean mHasProcessed;
  private String mPackage;
  private String mClassName;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    String fullName = processingEnv.getOptions().get(CLASS_NAME);
    if (fullName == null) {
      mHasProcessed = true;
      return;
    }
    int lastDot = fullName.lastIndexOf('.');
    mClassName = fullName.substring(lastDot + 1);
    mPackage = fullName.substring(0, lastDot);
    mMessager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
    if (mHasProcessed) {
      return false;
    }
    InjectorHelperBuilder helperBuilder =
        new InjectorHelperBuilder(mPackage, mClassName, mGeneratedAnnotation);
    SortedElement sortedElement = SortedElement.fromRoundEnv(roundEnv, Inject.class);
    for (Map.Entry<TypeElement, List<Element>> type : sortedElement) {
      List<Element> fields = type.getValue();
      if (fields == null) {
        continue;
      }
      InjectorBuilder injectorBuilder =
          generateForClass(mGeneratedAnnotation, type.getKey(), fields);
      if (injectorBuilder == null) {
        continue;
      }
      writeClass(injectorBuilder);
      helperBuilder.onNewInjector(TypeName.get(mTypes.erasure(type.getKey().asType())),
          injectorBuilder);
    }
    writeClass(helperBuilder);
    mHasProcessed = true;
    return false;
  }

  private InjectorBuilder generateForClass(AnnotationSpec generated,
      Element rootClass, List<Element> fields) {
    if (rootClass == null || rootClass.getKind() != ElementKind.CLASS) {
      return null;
    }
    String pkg = mUtils.getPackageOf(rootClass).toString();
    InjectorBuilder builder =
        new InjectorBuilder(pkg, rootClass.getSimpleName().toString(), generated,
            TypeName.get(rootClass.asType()));
    for (Element field : fields) {
      if (field.getKind() != ElementKind.FIELD || field.getModifiers().contains(Modifier.STATIC)) {
        continue;
      }
      Inject injectAnnotation = field.getAnnotation(Inject.class);
      if (injectAnnotation == null) {
        continue;
      }
      String fieldKey = injectAnnotation.value();
      String fieldName = field.getSimpleName().toString();
      TypeMirror fieldType = mTypes.erasure(field.asType());
      if ("".equals(fieldKey)) {
        builder.onByTypeField(fieldName, injectAnnotation.crashNoFound(),
            injectAnnotation.acceptNull(), TypeName.get(fieldType));
      } else {
        builder.onByNameField(fieldName, injectAnnotation.crashNoFound(),
            injectAnnotation.acceptNull(), TypeName.get(fieldType), fieldKey,
            mTypes.isSameType(fieldType,
                mTypes.erasure(mUtils.getTypeElement(Reference.class.getName()).asType())));
      }
    }
    return builder;
  }
}
