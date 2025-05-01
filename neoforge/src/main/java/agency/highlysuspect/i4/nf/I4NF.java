package agency.highlysuspect.i4.nf;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Reg;
import agency.highlysuspect.i4.ignos.RegType;
import agency.highlysuspect.i4.nf.ignos.DeferredReg;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.Set;
import java.util.function.BiFunction;

@Mod("i4")
public class I4NF extends I4 {
	public static I4NF LOADER_INST;
	protected final IEventBus modBus;

	public I4NF(IEventBus modbus) {
		LOADER_INST = this;
		LOG.info("Hello from I4NF");
		handleGens();
		
		this.modBus = modbus;
	}
	
	@Override
	public <T> Reg<T> createReg(I4 i4, RegType<T> regType) {
		return new DeferredReg<>(i4, regType.toRegistry(), modBus);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(BiFunction<BlockPos, BlockState, T> maker, Block... blocks) {
		return new BlockEntityType<>(maker::apply, Set.of(blocks), null);
	}
}
