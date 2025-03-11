package agency.highlysuspect.i4.dgen.gen;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface RtContext {
	<T, X extends T> Reg.Handle<X> register(Registry<T> registry, Id id, Supplier<X> s);

	<T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(BiFunction<BlockPos, BlockState, T> maker, Block... blocks);
}
