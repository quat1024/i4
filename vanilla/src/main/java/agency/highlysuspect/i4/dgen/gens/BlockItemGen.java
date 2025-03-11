package agency.highlysuspect.i4.dgen.gens;

import agency.highlysuspect.i4.dgen.facets.ItemModel;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlockItemGen<B extends Block, I extends BlockItem> extends ItemGen<I> {
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

	@SuppressWarnings("unchecked")
	@Override
	public I constructItem() {
		//this is an unsound cast tbh tbh, but it works as a default implementation
		//like "new BlockItemGen<>(something)" will work okay
		return (I) new BlockItem(block.handle.get(), new Item.Properties());
	}
}
