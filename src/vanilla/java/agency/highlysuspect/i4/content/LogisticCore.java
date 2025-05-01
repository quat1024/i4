package agency.highlysuspect.i4.content;

import java.util.HashSet;
import java.util.Set;

import agency.highlysuspect.i4.api.LogisticLinkable;
import agency.highlysuspect.i4.api.LogisticLinkableRoot;
import agency.highlysuspect.i4.crap.TickerHelper;
import agency.highlysuspect.i4.dgen.gen.FindGen;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.dgen.gens.BlockGen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
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

	private static final TickerHelper<Ent> tickerHelper = new TickerHelper<>(Latches.LOGISTIC_CORE_BE);
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return tickerHelper.simpleTicker(type, Ent::tickServer);
	}

	public static class Ent extends BlockEntity implements LogisticLinkableRoot {
		public Ent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(type, pos, state);
		}

		public Ent(BlockPos pos, BlockState state) {
			super(Latches.LOGISTIC_CORE_BE.get(), pos, state);
		}

		public static final int MAX_CONNECTIONS = 16; //TODO play with this value, too much, too little
		public static final int MAX_RANGE = 32; //TODO also play with this
		public static final int MAX_RANGE_SQUARED = MAX_RANGE * MAX_RANGE;

		private final Set<BlockPos> links = new HashSet<>();

		private void tickServer() {
			if(level == null) return;
			if(level.getGameTime() % 10 == 0) upkeepConnections();
		}

		@Override
		public boolean addConnection(BlockEntity other) {
			if(!(other instanceof LogisticLinkable dest)) return false;
			if(!dest.wantsToConnect(this)) return false;
			if(links.size() >= MAX_CONNECTIONS) return false;
			if(getBlockPos().distSqr(other.getBlockPos()) > MAX_RANGE_SQUARED) return false;

			links.add(other.getBlockPos().immutable());
			setChanged();
			return true;
		}

		@Override
		public void removeConnection(BlockEntity other) {
			links.remove(other.getBlockPos());
			setChanged();
		}

		@Override
		public boolean isConnectedTo(BlockEntity other) {
			return links.contains(other.getBlockPos());
		}

		public void upkeepConnections() {
			if(level == null) return;
			int oldSize = links.size();
			links.removeIf(link -> {
				if(getBlockPos().distSqr(link) > MAX_RANGE_SQUARED) return true; //too far
				if(!level.isLoaded(link)) return false; //don't know enough to make a decision
				BlockEntity be = level.getBlockEntity(link);
				if(!(be instanceof LogisticLinkable dest)) return true;
				else return !dest.wantsToConnect(this);
			});
			if(oldSize != links.size()) setChanged();
		}

		@Override
		protected void saveAdditional(CompoundTag tag, HolderLookup.Provider hlp) {
			if(links.isEmpty()) {
				tag.remove("Links");
			} else {
				int[] arr = new int[links.size() * 3];
				int i = 0;
				for(BlockPos pos : links) {
					arr[i] = pos.getX();
					arr[i + 1] = pos.getY();
					arr[i + 2] = pos.getZ();
					i += 3;
				}
				tag.putIntArray("Links", arr);
			}
		}

		@Override
		protected void loadAdditional(CompoundTag tag, HolderLookup.Provider hlp) {
			links.clear();

			if(tag.contains("Links", 11)) {
				int[] arr = tag.getIntArray("Links");
				if(arr.length % 3 == 0 && (arr.length / 3 <= MAX_CONNECTIONS)) {
					for(int i = 0; i < arr.length; i += 3) links.add(new BlockPos(arr[i], arr[i + 1], arr[i + 2]));
				}
			}
		}

		@Nullable
		@Override
		public Packet<ClientGamePacketListener> getUpdatePacket() {
			return ClientboundBlockEntityDataPacket.create(this);
		}

		@Override
		public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
			CompoundTag t = new CompoundTag();
			saveAdditional(t, provider);
			return t;
		}
	}

	@FindGen
	public static class Gen extends BlockGen<LogisticCore> {
		public Gen() {
			super(Latches.LOGISTIC_CORE);
		}

		public void gen(GenContext ctx) {
			super.gen(ctx);
			enUs("Logistic Core");
		}

		public void rt(RtContext ctx) {
			super.rt(ctx);
			blockEntity(Latches.LOGISTIC_CORE_BE).factory(Ent::new);
		}

		@Override
		public LogisticCore constructBlock() {
			return new LogisticCore(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
		}
	}
}
