package agency.highlysuspect.i4.crap;

import java.util.function.Consumer;

import agency.highlysuspect.i4.ignos.Latch;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class TickerHelper<T extends BlockEntity> {
	public TickerHelper(Latch<BlockEntityType<T>> latch) {
		this.latch = latch;
	}

	private final Latch<BlockEntityType<T>> latch;

	@SuppressWarnings("unchecked")
	@Nullable
	public <A extends BlockEntity> BlockEntityTicker<A> castTicker(BlockEntityType<A> wantedType, BlockEntityTicker<T> ticker) {
		if(wantedType.equals(latch.get())) return (BlockEntityTicker<A>) ticker;
		else return null;
	}

	@Nullable
	public <A extends BlockEntity> BlockEntityTicker<A> simpleTicker(BlockEntityType<A> wantedType, Consumer<T> ticker) {
		return castTicker(wantedType, (level, pos, state, be) -> ticker.accept(be));
	}
}
