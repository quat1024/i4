package agency.highlysuspect.i4.dgen.gens;

import java.lang.reflect.Field;
import java.util.function.Function;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.dgen.facets.ItemModel;
import agency.highlysuspect.i4.dgen.facets.Lang;
import agency.highlysuspect.i4.dgen.facets.Register;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.ignos.Id;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public abstract class ItemGen extends Gen {
	public ItemGen(Id id) {
		this.id = id;
	}

	public ItemGen() {
		for(Field field : getClass().getFields()) {
			if(field.getDeclaringClass() != ItemGen.class &&
				field.getType() == Id.class &&
				field.getName().equals("ID")
			) {
				try {
					this.id = (Id) field.get(this);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public Id id;

	public ItemModel.ItemGenerated itemGenerated(Id tex) {
		return put(new ItemModel.ItemGenerated()).id(id).layer0(tex);
	}

	public ItemModel.ItemGenerated itemGenerated() {
		//reuse the item id as the layer0 texture id
		return itemGenerated(id.prefixPath("items"));
	}

	public Lang enUs(String name) {
		return enUs().value(name);
	}

	public Lang enUs() {
		return put(new Lang()).id(I4.id("en_us")).item(id);
	}

	public <X extends Item> Register<Item, X> register(Function<Item.Properties, X> func) {
		return put(new Register<Item, X>())
			.registry(BuiltInRegistries.ITEM)
			.id(id)
			.thing(() -> func.apply(new Item.Properties())); //TODO allow picking the Item.Properties
	}
}
