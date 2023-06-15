package au.com.gamingutils.script.exception;

public class ComponentBindingException extends RuntimeException {
	
	private static final String MESSAGE = "Component %s is already bound to a scriptable.";
	
	public ComponentBindingException(String componentName) {
		super(String.format(MESSAGE, componentName));
	}
}
