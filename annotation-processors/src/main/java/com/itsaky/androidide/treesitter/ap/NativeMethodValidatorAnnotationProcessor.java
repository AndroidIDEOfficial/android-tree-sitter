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
import com.itsaky.androidide.treesitter.annotations.GenerateNativeHeaders;
import com.itsaky.androidide.treesitter.ap.utils.NativeMethodValidator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;

/**
 * Annotation processor for {@link GenerateNativeHeaders}.
 *
 * @author Akash Yadav
 */
@SupportedAnnotationTypes(value = {
  "com.itsaky.androidide.treesitter.annotations.GenerateNativeHeaders"})
@SupportedOptions(value = {})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@IncrementalAnnotationProcessor(AGGREGATING)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class NativeMethodValidatorAnnotationProcessor extends AbstractProcessor {

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(GenerateNativeHeaders.class.getName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    final var messager = processingEnv.getMessager();
    final var elements = roundEnvironment.getElementsAnnotatedWith(GenerateNativeHeaders.class);
    for (Element element : elements) {
      if (element.getKind() != ElementKind.CLASS) {
        messager.printMessage(Kind.ERROR,
          GenerateNativeHeaders.class.getSimpleName() + " can only be applied to classes");
        continue;
      }

      final var type = ((TypeElement) element);

      for (Element e : type.getEnclosedElements()) {
        if (e.getKind() != ElementKind.METHOD) {
          continue;
        }

        final var md = ((ExecutableElement) e);
        NativeMethodValidator.validateNativeMethod(md, messager);
      }
    }
    return false;
  }

  private static void writeFileContents(String methodHeaderContents, File methodHeaders) {
    try (final var out = new FileOutputStream(methodHeaders)) {
      out.write(methodHeaderContents.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
