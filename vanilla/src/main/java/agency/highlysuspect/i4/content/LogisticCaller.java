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
			super(Gen.INSTANCE.beType.get(), pos, state);
		}
	}

	@FindGen
	public static class Gen extends BlockGen<LogisticCaller> {
		public static final Gen INSTANCE = new Gen();
		public static final String ID = "i4:logistic_caller";

		public Latch<BlockEntityType<Ent>> beType = Latch.open(RegType.BLOCK_ENTITY_TYPES, id);

		@Override
		public void gen(GenContext ctx) {
			super.gen(ctx);
			enUs("Logistic Caller");
		}

		@Override
		public void rt(RtContext rt) {
			super.rt(rt);
			blockEntity(rt, Ent::new, block).latch(beType);
		}

		@Override
		public LogisticCaller constructBlock() {
			return new LogisticCaller(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
		}
	}
}
