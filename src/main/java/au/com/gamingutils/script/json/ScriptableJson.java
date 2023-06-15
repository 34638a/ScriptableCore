package au.com.gamingutils.script.json;

import au.com.gamingutils.script.Scriptable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

@UtilityClass
public final class ScriptableJson {
	
	public static final class Serializer extends JsonSerializer<Scriptable<?>> {
		@Override
		public void serialize(Scriptable<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeStartObject();
			gen.writeStringField("scriptableName", value.getScriptableName());
			gen.writeBooleanField("active", value.isActive());
			
			for (var field : value.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(JsonIgnore.class)) continue;
				if (Modifier.isFinal(field.getModifiers())) continue;
				
				field.setAccessible(true);
				try {
					gen.writeObjectField(field.getName(), field.get(value));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			gen.writeArrayFieldStart("components");
			for (var component : value.getComponents()) {
				gen.writeObject(component);
			}
			gen.writeEndArray();
			gen.writeEndObject();
			
		}
	}
	
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
					Scriptable.Component<?> component = componentData.traverse(p.getCodec()).readValueAs(Scriptable.Component.class);
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
