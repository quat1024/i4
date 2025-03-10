package agency.highlysuspect.i4.dgen.gen;

import java.util.Collection;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public interface GenContext {
	Collection<Gen> getGens();

	void writeFile(String subpath, String toWrite);

	default void writeJson(String subpath, JsonElement toWrite) {
		//If there's no file extension append ".json"
		if(subpath.indexOf('.') == -1) subpath += ".json";
		writeFile(subpath, new GsonBuilder().setPrettyPrinting().create().toJson(toWrite));
	}
}
