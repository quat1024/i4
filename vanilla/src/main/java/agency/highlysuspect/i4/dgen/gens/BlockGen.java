package agency.highlysuspect.i4.dgen.gens;

import java.util.function.Consumer;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.dgen.facets.Lang;
import agency.highlysuspect.i4.dgen.facets.Register;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenSupport;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class BlockGen<T extends Block> extends Gen {
	public BlockGen() {
		this.id = GenSupport.reflectivelyFindId(this);
		this.handle = new Reg.UnboundId<>(id);
	}

	public BlockGen(Id id) {
		this.id = id;
		this.handle = new Reg.UnboundId<>(id);
	}

	public transient Id id;
	public Reg.Handle<T> handle;

	@Override
	public void fanout(Consumer<Gen> fanout) {
		super.fanout(fanout);

		ItemGen<? extends Item> ig = itemForm();
		if(ig != null) fanout.accept(ig);
	}

	public @Nullable ItemGen<? extends Item> itemForm() {
		return new BlockItemGen<>(this);
	}

	/////

	@Override
	public void rt(RtContext rt) {
		super.rt(rt);
		put(new Register<Block, T>()
			.id(id)
			.registry(BuiltInRegistries.BLOCK)
			.thing(this::constructBlock)
			.handleCallback(h -> this.handle = h));
	}

	public abstract T constructBlock();

	/// lang ///

	public Lang enUs(String name) {
		return enUs().value(name);
	}

	public Lang enUs() {
		return put(new Lang()).id(I4.id("en_us")).block(id);
	}
}
