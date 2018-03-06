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

package pt.neticle.ark.base;

import pt.neticle.ark.annotations.Controller;
import pt.neticle.ark.failsafe.ErrorHandlingController;

/**
 * A controller handler object holds descriptive data about it's controller.
 *
 * Each controller should have exactly one handler, which lasts the whole lifespan of the application.
 */
public class ControllerHandler
{
    /**
     * The owning application
     */
    private final Application parent;

    /**
     * The controller's class
     */
    private final Class<?> controllerClass;

    /**
     * The controller instance
     */
    private final Object controller;

    /**
     * The controller annotation.
     * Currently only holds the path string, but might be useful in the future.
     */
    private final Controller annotation;

    /**
     * The controller's path/route
     */
    private final String path;

    /**
     * Creates a controller handler based on the provided controller
     *
     * @param parent The owning application
     * @param controllerClass The controller's class
     * @param controller The controller's instance
     * @param annotation The controller's annotation
     */
    protected ControllerHandler (Application parent, Class<?> controllerClass, Object controller, Controller annotation)
    {
        this.parent = parent;
        this.controllerClass = controllerClass;
        this.controller = controller;
        this.annotation = annotation;
        this.path = annotation.path();
    }

    public String getPath ()
    {
        return path;
    }

    public Class<?> getControllerClass ()
    {
        return controllerClass;
    }

    public Object getControllerInstance ()
    {
        return controller;
    }

    public boolean hasOwnErrorHandlers ()
    {
        return controller instanceof ErrorHandlingController;
    }

    public ErrorHandlingController getControllerInstanceAsErrorHandling ()
    {
        return hasOwnErrorHandlers() ? (ErrorHandlingController)controller : null;
    }
}
