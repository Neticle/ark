// Copyright 2018 Igor Azevedo <igor.azevedo@neticle.pt>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package pt.neticle.ark.introspection;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

import java.util.function.Consumer;

/**
 * Basically a wrapper for FastClasspathScanner
 */
public class ClassFinder
{
    private final FastClasspathScanner scanner;

    public ClassFinder (String basePackage)
    {
        scanner = new FastClasspathScanner(basePackage);
    }

    public void scan ()
    {
        scanner.scan();
    }

    public void handleClassesAnnotatedWith (Class annotation, Consumer<Class> consumer)
    {
        this.scanner.matchClassesWithAnnotation(annotation, c -> consumer.accept(c));
    }

    public <T> void handleSubclassesOf (Class<T> base, Consumer<Class<? extends T>> consumer)
    {
        this.scanner.matchSubclassesOf(base, c -> consumer.accept(c));
    }
}
