package agency.highlysuspect.i4.content;

import agency.highlysuspect.i4.dgen.gen.FindGen;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.dgen.gens.BlockGen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LogisticCaller extends Block implements EntityBlock {
	public LogisticCaller(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new Ent(pos, state);
	}

	public static class Ent extends BlockEntity {
		public Ent(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
			super(blockEntityType, pos, state);
		}

		public Ent(BlockPos pos, BlockState state) {
			super(Latches.LOGISTIC_CALLER_ENT.get(), pos, state);
		}
	}

	@FindGen
	public static class Gen extends BlockGen<LogisticCaller> {
		public Gen() {
			super(Latches.LOGISTIC_CALLER);
		}

		@Override
		public void gen(GenContext ctx) {
			super.gen(ctx);
			enUs("Logistic Caller");
		}

		@Override
		public void rt(RtContext rt) {
			super.rt(rt);
			blockEntity(Latches.LOGISTIC_CALLER_ENT).factory(Ent::new);
		}

		@Override
		public LogisticCaller constructBlock() {
			return new LogisticCaller(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
		}
	}
}
