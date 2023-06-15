package au.com.gamingutils.script.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class JsonHelper {
	
	/**
	 * Fetch a class from a string.</br>
	 * This method is used by {@link ScriptableJson.Deserializer} and {@link ScriptableComponentJson.Deserializer} to decode scriptables, and components.
	 * @param className Class name.
	 * @return Class.
	 * @throws IOException If the class is not found.
	 */
	public static Class<?> fetchClass(String className) throws IOException {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IOException("Failed to find class: " + className, e);
		}
	}
	
	/**
	 * Decode the fields of a class from a jsonNode.</br>
	 * This will decode all fields that are not final, static, transient, or annotated with {@link JsonIgnore}.</br>
	 * This method is used by {@link ScriptableJson.Deserializer} and {@link ScriptableComponentJson.Deserializer} to decode scriptables, and components.
	 * @param p JsonParser.
	 * @param ctxt DeserializationContext.
	 * @param node JsonNode.
	 * @param target Target object.
	 * @param targetClass Target class.
	 * @param dontDecode Fields to not decode.
	 * @throws IllegalAccessException If the field is inaccessible.
	 * @throws IOException If the field is null.
	 */
	public static void decodeFields(JsonParser p, DeserializationContext ctxt, JsonNode node, Object target, Class<?> targetClass, String... dontDecode) throws IllegalAccessException, IOException {
		List<Field> fields = new ArrayList<>(List.of(targetClass.getDeclaredFields()));
		fields.removeIf(field ->
				field.isAnnotationPresent(JsonIgnore.class) ||
				Modifier.isFinal(field.getModifiers()) ||
				Modifier.isStatic(field.getModifiers()) ||
				Modifier.isTransient(field.getModifiers()) ||
				List.of(dontDecode).contains(field.getName())
		);
		for (var field : fields) {
			field.setAccessible(true);
			JsonNode fieldNode = node.get(field.getName());
			//Check if the jsonNode is null.
			if (fieldNode == null || fieldNode.isNull()) {
				//Set the field to null.
				field.set(target, null);
			} else {
				//Convert the jsonNode to the field type.
				Object data = p.getCodec().treeToValue(fieldNode, field.getType());
				field.set(target, data);
			}
		}
	}
}
