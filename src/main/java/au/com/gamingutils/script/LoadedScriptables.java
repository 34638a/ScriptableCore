package au.com.gamingutils.script;

import java.util.ArrayList;
import java.util.List;

public class LoadedScriptables {
	
	static final List<Scriptable<?>> scriptables = new ArrayList<>();
	
	
	public static List<Scriptable<?>> getScriptables() {
		return new ArrayList<>(scriptables);
	}
	
	public static List<Scriptable<?>> getActiveScriptables() {
		return scriptables.stream().filter(Scriptable::isActive).toList();
	}
}
