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

/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.tasks;

import groovy.lang.Closure;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.internal.TsConventionTask;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.internal.Factory;
import org.gradle.work.DisableCachingByDefault;

import javax.inject.Inject;
import java.util.Set;

/**
 * A {@code SourceTask} performs some operation on source files.
 */
@NonNullApi
@DisableCachingByDefault(because = "Super-class, not to be instantiated directly")
public abstract class TsSourceTask extends TsConventionTask implements PatternFilterable {
  private ConfigurableFileCollection sourceFiles = getProject().getObjects().fileCollection();
  private final PatternFilterable patternSet;

  public TsSourceTask() {
    patternSet = getPatternSetFactory().create();
  }

  @Inject
  protected Factory<PatternSet> getPatternSetFactory() {
    throw new UnsupportedOperationException();
  }

  @Internal
  protected PatternFilterable getPatternSet() {
    return patternSet;
  }

  /**
   * Returns the source for this task, after the include and exclude patterns have been applied. Ignores source files which do not exist.
   *
   * <p>
   * The {@link PathSensitivity} for the sources is configured to be {@link PathSensitivity#ABSOLUTE}.
   * If your sources are less strict, please change it accordingly by overriding this method in your subclass.
   * </p>
   *
   * @return The source.
   */
  @InputFiles
  @SkipWhenEmpty
  @IgnoreEmptyDirectories
  @PathSensitive(PathSensitivity.ABSOLUTE)
  public FileTree getSource() {
    return sourceFiles.getAsFileTree().matching(patternSet);
  }

  /**
   * Sets the source for this task.
   *
   * @param source The source.
   * @since 4.0
   */
  public void setSource(FileTree source) {
    setSource((Object) source);
  }

  /**
   * Sets the source for this task. The given source object is evaluated as per {@link org.gradle.api.Project#files(Object...)}.
   *
   * @param source The source.
   */
  public void setSource(Object source) {
    sourceFiles = getProject().getObjects().fileCollection().from(source);
  }

  /**
   * Adds some source to this task. The given source objects will be evaluated as per {@link org.gradle.api.Project#files(Object...)}.
   *
   * @param sources The source to add
   * @return this
   */
  public TsSourceTask source(Object... sources) {
    sourceFiles.from(sources);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask include(String... includes) {
    patternSet.include(includes);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask include(Iterable<String> includes) {
    patternSet.include(includes);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask include(Spec<FileTreeElement> includeSpec) {
    patternSet.include(includeSpec);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask include(Closure includeSpec) {
    patternSet.include(includeSpec);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask exclude(String... excludes) {
    patternSet.exclude(excludes);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask exclude(Iterable<String> excludes) {
    patternSet.exclude(excludes);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask exclude(Spec<FileTreeElement> excludeSpec) {
    patternSet.exclude(excludeSpec);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask exclude(Closure excludeSpec) {
    patternSet.exclude(excludeSpec);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Internal
  public Set<String> getIncludes() {
    return patternSet.getIncludes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask setIncludes(Iterable<String> includes) {
    patternSet.setIncludes(includes);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Internal
  public Set<String> getExcludes() {
    return patternSet.getExcludes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TsSourceTask setExcludes(Iterable<String> excludes) {
    patternSet.setExcludes(excludes);
    return this;
  }
}
