/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter.ap;

import static net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.AGGREGATING;

import com.google.auto.service.AutoService;
import com.itsaky.androidide.treesitter.annotations.DontSynchronize;
import com.itsaky.androidide.treesitter.annotations.Synchronized;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic.Kind;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;

/**
 * @author Akash Yadav
 */
@SupportedAnnotationTypes(value = {"com.itsaky.androidide.treesitter.annotations.Synchronized",
  "com.itsaky.androidide.treesitter.annotations.NotSynchronized"})
@SupportedOptions(value = {})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@IncrementalAnnotationProcessor(AGGREGATING)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class SynchronizedAnnotationProcessor extends AbstractProcessor {

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(Synchronized.class.getName(), DontSynchronize.class.getName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    final var messager = processingEnv.getMessager();
    final var elements = roundEnvironment.getElementsAnnotatedWith(Synchronized.class);
    for (Element element : elements) {
      if (element.getKind() != ElementKind.CLASS) {
        messager.printMessage(Kind.ERROR,
          Synchronized.class.getSimpleName() + " can only be applied to classes");
        continue;
      }

      if (element.getModifiers().contains(Modifier.FINAL)) {
        messager.printMessage(Kind.ERROR,
          "Class annotation with " + Synchronized.class.getSimpleName() + " cannot be final!");
        continue;
      }

      final var type = ((TypeElement) element);
      final var annotation = Objects.requireNonNull(element.getAnnotation(Synchronized.class));

      if (type.getEnclosingElement().getKind() != ElementKind.PACKAGE) {
        messager.printMessage(Kind.ERROR,
          Synchronized.class.getSimpleName() + " can only be applied to top-level classes");
        continue;
      }

      final var pkg = ((PackageElement) type.getEnclosingElement()).getQualifiedName().toString();
      final var name = type.getSimpleName().toString();
      final var simpleName = "Synchronized" + capitalize(name);
      final var qualifiedName = pkg + "." + simpleName;
      final var methods = type.getEnclosedElements()
        .stream()
        .filter(
          e -> (e.getKind() == ElementKind.METHOD || e.getKind() == ElementKind.CONSTRUCTOR) &&
            e.getAnnotation(DontSynchronize.class) == null)
        .map(e -> ((ExecutableElement) e));

      final var typeSpec = TypeSpec.classBuilder(simpleName);
      typeSpec.addModifiers(Modifier.PUBLIC);
      typeSpec.superclass(ClassName.get(type));

      if (annotation.useReentrantLock()) {
        final var field = FieldSpec.builder(ReentrantLock.class, "lock", Modifier.PRIVATE,
          Modifier.FINAL).initializer("new $T()", ReentrantLock.class);
        typeSpec.addField(field.build());
      }

      methods.forEach(method -> {
        if (method.getKind() == ElementKind.CONSTRUCTOR) {
          final var constructor = MethodSpec.constructorBuilder()
            .addParameters(
              method.getParameters().stream().map(ParameterSpec::get).collect(Collectors.toList()))
            .addAnnotations(method.getAnnotationMirrors()
              .stream()
              .map(AnnotationSpec::get)
              .collect(Collectors.toList()));

          if (annotation.packagePrivateConstructor()) {
            constructor.modifiers.clear();
          } else {
            constructor.addModifiers(method.getModifiers());
          }

          var stmt = "super(";
          stmt += method.getParameters()
            .stream()
            .map(VariableElement::getSimpleName)
            .map(Name::toString)
            .collect(Collectors.joining(", "));
          stmt += ");";

          constructor.addCode(stmt);
          typeSpec.addMethod(constructor.build());
          return;
        }

        final var methodSpec = MethodSpec.overriding(method);

        if (!annotation.useReentrantLock() &&
          !methodSpec.modifiers.contains(Modifier.SYNCHRONIZED)) {
          methodSpec.addModifiers(Modifier.SYNCHRONIZED);
        }

        var stmt = "super.$L";
        if (method.getReturnType().getKind() != TypeKind.VOID) {
          stmt = "return " + stmt;
        }
        stmt += "(";
        if (!method.getParameters().isEmpty()) {
          stmt += method.getParameters()
            .stream()
            .map(VariableElement::getSimpleName)
            .map(Name::toString)
            .collect(Collectors.joining(", "));
        }
        stmt += ")";

        final var body = CodeBlock.builder();
        if (annotation.useReentrantLock()) {
          body.addStatement("lock.lock()").beginControlFlow("try");
        }

        body.addStatement(stmt, method.getSimpleName().toString());

        if (annotation.useReentrantLock()) {
          body.nextControlFlow("finally").addStatement("lock.unlock()").endControlFlow();
        }

        methodSpec.addCode(body.build());
        typeSpec.addMethod(methodSpec.build());
      });

      final var javaFile = JavaFile.builder(pkg, typeSpec.build())
        .addFileComment("This is an automatically generated file. DO NOT EDIT!!")
        .indent("    ")
        .build();

      try {
        final var fileObject = processingEnv.getFiler().createSourceFile(qualifiedName);
        try (final var writer = fileObject.openWriter()) {
          javaFile.writeTo(writer);
          writer.flush();
        }
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }
    return true;
  }

  private String capitalize(String str) {
    if (str.length() == 0) {
      return str;
    }

    final var chars = str.toCharArray();
    final var c = chars[0];
    if (Character.isAlphabetic(c) && !Character.isUpperCase(c)) {
      chars[0] = Character.toUpperCase(c);
    } else {
      return str;
    }

    return new String(chars);
  }
}
