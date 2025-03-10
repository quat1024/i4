package agency.highlysuspect.i4.dgen.facets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agency.highlysuspect.i4.dgen.facet.Downcastable;
import agency.highlysuspect.i4.dgen.facet.Facet;
import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.ignos.Id;
import com.google.gson.JsonObject;

//id is the id of the lang file
@Facet
public class Lang extends Idable<Lang> implements Downcastable<Lang> {
	public String key, value;

	public Lang key(String key) {
		this.key = key;
		return this;
	}

	public Lang block(Id blockId) {
		this.key = blockId.toLangKey("block");
		return this;
	}

	public Lang item(Id itemId) {
		this.key = itemId.toLangKey("item");
		return this;
	}

	public Lang value(String value) {
		this.value = value;
		return this;
	}

	public static void handle(FacetHolder everyFacet, GenContext ctx) {
		Map<Id, List<Lang>> everyLang = new HashMap<>();
		everyFacet.forEach(Lang.class, lang ->
			everyLang.computeIfAbsent(lang.id, __ -> new ArrayList<>()).add(lang));

		//sort all lang files by key
		everyLang.values().forEach(list -> list.sort(Comparator.comparing(l -> l.key)));

		everyLang.forEach((langId, entries) -> {
			JsonObject kvs = new JsonObject();
			for(Lang lang : entries) kvs.addProperty(lang.key, lang.value);

			ctx.writeJson("assets/" + langId.namespace + "/lang/" + langId.path, kvs);
		});
	}
}
