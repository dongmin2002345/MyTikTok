package com.example.annotation_processing.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public final class SortedElement implements Iterable<Map.Entry<TypeElement, List<Element>>> {
  public static final Comparator<Element> COMPARATOR = new Comparator<Element>() {
    @Override
    public int compare(Element element, Element t1) {
      // toString就是获得element名
      return element.toString().compareTo(t1.toString());
    }
  };

  private final Map<TypeElement, List<Element>> mClassFieldsMapping = new HashMap<>();
  private final Set<TypeElement> mFieldSortedFlags = new HashSet<>();

  public static SortedElement fromRoundEnv(RoundEnvironment roundEnv,
      Class<? extends Annotation> annotation) {
    SortedElement sortedElement = new SortedElement();
    for (Element field : roundEnv.getElementsAnnotatedWith(annotation)) {
      Element rootClass = field.getEnclosingElement();
      if (rootClass == null || rootClass.getKind() != ElementKind.CLASS) {
        continue;
      }
      List<Element> fields = sortedElement.mClassFieldsMapping.get(rootClass);
      if (fields == null) {
        fields = new ArrayList<>();
        sortedElement.mClassFieldsMapping.put((TypeElement) rootClass, fields);
      }
      fields.add(field);
    }
    return sortedElement;
  }

  private SortedElement() {}

  @Override
  public Iterator<Map.Entry<TypeElement, List<Element>>> iterator() {
    return new Iterator<Map.Entry<TypeElement, List<Element>>>() {
      private List<TypeElement> mKeys;
      private Iterator<TypeElement> mKeyIterator;
      {
        mKeys = new ArrayList<>(mClassFieldsMapping.keySet());
        mKeys.sort(COMPARATOR);
        mKeyIterator = mKeys.iterator();
      }

      @Override
      public boolean hasNext() {
        return mKeyIterator.hasNext();
      }

      @Override
      public Map.Entry<TypeElement, List<Element>> next() {
        TypeElement rootClass = mKeyIterator.next();
        List<Element> elements = mClassFieldsMapping.get(rootClass);
        if (mFieldSortedFlags.add(rootClass)) {
          elements.sort(COMPARATOR);
        }
        return new HashMap.SimpleEntry<>(rootClass, elements);
      }
    };
  }
}
