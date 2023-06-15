package au.com.gamingutils.objtests;

import au.com.gamingutils.script.event.EventListener;

public class TestEventListener {
	
	
	@EventListener(TestEvent.class)
	public void testListener1(TestEvent event) {
		System.out.println("TestEventListener testListener instance");
		event.count++;
	}
	
	@EventListener(TestEvent.class)
	public static void testListener2(TestEvent event) {
		System.out.println("TestEventListener testListener static");
		event.count++;
	}
}
