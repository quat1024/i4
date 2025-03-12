package agency.highlysuspect.i4.ignos;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * BuiltInRegistries is a classloading bomb pre-bootstrap so have this janky thing instead
 */
public class RegType<T> {
	public static final RegType<Block> BLOCKS = new RegType<>();
	public static final RegType<Item> ITEMS = new RegType<>();
	public static final RegType<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new RegType<>();

	@SuppressWarnings("unchecked")
	public Registry<T> toRegistry() {
		if(this == BLOCKS) return (Registry<T>) BuiltInRegistries.BLOCK;
		else if(this == ITEMS) return (Registry<T>) BuiltInRegistries.ITEM;
		else if(this == BLOCK_ENTITY_TYPES) return (Registry<T>) BuiltInRegistries.BLOCK_ENTITY_TYPE;
		else throw new IllegalArgumentException("unknown Regs " + this);
	}

	@SuppressWarnings("unchecked")
	public ResourceKey<Registry<T>> toKey() {
		if(this == BLOCKS) return (ResourceKey<Registry<T>>) (Object) Registries.BLOCK;
		else if(this == ITEMS) return (ResourceKey<Registry<T>>) (Object) Registries.ITEM;
		else if(this == BLOCK_ENTITY_TYPES) return (ResourceKey<Registry<T>>) (Object) Registries.BLOCK_ENTITY_TYPE;
		else throw new IllegalArgumentException("unknown Regs " + this);
	}

	@Override
	public String toString() {
		if(this == BLOCKS) return "blocks";
		else if(this == ITEMS) return "items";
		else if(this == BLOCK_ENTITY_TYPES) return "block entity types";
		else return "unknown!";
	}
}
