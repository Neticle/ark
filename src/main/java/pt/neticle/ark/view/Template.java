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

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.output.ContentOutput;

/**
 * A template contains the logic necessary to render a piece of content, given a set of input data.
 *
 * Template objects are meant to be initialized once and kept through-out the application's life-cycle,
 * so each template object will fulfill multiple responses and thus should not store any context-related
 * data on it's instance.
 */
public interface Template
{
    ContentOutput<?> render (DispatchContext context, ActionHandler origin, View view);
}
