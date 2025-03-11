package agency.highlysuspect.i4.content;

import agency.highlysuspect.i4.dgen.gen.FindGen;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.dgen.gens.BlockGen;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
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
			super(Gen.beType.get(), pos, state);
		}
	}

	@FindGen
	public static class Gen extends BlockGen {
		public static final String ID = "i4:logistic_core";

		public static Reg.Handle<LogisticCore> block;
		public static Reg.Handle<BlockEntityType<Ent>> beType;

		public void gen(GenContext ctx) {
			enUs("Logistic Core");
		}

		public void rt(RtContext ctx) {
			block = regBlock(ctx, () -> new LogisticCore(BlockBehaviour.Properties.of()));
			regBlockItem(ctx, block);

			//hmm hmm not that good
			beType = ctx.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockId, () -> ctx.makeBlockEntityType(Ent::new, block.get()));
		}
	}
}
