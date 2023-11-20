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

package com.itsaky.androidide.treesitter.ap.utils;

import java.util.Optional;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic.Kind;

/**
 * @author Akash Yadav
 */
public class NativeMethodValidator {

  private NativeMethodValidator() {
    throw new UnsupportedOperationException();
  }

  public static Triple<Boolean, Boolean, Boolean> validateNativeMethod(TypeElement type, ExecutableElement md, Messager messager) {
    final var fastNative = getAnnotationByName(md, "dalvik.annotation.optimization.FastNative");
    final var criticalNative = getAnnotationByName(md, "dalvik.annotation.optimization.CriticalNative");

    if (fastNative.isPresent() && criticalNative.isPresent()) {
      messager.printMessage(Kind.ERROR,
        "Method '" + md.getSimpleName() + "' can be either @FastNative or @CriticalNative, but not both. In class " + type);
      return Triple.of(false, true, true);
    }

    if (criticalNative.isPresent()) {
      final var failed = Triple.of(false, false, true);
      // @CriticalNative methods must be static and must not have any ReferenceType parameters or return type
      if (!md.getModifiers().contains(Modifier.STATIC)) {
        messager.printMessage(Kind.ERROR, "@CriticalNative methods must be static. Method " + md.getSimpleName() + " in class " + type);
        return failed;
      }

      if (!md.getReturnType().getKind().isPrimitive() && md.getReturnType().getKind() != TypeKind.VOID) {
        messager.printMessage(Kind.ERROR,
          "@CriticalNative methods must have a primitive return type. Method " + md.getSimpleName() + " in class " + type);
        return failed;
      }

      for (final var p : md.getParameters()) {
        if (!p.asType().getKind().isPrimitive()) {
          messager.printMessage(Kind.ERROR,
            "@CriticalNative methods must have primitive parameter types. Method " + md.getSimpleName() + " in class " + type);
          return failed;
        }
      }
    }

    return Triple.of(true, fastNative.isPresent(), criticalNative.isPresent());
  }

  private static Optional<? extends AnnotationMirror> getAnnotationByName(ExecutableElement md, String annotation) {
    return md.getAnnotationMirrors()
      .stream()
      .filter(am -> ((TypeElement) am.getAnnotationType().asElement()).getQualifiedName()
        .contentEquals(annotation))
      .findFirst();
  }
}
