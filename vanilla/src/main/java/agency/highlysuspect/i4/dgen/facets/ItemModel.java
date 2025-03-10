package agency.highlysuspect.i4.dgen.facets;

import agency.highlysuspect.i4.dgen.facet.Facet;
import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.facet.WriteTo;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.ignos.Id;
import com.google.gson.JsonObject;

@Facet
public abstract class ItemModel<D> extends Idable<D> implements WriteTo<JsonObject> {
	public Id parent;

	public ItemModel<D> parent(Id parent) {
		this.parent = parent;
		return this;
	}

	@Override
	public JsonObject write() {
		JsonObject obj = new JsonObject();
		obj.addProperty("parent", parent.toStringOmitMc());
		return obj;
	}

	public static class ItemGenerated extends ItemModel<ItemGenerated> {
		public ItemGenerated() {
			super();
			parent(Id.mc("item/generated"));
		}

		public Id layer0;

		public ItemGenerated layer0(Id layer0) {
			this.layer0 = layer0;
			return this;
		}

		@Override
		public JsonObject write() {
			JsonObject obj = super.write();

			JsonObject textures = new JsonObject();
			textures.addProperty("layer0", layer0.toStringOmitMc());
			obj.add("textures", textures);

			return obj;
		}
	}

	public static void handle(FacetHolder everyFacet, GenContext ctx) {
		everyFacet.forEach(ItemModel.class, im ->
			ctx.writeJson("assets/" + im.id.namespace + "/models/item/" + im.id.path, im.write()));
	}
}
