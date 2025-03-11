package agency.highlysuspect.i4.dgen.gen;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import agency.highlysuspect.i4.ignos.Latch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface RtContext {
	<T, X extends T> Latch<X> register(Latch<X> latch, Supplier<X> s);

	<T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(BiFunction<BlockPos, BlockState, T> maker, Block... blocks);
}
