package agency.highlysuspect.i4.content;

import agency.highlysuspect.i4.dgen.gen.FindGen;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.dgen.gens.ItemGen;
import net.minecraft.world.item.Item;

public class Wand extends Item {
	public Wand(Properties properties) {
		super(properties);
	}

	//blah blah

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

		@Override
		public Wand constructItem() {
			return new Wand(new Item.Properties().stacksTo(1));
		}
	}
}
