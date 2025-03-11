package agency.highlysuspect.i4.content;

import agency.highlysuspect.i4.dgen.gen.FindGen;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.dgen.gens.BlockGen;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.RegType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LogisticCore extends Block implements EntityBlock {
	public LogisticCore(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new Ent(pos, state);
	}

	public static class Ent extends BlockEntity {
		public Ent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(type, pos, state);
		}

		public Ent(BlockPos pos, BlockState state) {
			super(Gen.INSTANCE.beType.get(), pos, state);
		}
	}

	@FindGen
	public static class Gen extends BlockGen<LogisticCore> {
		public static final Gen INSTANCE = new Gen();
		public static final String ID = "i4:logistic_core";

		public Latch<BlockEntityType<Ent>> beType = Latch.open(RegType.BLOCK_ENTITY_TYPES, id);

		public void gen(GenContext ctx) {
			super.gen(ctx);
			enUs("Logistic Core");
		}

		public void rt(RtContext ctx) {
			super.rt(ctx);

			blockEntity(ctx, Ent::new, block).latch(beType);
		}

		@Override
		public LogisticCore constructBlock() {
			return new LogisticCore(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
		}
	}
}
