package agency.highlysuspect.i4.content;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.dgen.gen.FindGen;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.dgen.gens.ItemGen;
import agency.highlysuspect.i4.ignos.Id;
import net.minecraft.world.item.Item;

public class Wand extends Item {
	public Wand(Properties properties) {
		super(properties);
	}

	@FindGen
	public static class Gen extends ItemGen {
		public static final Id ID = I4.id("wand");

		public void gen(GenContext ctx) {
			enUs("Magic Wand");
			itemGenerated();
		}

		public void rt(RtContext ctx) {
			register(Wand::new);
		}
	}
}
