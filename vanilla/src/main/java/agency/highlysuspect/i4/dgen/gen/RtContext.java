package agency.highlysuspect.i4.dgen.gen;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import agency.highlysuspect.i4.ignos.Latch;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface RtContext {
	<T, X extends T> Latch<X> register(Latch<X> latch, Supplier<X> s);

	<T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(BiFunction<BlockPos, BlockState, T> maker, Block... blocks);

	default <T> DataComponentType<T> createDataComponentType(Codec<T> codec) {
		return new DataComponentType.Builder<T>().persistent(codec).build();
	}

	default <T> DataComponentType<T> createDataComponentType(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> stream) {
		return new DataComponentType.Builder<T>().persistent(codec).networkSynchronized(stream).build();
	}
}
