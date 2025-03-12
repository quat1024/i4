package agency.highlysuspect.i4.dgen.gens;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.dgen.facets.ItemModel;
import agency.highlysuspect.i4.dgen.facets.Lang;
import agency.highlysuspect.i4.dgen.facets.Register;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenSupport;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.RegType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;

public abstract class ItemGen<T extends Item> extends Gen {
	public ItemGen(Latch<T> latch) {
		this.itemLatch = latch;
	}

	public ItemGen(Id id) {
		this.itemLatch = Latch.open(RegType.ITEMS, id);
	}

	public ItemGen() {
		this.itemLatch = GenSupport.reflectivelyFindLatch(RegType.ITEMS, this);
	}

	public Latch<T> itemLatch;

	@Override
	public void rt(RtContext rt) {
		put(new Register<Item, T>()).latch(itemLatch).thing(this::constructItem);
	}

	public abstract T constructItem();

	/// models ///

	public ItemModel.ItemGenerated itemGenerated(Id tex) {
		return put(new ItemModel.ItemGenerated()).id(itemLatch.id).layer0(tex);
	}

	public ItemModel.ItemGenerated itemGenerated() {
		//reuse the item id as the layer0 texture id
		return itemGenerated(itemLatch.id.prefixPath("items"));
	}

	/// lang ///

	public Lang enUs(String name) {
		return enUs().value(name);
	}

	public Lang enUs() {
		return put(new Lang()).id(I4.id("en_us")).item(itemLatch.id);
	}

	/// wadda hell ///
	//TODO mabe push this up into an interface or even into Gen if it makes sense

	public <X> Register<DataComponentType<?>, DataComponentType<X>> dataComponent(Latch<DataComponentType<X>> latch) {
		return put(new Register<DataComponentType<?>, DataComponentType<X>>()).latch(latch);
	}
}
