package au.com.gamingutils.script.event;

import au.com.gamingutils.script.Scriptable;
import au.com.gamingutils.script.LoadedScriptables;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

@Log
@UtilityClass
public class EventEngine {
	
	private static final HashMap<Class<? extends Event<?>>, List<Method>> staticEventListeners = new HashMap<>();
	private static final HashMap<Class<? extends Event<?>>, List<CallbackReference>> scriptableEventListeners = new HashMap<>();
	private static final HashMap<Class<? extends Event<?>>, List<CallbackReference>> scriptableComponentsEventListeners = new HashMap<>();
	
	private static final List<Event<?>> eventQueue = new ArrayList<>();
	
	static {
		
		//Load all the event listeners and cache.
		
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.forPackages("")
				.addScanners(Scanners.MethodsAnnotated)
		);
		log.log(java.util.logging.Level.INFO, reflections.getMethodsAnnotatedWith(EventListener.class).size() + " event listeners found.");
		reflections.getMethodsAnnotatedWith(EventListener.class).stream()
				.filter(method -> method.getParameterCount() == 1)
				.filter(method -> Event.class.isAssignableFrom(method.getParameterTypes()[0]))
				.filter(method -> method.getParameterTypes()[0].isAssignableFrom(method.getAnnotation(EventListener.class).value()))
				.filter(method -> java.lang.reflect.Modifier.isStatic(method.getModifiers()) || (Scriptable.Component.class.isAssignableFrom(method.getDeclaringClass()) || Scriptable.class.isAssignableFrom(method.getDeclaringClass())))
				.forEach(method -> {
					Class<? extends Event<?>> eventClass = (Class<? extends Event<?>>) method.getParameterTypes()[0].asSubclass(Event.class);
					if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
						staticEventListeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(method);
					} else if (Scriptable.Component.class.isAssignableFrom(method.getDeclaringClass())) {
						scriptableComponentsEventListeners
								.computeIfAbsent(eventClass, k -> new ArrayList<>())
								.add(new CallbackReference(method.getDeclaringClass().asSubclass(Scriptable.Component.class), method));
					} else if (Scriptable.class.isAssignableFrom(method.getDeclaringClass())) {
						scriptableEventListeners
								.computeIfAbsent(eventClass, k -> new ArrayList<>())
								.add(new CallbackReference(method.getDeclaringClass().asSubclass(Scriptable.class), method));
					} else {
						log.log(Level.WARNING, "Found event listener that is not static, scriptable or scriptable component: " + method.getDeclaringClass().getName() + "." + method.getName());
					}
				});
		log.log(java.util.logging.Level.INFO, "Loaded " + staticEventListeners.values().stream().mapToLong(Collection::size).sum() + " static event listeners.");
		log.log(java.util.logging.Level.INFO, "Loaded " + scriptableEventListeners.values().stream().mapToLong(Collection::size).sum() + " scriptable event listeners.");
		log.log(java.util.logging.Level.INFO, "Loaded " + scriptableComponentsEventListeners.values().stream().mapToLong(Collection::size).sum() + " scriptable component event listeners.");
	}
	
	static void scheduleEvent(@NonNull Event<?> event) {
		synchronized (eventQueue) {
			eventQueue.add(event);
		}
	}
	
	public static void processEventQueue() {
		List<Event<?>> queue;
		synchronized (eventQueue) {
			queue = new ArrayList<>(eventQueue);
			eventQueue.clear();
		}
		queue.forEach(EventEngine::dispatchEvent);
	}
	
	static void dispatchEvent(@NonNull Event<?> event) {
		List<Method> methods = staticEventListeners.get(event.getClass());
		if (methods != null) {
			for (Method method : methods) {
				try {
					method.invoke(null, event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		List<CallbackReference> scriptableCallbacks = scriptableEventListeners.get(event.getClass());
		if (scriptableCallbacks != null) {
			CallbackList scriptableCallbackList = new CallbackList(scriptableCallbacks);
			List<Scriptable<?>> activeScriptables = LoadedScriptables.getActiveScriptables();
			activeScriptables.stream()
					.filter(scriptable -> scriptableCallbackList.contains(scriptable.getClass()))
					.forEach(scriptable -> {
						scriptableCallbacks.stream()
								.filter(callback -> callback.type().isInstance(scriptable))
								.forEach(callback -> callback.invoke(scriptable, event));
						}
					);
		}
		
		List<CallbackReference> scriptableComponentCallbacks = scriptableComponentsEventListeners.get(event.getClass());
		if (scriptableCallbacks != null) {
			CallbackList scriptableComponentCallbackList = new CallbackList(scriptableComponentCallbacks);
			LoadedScriptables.getActiveScriptables().stream()
					.flatMap(scriptable -> scriptable.getActiveComponents().stream())
					.filter(scriptableComponent -> scriptableComponentCallbackList.contains(scriptableComponent.getClass()))
					.forEach(scriptableComponent -> scriptableComponentCallbacks.stream()
							.filter(callback -> callback.type().isInstance(scriptableComponent))
							.forEach(callback -> callback.invoke(scriptableComponent, event)));
		}
	}
	
	
	private class CallbackList {
		private List<? extends Class<?>> callbacks;
		
		CallbackList(List<CallbackReference> callbacks) {
			this.callbacks = callbacks.stream()
					.map(CallbackReference::type)
					.toList();
		}
		
		boolean contains(Class<?> type) {
			return callbacks.contains(type);
		}
	}
	
	private record CallbackReference(Class<?> type, Method method) {
		void invoke(Object obj, Event<?> event) {
			try {
				method.invoke(obj, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
