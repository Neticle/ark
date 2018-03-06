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

import pt.neticle.ark.data.output.Output;

import java.util.Map;

/**
 * A view object is essentially a container. It doesn't render anything, it just carries information necessary to
 * generate output.
 *
 * The name refers to the name of the template we're using. The data is just a map of key/values to be passed to the
 * template.
 *
 * Templates are resolved not just based on name but also on origin of the view.
 */
public interface View extends Output<View>
{
    String getName ();

    Map<String,Object> getData ();
}
