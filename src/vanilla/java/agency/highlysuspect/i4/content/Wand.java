package agency.highlysuspect.i4.content;

import agency.highlysuspect.i4.api.LogisticLinkable;
import agency.highlysuspect.i4.api.LogisticLinkableRoot;
import agency.highlysuspect.i4.dgen.gen.FindGen;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.dgen.gens.ItemGen;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Wand extends Item {
	public Wand(Properties properties) {
		super(properties);
	}

	//blah blah


	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		ItemStack held = ctx.getItemInHand();

		BlockEntity clicked = level.getBlockEntity(pos);
		if(clicked == null) return super.useOn(ctx);

		DataComponentType<LinkData> t = Latches.WAND_LINK_DATA.get();
		LinkData link = held.getOrDefault(t, LinkData.UNLINKED);

		if(clicked instanceof LogisticLinkableRoot) {
			//unset the link pos if you click on the same block again
			if(link.is(level, pos)) held.set(t, link.withUnlinked());
			else held.set(t, link.with(level, pos));

			if(ctx.getPlayer() != null)
				ctx.getPlayer().sendSystemMessage(Component.literal("new link is " + held.get(t)));

			return InteractionResult.sidedSuccess(level.isClientSide); //never understood this
		} else if(clicked instanceof LogisticLinkable) {
			LogisticLinkableRoot root = link.getRootIfValid(level);
			if(root != null) {
				if(root.isConnectedTo(clicked)) {
					root.removeConnection(clicked);
					if(ctx.getPlayer() != null) ctx.getPlayer().sendSystemMessage(Component.literal("removed connection"));
					return InteractionResult.sidedSuccess(level.isClientSide);
				} else {
					if(root.addConnection(clicked)) {
						if(ctx.getPlayer() != null) ctx.getPlayer().sendSystemMessage(Component.literal("added connection"));
						return InteractionResult.sidedSuccess(level.isClientSide);
					} else {
						if(ctx.getPlayer() != null) ctx.getPlayer().sendSystemMessage(Component.literal("couldnt add connection :("));
					}
				}
			}
		}

		return super.useOn(ctx);
	}

	public record LinkData(Optional<GlobalPos> dest) {
		public static final LinkData UNLINKED = new LinkData(Optional.empty());

		public boolean is(GlobalPos pos) {
			return dest.filter(pos::equals).isPresent();
		}

		public boolean is(Level level, BlockPos pos) {
			return is(new GlobalPos(level.dimension(), pos));
		}

		public @Nullable LogisticLinkableRoot getRootIfValid(Level level) {
			GlobalPos p = dest.orElse(null);
			if(p == null || !level.dimension().equals(p.dimension()) || !level.isLoaded(p.pos())) return null;
			BlockEntity be = level.getBlockEntity(p.pos());
			if(be instanceof LogisticLinkableRoot root) return root;
			else return null;
		}

		public LinkData with(GlobalPos newPos) {
			return new LinkData(Optional.of(newPos));
		}

		public LinkData with(Level level, BlockPos pos) {
			return with(new GlobalPos(level.dimension(), pos));
		}

		public LinkData withUnlinked() {
			return UNLINKED;
		}

		//welcome to mr mojang's wild ride
		public static final Codec<LinkData> CODEC = RecordCodecBuilder.create(i ->
			i.group(GlobalPos.CODEC.optionalFieldOf("linkPos").forGetter(l -> l.dest)).apply(i, LinkData::new));
		public static final StreamCodec<ByteBuf, LinkData> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), l -> l.dest, LinkData::new);
	}

	@FindGen
	public static class Gen extends ItemGen<Wand> {
		public Gen() {
			super(Latches.WAND);
		}

		public void gen(GenContext ctx) {
			super.gen(ctx);
			enUs("Entwining Wand");
			itemGenerated();
		}

		public void rt(RtContext ctx) {
			super.rt(ctx);

			dataComponent(Latches.WAND_LINK_DATA)
				.thing(() -> ctx.createDataComponentType(LinkData.CODEC, LinkData.STREAM_CODEC));
		}

		@Override
		public Wand constructItem() {
			return new Wand(new Item.Properties().stacksTo(1));
		}
	}
}
