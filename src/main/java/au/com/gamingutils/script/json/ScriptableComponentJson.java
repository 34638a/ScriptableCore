package au.com.gamingutils.script.json;

import au.com.gamingutils.script.Scriptable;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public final class ScriptableComponentJson {
	public static final class Deserializer extends JsonDeserializer<Scriptable.Component<?>> {
		@Override
		public Scriptable.Component<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
			
			JsonNode node = p.getCodec().readTree(p);
			String componentName = node.get("componentName").asText();
			Class<?> componentClass = JsonHelper.fetchClass(componentName);
			Scriptable.Component<?> component;
			try {
				component = (Scriptable.Component<?>) componentClass.getConstructor().newInstance();
			} catch (ReflectiveOperationException e) {
				throw new IOException("Failed to create component instance: " + componentName, e);
			}
			
			try {
				JsonHelper.decodeFields(p,ctxt,node,component,componentClass);
			} catch (IllegalAccessException e) {
				throw new IOException(e);
			}
			
			return component;
		}
	}
}
