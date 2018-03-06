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

package pt.neticle.ark.data;

import java.math.BigDecimal;
import java.util.Optional;

public class DefaultConverter extends Converter
{
    public DefaultConverter ()
    {
        super();

        this.addConverter(String.class, Integer.class, (sourceStr) ->
        {
            try {
                return Optional.of(Integer.valueOf(sourceStr));
            } catch (NumberFormatException | NullPointerException e) {
                return Optional.empty();
            }
        });

        this.addConverter(String.class, Double.class, (sourceStr) ->
        {
            try {
                return Optional.of(Double.valueOf(sourceStr));
            } catch(NumberFormatException | NullPointerException e)
            {
                return Optional.empty();
            }
        });

        this.addConverter(String.class, BigDecimal.class, (sourceStr) ->
        {
            try {
                return Optional.of(new BigDecimal(sourceStr));
            } catch(NumberFormatException | NullPointerException e)
            {
                return Optional.empty();
            }
        });

        this.addConverter(String.class, Boolean.class,
            (sourceStr) -> Optional.of(sourceStr.equalsIgnoreCase("true") || sourceStr.equals("1")));
    }
}
