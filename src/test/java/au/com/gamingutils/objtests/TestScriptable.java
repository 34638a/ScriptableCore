package au.com.gamingutils.objtests;

import au.com.gamingutils.script.Scriptable;
import au.com.gamingutils.script.event.EventListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestScriptable extends Scriptable<TestScriptable> {
	
	private EncodeDecodeTestObject testObject1;
	protected EncodeDecodeTestObject testObject2;
	public EncodeDecodeTestObject testObject3;
	public EncodeDecodeTestObject testObject4 = null;
	EncodeDecodeTestObject testObject5;
	final EncodeDecodeTestObject testObject6 = new EncodeDecodeTestObject();
	transient EncodeDecodeTestObject testObject7;
	
	static EncodeDecodeTestObject testObject8;
	
	@EventListener(TestEvent.class)
	public void testListener1(TestEvent event) {
		System.out.println("Scriptable testListener instance");
		event.count++;
	}
	
	@EventListener(TestEvent.class)
	public static void testListener2(TestEvent event) {
		System.out.println("Scriptable testListener static");
		event.count++;
	}
	
	public static TestScriptable create() {
		TestScriptable test = new TestScriptable();
		test.testObject1 = new EncodeDecodeTestObject();
		test.testObject2 = new EncodeDecodeTestObject();
		test.testObject3 = new EncodeDecodeTestObject();
		test.testObject5 = new EncodeDecodeTestObject();
		test.testObject7 = new EncodeDecodeTestObject();
		return test;
	}
	
}
