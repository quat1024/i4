package agency.highlysuspect.i4.dgen.facets;

import agency.highlysuspect.i4.dgen.facet.Facet;
import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Latch;

import java.util.function.Supplier;

/**
 * registers some type of content into the game's registry system
 * @param <T> registry type (eg. Block)
 * @param <X> more specfic type (eg. MyBlock)
 */
@Facet
public class Register<T, X extends T> {
	public Latch<X> latch;
	public Supplier<X> thing;
	
	/**
	 * a latch that this content will shut when it's registered.
	 * latches contain the registry type & the ID to register it under.
	 * many latches can be created for the same piece of content - i only need one
	 * @return this
	 */
	public Register<T, X> latch(Latch<X> latch) {
		this.latch = latch;
		return this;
	}
	
	/**
	 * the thing to register
	 * @return this
	 */
	public Register<T, X> thing(Supplier<X> thing) {
		this.thing = thing;
		return this;
	}

	@SuppressWarnings("unchecked")
	public static void handle(FacetHolder allFacets, RtContext ctx) {
		allFacets.forEach(Register.class, r -> ctx.register(r.latch, r.thing));
	}
}
