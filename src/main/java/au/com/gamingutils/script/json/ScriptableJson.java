package au.com.gamingutils.script.json;

import au.com.gamingutils.script.Scriptable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@UtilityClass
public final class ScriptableJson {
	
	public static final class Deserializer extends JsonDeserializer<Scriptable<?>> {
		@Override
		public Scriptable<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);
			String scriptableName = node.get("scriptableName").asText();
			
			//Get the scriptable class.
			Class<?> scriptableClass;
			try {
				scriptableClass = Class.forName(scriptableName);
			} catch (ClassNotFoundException e) {
				throw new IOException("Failed to find scriptable class: " + scriptableName, e);
			}
			
			//Create new Scriptable
			Scriptable<?> scriptable;
			try {
				//Check if the scriptableClass is a subclass of Scriptable.
				if (!Scriptable.class.isAssignableFrom(scriptableClass)) {
					throw new IOException("Deserialized Object does not extend Scriptable<?>.class. Deserializing: " + scriptableName);
				}
				scriptable = (Scriptable<?>) scriptableClass.getConstructor().newInstance();
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
			         NoSuchMethodException e) {
				throw new IOException(e);
			}
			
			//Load all variables from JSON into the scriptable.
			try {
				JsonHelper.decodeFields(p, ctxt, node, scriptable, scriptableClass);
			} catch (IllegalAccessException e) {
				throw new IOException(e);
			}
			
			var components = node.get("components");
			for (var componentData : components) {
				try {
					Scriptable.Component<?> component = p.getCodec().treeToValue(componentData, Scriptable.Component.class);
					scriptable.registerComponent(component);
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
			
			return scriptable;
		}
	}
}
