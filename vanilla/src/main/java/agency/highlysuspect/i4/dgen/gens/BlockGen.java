package agency.highlysuspect.i4.dgen.gens;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.dgen.facets.Lang;
import agency.highlysuspect.i4.dgen.facets.Register;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenSupport;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.RegType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BlockGen<T extends Block> extends Gen {
	public BlockGen() {
		this.id = GenSupport.reflectivelyFindId(this);
		this.block = Latch.open(RegType.BLOCKS, id);
	}

	public BlockGen(Id id) {
		this.id = id;
		this.block = Latch.open(RegType.BLOCKS, id);
	}

	public transient Id id;
	public Latch<T> block;

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
			.latch(block)
			.thing(this::constructBlock));
	}

	public abstract T constructBlock();

	/// lang ///

	public Lang enUs(String name) {
		return enUs().value(name);
	}

	public Lang enUs() {
		return put(new Lang()).id(I4.id("en_us")).block(id);
	}

	/// while we're here ///

	public <X extends BlockEntity> Register<BlockEntityType<?>, BlockEntityType<X>> blockEntity(RtContext rt, BiFunction<BlockPos, BlockState, X> maker, Collection<Latch<? extends Block>> blocks) {
		return put(new Register<BlockEntityType<?>, BlockEntityType<X>>())
			.thing(() -> rt.makeBlockEntityType(maker, blocks.stream().map(Latch::get).toArray(Block[]::new)));
	}

	@SafeVarargs
	public final <X extends BlockEntity> Register<BlockEntityType<?>, BlockEntityType<X>> blockEntity(RtContext rt, BiFunction<BlockPos, BlockState, X> maker, Latch<? extends Block>... blocks) {
		return blockEntity(rt, maker, List.of(blocks));
	}
}
