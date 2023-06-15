package au.com.gamingutils.script;

import au.com.gamingutils.objtests.TestComponent;
import au.com.gamingutils.objtests.TestEvent;
import au.com.gamingutils.objtests.TestScriptable;
import au.com.gamingutils.script.event.EventEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScriptableTest {
	
	private static String testEncodedObject = "{\"scriptableName\":\"au.com.gamingutils.objtests.TestScriptable\",\"components\":[{\"componentName\":\"au.com.gamingutils.objtests.TestComponent\",\"active\":true,\"testComponentString\":\"TestComponentString\"}],\"active\":true,\"testObject1\":{\"boolValue\":false,\"byteValue\":0,\"shortValue\":0,\"intValue\":0,\"longValue\":0,\"floatValue\":0.0,\"doubleValue\":0.0,\"charValue\":\"a\",\"stringValue\":\"Test String\"},\"testObject2\":{\"boolValue\":false,\"byteValue\":0,\"shortValue\":0,\"intValue\":0,\"longValue\":0,\"floatValue\":0.0,\"doubleValue\":0.0,\"charValue\":\"a\",\"stringValue\":\"Test String\"},\"testObject3\":{\"boolValue\":false,\"byteValue\":0,\"shortValue\":0,\"intValue\":0,\"longValue\":0,\"floatValue\":0.0,\"doubleValue\":0.0,\"charValue\":\"a\",\"stringValue\":\"Test String\"},\"testObject4\":null,\"testObject5\":{\"boolValue\":false,\"byteValue\":0,\"shortValue\":0,\"intValue\":0,\"longValue\":0,\"floatValue\":0.0,\"doubleValue\":0.0,\"charValue\":\"a\",\"stringValue\":\"Test String\"},\"testObject6\":{\"boolValue\":false,\"byteValue\":0,\"shortValue\":0,\"intValue\":0,\"longValue\":0,\"floatValue\":0.0,\"doubleValue\":0.0,\"charValue\":\"a\",\"stringValue\":\"Test String\"},\"testObject7\":{\"boolValue\":false,\"byteValue\":0,\"shortValue\":0,\"intValue\":0,\"longValue\":0,\"floatValue\":0.0,\"doubleValue\":0.0,\"charValue\":\"a\",\"stringValue\":\"Test String\"}}";
	
	private TestScriptable scriptable;
	private TestComponent component;
	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		scriptable = TestScriptable.create();
		component = TestComponent.create();
	}
	
	@org.junit.jupiter.api.AfterEach
	void tearDown() {
		scriptable.destroy();
		scriptable = null;
	}
	
	@org.junit.jupiter.api.Test
	void enable() {
		assertTrue(scriptable.disable().enable().isActive());
	}
	
	@org.junit.jupiter.api.Test
	void disable() {
		assertFalse(scriptable.enable().disable().isActive());
	}
	
	@org.junit.jupiter.api.Test
	void registerComponent() {
		scriptable.registerComponent(component);
		assertTrue(scriptable.getActiveComponents().contains(component));
		component.disable();
		assertFalse(scriptable.getActiveComponents().contains(component));
		assertTrue(scriptable.getComponents().contains(component));
	}
	
	@org.junit.jupiter.api.Test
	void unregisterComponent() {
		scriptable.getComponents().forEach(scriptable::unregisterComponent);
		scriptable.registerComponent(component.enable());
		assertTrue(scriptable.getActiveComponents().contains(component));
		assertTrue(scriptable.getComponents().contains(component));
		scriptable.unregisterComponent(component);
		assertFalse(scriptable.getActiveComponents().contains(component));
		assertFalse(scriptable.getComponents().contains(component));
	}
	
	@org.junit.jupiter.api.Test
	void getActiveComponents() {
		scriptable.getComponents().forEach(scriptable::unregisterComponent);
		scriptable.registerComponent(component.enable());
		assertTrue(scriptable.getActiveComponents().contains(component));
		component.disable();
		assertFalse(scriptable.getActiveComponents().contains(component));
	}
	
	@org.junit.jupiter.api.Test
	void getScriptableName() {
		assertEquals(scriptable.getScriptableName(), TestScriptable.class.getName());
	}
	
	@SneakyThrows
	@org.junit.jupiter.api.Test
	void encodeScriptable() {
		ObjectMapper objectMapper = new ObjectMapper();
		scriptable.getComponents().forEach(scriptable::unregisterComponent);
		scriptable.registerComponent(component.enable());
		String json = objectMapper.writeValueAsString(scriptable);
		assertEquals(testEncodedObject, json);
	}
	
	@SneakyThrows
	@org.junit.jupiter.api.Test
	void decodeScriptable() {
		ObjectMapper objectMapper = new ObjectMapper();
		Scriptable<?> decodedScriptable = objectMapper.readValue(testEncodedObject, Scriptable.class);
		scriptable.getComponents().forEach(scriptable::unregisterComponent);
		scriptable.registerComponent(component.enable());
		assertEquals(scriptable, decodedScriptable);
	}
	
	@Test
	void testEventDispatch() {
		scriptable.getComponents().forEach(scriptable::unregisterComponent);
		scriptable.registerComponent(component.enable());
		TestEvent testEvent = new TestEvent().dispatch();
		EventEngine.processEventQueue();
		assertEquals( 5, testEvent.count);
	}
}