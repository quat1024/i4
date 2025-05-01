package agency.highlysuspect.i4.dgen.gens;

import java.util.function.Consumer;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.dgen.facets.AddBlockEntity;
import agency.highlysuspect.i4.dgen.facets.Lang;
import agency.highlysuspect.i4.dgen.facets.Register;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenSupport;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.RegType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public abstract class BlockGen<T extends Block> extends Gen {
	public BlockGen(Latch<T> latch) {
		this.blockLatch = latch;
	}

	public BlockGen(Id id) {
		this.blockLatch = Latch.open(RegType.BLOCKS, id);
	}

	public BlockGen() {
		this.blockLatch = GenSupport.reflectivelyFindLatch(RegType.BLOCKS, this);
	}

	public Latch<T> blockLatch;

	/////

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
		put(new Register<Block, T>()).latch(blockLatch).thing(this::constructBlock);
	}

	public abstract T constructBlock();

	/// lang ///

	public Lang enUs(String name) {
		return enUs().value(name);
	}

	public Lang enUs() {
		return put(new Lang()).id(I4.id("en_us")).block(blockLatch.id);
	}

	/// while we're here ///

	public <X extends BlockEntity> AddBlockEntity<X> blockEntity(Latch<BlockEntityType<X>> beTypeLatch) {
		return put(new AddBlockEntity<X>()).latch(beTypeLatch).addBlocks(blockLatch); //add self to this block
	}
}
