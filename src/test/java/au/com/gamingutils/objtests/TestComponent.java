package au.com.gamingutils.objtests;

import au.com.gamingutils.script.Scriptable;
import au.com.gamingutils.script.event.EventListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TestComponent extends Scriptable.Component<TestComponent> {
	
	public String testComponentString;
	
	
	@EventListener(TestEvent.class)
	public void testListener1(TestEvent event) {
		System.out.println("Component testListener instance");
		event.count++;
	}
	
	@EventListener(TestEvent.class)
	public static void testListener2(TestEvent event) {
		System.out.println("Component testListener static");
		event.count++;
	}
	
	public static TestComponent create() {
		return new TestComponent().setTestComponentString("TestComponentString");
	}
}
