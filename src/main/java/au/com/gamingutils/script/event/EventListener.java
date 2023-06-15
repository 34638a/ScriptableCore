package au.com.gamingutils.script.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//An annotation to mark methods that will be registered as event listeners.
//This annotation is used by the EventEngine to register event listeners.
//Valid targets are methods within a class extend the Scriptable.Component class
//and methods within a class that extends the Scriptable class
//or static methods anywhere.
//The method must have a single parameter either is or extends the Event class.
//The parameter must be the same as the value of the annotation or a class that the value extends from.


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {
	Class<? extends Event<?>> value();
}
