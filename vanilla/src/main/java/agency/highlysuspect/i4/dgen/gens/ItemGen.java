package agency.highlysuspect.i4.dgen.gens;

import java.util.function.Supplier;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.dgen.facets.ItemModel;
import agency.highlysuspect.i4.dgen.facets.Lang;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenSupport;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public abstract class ItemGen extends Gen {
	public ItemGen() {
		this.itemId = GenSupport.reflectivelyFindId(this);
	}

	public ItemGen(Id itemId) {
		this.itemId = itemId;
	}

	public transient Id itemId;

	public ItemModel.ItemGenerated itemGenerated(Id tex) {
		return put(new ItemModel.ItemGenerated()).id(itemId).layer0(tex);
	}

	public ItemModel.ItemGenerated itemGenerated() {
		//reuse the item id as the layer0 texture id
		return itemGenerated(itemId.prefixPath("items"));
	}

	public Lang enUs(String name) {
		return enUs().value(name);
	}

	public Lang enUs() {
		return put(new Lang()).id(I4.id("en_us")).item(itemId);
	}

	//TODO: this idea is kind of broken, no way to get a registryhandle
	//so you can't really make blockitems...
//	public <X extends Item> Register<Item, X> register(Function<Item.Properties, X> func) {
//		return put(new Register<Item, X>())
//			.registry(BuiltInRegistries.ITEM)
//			.id(itemId)
//			.thing(() -> func.apply(new Item.Properties())); //TODO allow picking the Item.Properties
//	}
	public <X extends Item> Reg.Handle<X> register(RtContext ctx, Supplier<X> s) {
		return ctx.register(BuiltInRegistries.ITEM, itemId, s);
	}
}
