package agency.highlysuspect.i4.fabric;

import java.util.function.BiFunction;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.fabric.ignos.FabricReg;
import agency.highlysuspect.i4.ignos.Reg;
import agency.highlysuspect.i4.ignos.RegType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class I4Fabric extends I4 implements ModInitializer {
	public static I4Fabric LOADER_INST;

	@SuppressWarnings("unchecked")
	@Override
	public void onInitialize() {
		LOADER_INST = this;
		LOG.info("Hello from i4Fabric");

		handleGens();

		//register the blocks, then the items, then everything else
		FabricReg<Block> block = (FabricReg<Block>) defers.get(RegType.BLOCKS);
		if(block != null) block.registerAll();
		FabricReg<Item> item = (FabricReg<Item>) defers.get(RegType.ITEMS);
		if(item != null) item.registerAll();
		defers.forEach((k, r) -> {
			if(k == RegType.BLOCKS || k == RegType.ITEMS) return;
			((FabricReg<?>) r).registerAll();
		});

		defers = null; //not needed anymore
	}

	@Override
	public <T> Reg<T> createReg(I4 i4, RegType<T> registry) {
		return new FabricReg<>(i4, registry.toRegistry());
	}

	@Override
	public <T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(BiFunction<BlockPos, BlockState, T> maker, Block... blocks) {
		return BlockEntityType.Builder.of(maker::apply, blocks).build(null);
	}
}
