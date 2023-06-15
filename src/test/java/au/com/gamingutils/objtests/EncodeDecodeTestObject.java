package au.com.gamingutils.objtests;

import lombok.Data;

@Data
public class EncodeDecodeTestObject {
	
	//Primitives and Simple Types
	private boolean boolValue;
	private byte byteValue;
	private short shortValue;
	private int intValue;
	private long longValue;
	private float floatValue;
	private double doubleValue;
	private char charValue = 'a';
	private String stringValue = "Test String";
}
