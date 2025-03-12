package agency.highlysuspect.i4.content;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.RegType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class Latches {
	public static final Latch<Wand> WAND = Latch.open(RegType.ITEMS, I4.id("wand"));

	//ugh
	public static final Latch<DataComponentType<Wand.LinkData>> WAND_LINK_DATA = Latch.open(RegType.DATA_COMPONENT_TYPES, I4.id("wand_link_data"));


	public static final Latch<LogisticCore> LOGISTIC_CORE =
		Latch.open(RegType.BLOCKS, I4.id("logistic_core"));
	public static final Latch<BlockEntityType<LogisticCore.Ent>> LOGISTIC_CORE_BE =
		Latch.open(RegType.BLOCK_ENTITY_TYPES, I4.id("logistic_core"));

	public static final Latch<LogisticCaller> LOGISTIC_CALLER =
		Latch.open(RegType.BLOCKS, I4.id("logistic_caller"));
	public static final Latch<BlockEntityType<LogisticCaller.Ent>> LOGISTIC_CALLER_ENT =
		Latch.open(RegType.BLOCK_ENTITY_TYPES, I4.id("logistic_caller"));
}
