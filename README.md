# Introduction

Ark is a Java application framework that specializes in the creation of web applications. It aims to 
be a good resource for building stable and performant solutions without compromising productivity.

## Current Status

At the moment the project is in it's very early stages and being actively developed. As such, we 
encourage anyone to take a look and even contribute, but advise caution if you intend to 
make use of this project for production purposes, not only because it's currently in a very incomplete 
and possibly unstable stage, but also because the API might change over time.

# Quick Start and Project Overview

Ark adheres to the Model-View-Controller pattern, and as such, in order to expose functionality to 
the outside world you need to define at least one Controller that will contain one or more Actions 
that will be executed according to what your application clients request.

## Code

In Ark you define these two components by using the annotations `@Controller` and `@Action`.

Usually you want your controllers to be isolated into separate classes, but for the sake of the simpleness 
of having our example in just one file, and because Ark considers any class annotated as `@Controller` with 
any public methods annotated as `@Action` to be callable, we'll define a few Actions into the application class itself.

```java
@Controller(path = "/")
public class ExampleApp extends WebApplication
{
    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @Action
    public Output<PlainText> Hello ()
    {
        return PlainText.buffered("Hello Ark!");
    }

    @Action
    public void Add (Input<String> key, OptionalInput<String> value)
    {
        this.storage.put(key.get(), value.orElse("unspecified"));
    }

    @Action(path = ":key/read")
    public void Read (Input<String> key)
    {
        // Parameters can be passed as part of the path, they automatically match based on name.
        
        return PlainText.buffered(key.get() + " is: " + this.storage.getOrDefault(key, "not present"));
    }

    public static void main (String[] args)
    {
        new DefaultNettyServer(8888)
                .serve(new Test());
    }
}
```

## Testing

You can now access your application with your web browser.

* [http://localhost:8888/hello]() will display "Hello Ark"
* [http://localhost:8888/add?key=foo&value=bar]() will add an entry (no response)
* [http://localhost:8888/foo/read]() will display the entry we added ("foo")
* [http://localhost:8888/add]() will display a "bad request" error - because it's missing one required input

## Action Input Parameters

As you've seen, parameters from the request's query string are automatically injected into the method's 
argument list, as long as data with a corresponding name and type was provided.

You may use the following types to match request data:

* `Input<T>` for simple parameters
* `OptionalInput<T>` for optional parameters. The request won't automatically halt if those are not present in the 
request. You are then free to handle how you deal with presence or absence of data. 
You can use the `OptionalInput#orElse` method to specify a default value and you can use the 
`OptionalInput#orElseThrow` to provide an exception for when the value is absent. Other methods are 
available and are similar to those found on java's own `Optional` class.
* `InputList<T>` groups parameters of the same name and that end in `[]` into the list. For instance, 
the following query string - `example[]=10&example[]=20` would populate the `example` parameter with 
two entries. If no matching parameters are present on the request, the list is passed as empty.
* `InputMap<K,V>` similar to `InputList` but for associative data. Use `example[foo]=bar` to assign 
data. If no matching parameters are present on the request, the map is passed as empty.

Ark contains by default data conversion policies to convert text from the request into the following 
types: `Integer`, `Double`, `BigDecimal`, `Boolean` - which means that, as well as `String`, these all can be 
used as type parameters for the input objects listed above.

You are not restricted to these types, as you can add your own data conversion policies to the application.

It is also possible to manually extract data from the request object by adding a parameter of type `HttpRequest` to 
the method's argument list and using the provided object to query information.

### Planned Input Parameter Types

At this initial stage, not all handlers have been implemented yet. We plan to include the 
following features as well:

* `UrlEncodedBody` to automatically match and decode data of the `application/x-www-form-urlencoded` format 
present in the request's body. (Typically used on HTTP POST requests when submitting forms)
* `JsonEncodedBody` similar to the previous, but for JSON data.
* Support passing object types such as models, to the Input classes, so that we can declare something like 
this: `Input<PersonModel>` and have it automatically bind the model's fields.

As of right now, it is possible to deal with these use-cases, but one must use the HttpRequest object to extract
the data.

## Other Injectable Objects

From the examples we can see that various input types can be injected, as well as the request and response objects, but 
our framework provides the tools to extend this functionality to inject any objects you want.

This opens up possibilities for various use-cases that might come in handy, for example injecting the current session's user:

```java
    @Action
    public void ReadSomething (ActiveUser activeUser)
    {
        // This action will only be called if we were able to resolve an active user from session data.
        // This means that for anyone that isn't logged in, this action won't even be executed because it 
        // doesn't meet the necessary criteria
        
        // Additionally, this eliminates the need to check session data against the database for every 
        // single request made. As the checks are only run when the targeted action requires it.
    }
```

One way to provide a custom injector for such functionalities is to extend your application's constructor, adding the following code:

```java
    public ExampleApp ()
    {
        super();

        context().addPolicy(new InlineInjectionPolicy<ActiveUser>
        (
            ActiveUser.class,

            // This means once we fetch the object, it will stay cached during the request's
            // lifespan, should we need to inject it somewhere else. If you use InjectionPolicy.ObjectLifespan.DISPOSABLE
            // instead, the injector get's called everytime it's needed.
            InjectionPolicy.ObjectLifespan.RETAINED,

            (requestingContext, name, typeData) ->
            {
                HttpDispatchContext context = Cast.attempt(HttpDispatchContext.class, requestingContext)
                    .orElseThrow(() -> new InputException.PreconditionFailed("This action requires access through HTTP"));
                    // we need an http context in order to fetch the session cookies, so if we got
                    // a Cli Context instead, we need to halt.


                String sessionId = context.getRequest().cookies()
                    .filter((c) -> c.getName() == "MY_SESSION_COOKIE_NAME")
                    .map((c) -> c.getValue())
                    .findFirst()
                    .orElseThrow(() -> new InputException.UnauthorizedAccess());

                /* Fetch session information from storage and validate it here */

                /* Throw an UnauthorizedAccess exception if session is invalid */

                ActiveUser user = /* fetch user record if session is valid */;

                return user;
            },

            // A clean-up function can also be provided. It gets called once the context is destroyed.
            // In this case we wouldn't need one.
            /*(context, user) ->
            {
            }*/
        ));
    }
```

This is an example of a simple handler declared with the help of `InlineInjectionPolicy` class, but if you have more complex needs you can extend either the `InlineInjectionPolicy` or it's base class `InjectionPolicy`.

Do note that this policy is being added to the application's context and not to the dispatch context. This is because whenever a context needs to inject something, it attempts to grab a suitable injector from it's _parent_ context.

The application context also has a parent context, which is called the main context. The main context has policies for injecting necessary components of the application, such as the router, IO converter and view template resolver. This separation makes these components impossible to inject from within a dispatch context, but possible to inject from the application context.

## Action Output

### Response Object Manipulation vs Return

There are two ways one can output results from within an action:

* Actions can return an Output<T> object that describes and contains the content being returned to the client.
* Actions can have the HttpResponse object injected into it's arguments and this object can be used to send data.

We advise you to follow the first option out of these two unless you are doing something really specific that requires manipulating the response's byte buffer directly, and here's why:

* By declaring the return type of an action, the framework knows what content type to expect from it, and thus, in case 
of an exception that halts the action, the fallback routines can appropriately handle the error and respond according 
to the formats of data your client is expecting. For instance, if you're building a web service action and your clients expect responses to come as JSON formatted data, the error handling code automatically knows to display an error message as JSON.
* The output objects carry meta-data such as the content-type and charset/encoding used. All this is automatically 
handled behind the scenes, including the manipulation of the response object with any necessary http headers.

If you want your action to be able to return multiple types of content, you can declare the return type as `Content<?>`, as shown bellow. Error handling won't be as efficient but you'll still benefit from everything else.

```java
    @Action
    public Output<?> Example (Input<Boolean> sendHtml)
    {
        return sendHtml.get() ? 
            Html.buffered("<h1>This is HTML content</h1>") : 
            PlainText.buffered("# This is plain text");
    }
```

### Existing Output Types

* `PlainText`
* `Html`
* `View`

### Planned Output Types

* `Json`
* `Xml`
* `StoredFile`

## Where's the "Model" and "View"? What's missing?

At the moment we haven't completed the implementation of those components. As stated before, this project 
is in very early stages and thus there's quite a bit of features missing, not including model and views.

As of right now, we're working on stabilizing the core components of the framework. Once that's out of the way, we can move on to the data model API and, once that is completed, the more complex Input Parameter Types mentioned above.

As for the view components, although those are already present in the source code, they are not documented because we're still actively working on them and it's very likely the final implementation won't look like it does now.
