package agency.highlysuspect.i4.dgen.gens;

import agency.highlysuspect.i4.dgen.facets.ItemModel;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlockItemGen<B extends Block> extends ItemGen<BlockItem> {
	public BlockItemGen(BlockGen<B> block) {
		super(block.id);
		this.block = block;
	}

	protected BlockGen<B> block;

	@Override
	public void gen(GenContext ctx) {
		super.gen(ctx);
		put(new ItemModel.Plain()).id(id).parent(block.id.prefixPath("block/"));
	}

	@Override
	public BlockItem constructItem() {
		return new BlockItem(block.handle.get(), new Item.Properties());
	}
}
