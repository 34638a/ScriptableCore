package au.com.gamingutils.script;

import au.com.gamingutils.script.exception.ComponentBindingException;
import au.com.gamingutils.script.json.ScriptableComponentJson;
import au.com.gamingutils.script.json.ScriptableJson;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@JsonDeserialize(using = ScriptableJson.Deserializer.class)
public abstract class Scriptable<S extends Scriptable<?>> {
	
	@Getter
	private final String scriptableName = this.getClass().getName();
	private final List<Component<?>> components = new ArrayList<>();
	@Getter
	private boolean active = true;
	
	public Scriptable() {
		LoadedScriptables.scriptables.add(this);
	}
	
	public S destroy() {
		LoadedScriptables.scriptables.remove(this);
		return (S) this;
	}
	
	/**
	 * Enable the scriptable.
	 * @return this.
	 */
	public S enable() {
		active = true;
		return (S) this;
	}
	
	/**
	 * Disable the scriptable.
	 * @return this.
	 */
	public S disable() {
		active = false;
		return (S) this;
	}
	
	/**
	 * Register a component to the scriptable.
	 * @param component Component to register.
	 * @return this.
	 */
	public S registerComponent(@NonNull Component<?> component) {
		if (component.hasHost()) throw new ComponentBindingException(component.getComponentName());
		components.add(component);
		component.host = this;
		component.onRegister();
		return (S) this;
	}
	
	/**
	 * Unregister a component from the scriptable.
	 * @param component Component to unregister.
	 * @return this.
	 */
	public S unregisterComponent(@NonNull Component<?> component) {
		if (component.hasHost()) {
			components.remove(component);
			component.onUnregister();
			component.host = null;
		}
		return (S) this;
	}
	
	/**
	 * Get a list of all the components.
	 * @return List of components.
	 */
	public List<Component<?>> getComponents() {
		return new ArrayList<>(components);
	}
	
	/**
	 * Get a list of all the active components.
	 * @return List of active components.
	 */
	@JsonIgnore
	public List<Component<?>> getActiveComponents() {
		List<Component<?>> activeComponents = new ArrayList<>();
		for (Component<?> component : components) {
			if (component.isActive()) activeComponents.add(component);
		}
		return activeComponents;
	}
	
	/**
	 * An abstract representation of a component that can be bound to a scriptable.
	 * Components are used to extend the functionality of a scriptable.
	 *
	 * @param <C> The component type.
	 */
	@EqualsAndHashCode
	@JsonDeserialize(using = ScriptableComponentJson.Deserializer.class)
	public abstract static class Component<C extends Component<?>> {
		@Getter
		@JsonIgnore
		@EqualsAndHashCode.Exclude
		private Scriptable<?> host;
		@Getter
		private final String componentName = this.getClass().getName();
		@Getter
		private boolean active = true;
		
		/**
		 * Enable the component.
		 * @return this.
		 */
		public final C enable() {
			this.active = true;
			return (C)this;
		}
		
		/**
		 * Disable the component.
		 * @return this.
		 */
		public final C disable() {
			this.active = false;
			return (C)this;
		}
		
		/**
		 * Check if the component is bound to a scriptable.
		 * @return true if the component is bound to a scriptable.
		 */
		public final boolean hasHost() {
			return this.host != null;
		}
		
		/**
		 * Called when the component is registered to a scriptable.
		 */
		protected void onRegister() {}
		
		/**
		 * Called when the component is unregistered from a scriptable.
		 */
		protected void onUnregister() {}
	}
}
