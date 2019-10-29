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

package pt.neticle.ark.annotations;

import pt.neticle.ark.http.HttpRequest;

import java.lang.annotation.*;

/**
 * Denotes that the method annotated is an action and thus is available to be requested by clients.
 *
 * It is required that the owning class is also annotated with {@link pt.neticle.ark.annotations.Controller}, otherwise
 * the action won't get picked up.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action
{
    String path () default "";

    HttpRequest.Method[] methods () default {};
}
