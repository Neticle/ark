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

package pt.neticle.ark.view;

import pt.neticle.ark.annotations.TemplateObject;
import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.ApplicationComponent;
import pt.neticle.ark.base.DispatchContext;

/**
 * The view template resolver is in charge of determining the correct Template object given a context, action and
 * view data.
 *
 * Typically you'll want to extract the view's specified name (which should match the template name) and the action's
 * parent controller class (so you can check if any of the available templates match that origin).
 */
public interface ViewTemplateResolver extends ApplicationComponent
{
    default void foundTemplateObject (Class<? extends Template> tClass, TemplateObject annotation) {};

    Template resolve (Class<? extends DispatchContext> contextType, ActionHandler origin, View view);
}
