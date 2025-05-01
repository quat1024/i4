package agency.highlysuspect.i4.nf;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Reg;
import agency.highlysuspect.i4.ignos.RegType;
import agency.highlysuspect.i4.nf.ignos.NfReg;
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

	public I4NF(IEventBus modBus) {
		LOADER_INST = this;
		this.modBus = modBus;
		
		LOG.info("Hello from I4NF");
		handleGens();
	}
	
	@Override
	public <T> Reg<T> createRegHelper(I4 i4, RegType<T> regType) {
		NfReg<T> reg = new NfReg<>(i4, regType.toRegistry());
		modBus.addListener(reg::doRegister);
		return reg;
	}
	
	//annoyingly loader-specific. NF access-widens this
	@Override
	public <T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(BiFunction<BlockPos, BlockState, T> maker, Block... blocks) {
		return new BlockEntityType<>(maker::apply, Set.of(blocks), null);
	}
}
