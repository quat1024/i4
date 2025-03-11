package agency.highlysuspect.i4.dgen.gens;

import java.util.function.Supplier;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.dgen.facets.Lang;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenSupport;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class BlockGen extends Gen {
	public BlockGen() {
		this.blockId = GenSupport.reflectivelyFindId(this);
	}

	public BlockGen(Id blockId) {
		this.blockId = blockId;
	}

	public transient Id blockId;

	public Lang enUs(String name) {
		return enUs().value(name);
	}

	public Lang enUs() {
		return put(new Lang()).id(I4.id("en_us")).block(blockId);
	}

	//helpers
	protected <X extends Block> Reg.Handle<X> regBlock(RtContext ctx, Supplier<X> b) {
		return ctx.register(BuiltInRegistries.BLOCK, blockId, b);
	}

	protected Reg.Handle<BlockItem> regBlockItem(RtContext ctx, Reg.Handle<? extends Block> b) {
		return ctx.register(BuiltInRegistries.ITEM, blockId, () -> new BlockItem(b.get(), new Item.Properties()));
	}
}
