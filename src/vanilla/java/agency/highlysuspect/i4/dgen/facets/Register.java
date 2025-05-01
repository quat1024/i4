package agency.highlysuspect.i4.dgen.facets;

import java.util.function.Supplier;

import agency.highlysuspect.i4.dgen.facet.Facet;
import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Latch;

@Facet
public class Register<T, X extends T> {
	public Latch<X> latch;
	public Supplier<X> thing;

	public Register<T, X> latch(Latch<X> latch) {
		this.latch = latch;
		return this;
	}

	public Register<T, X> thing(Supplier<X> thing) {
		this.thing = thing;
		return this;
	}

	@SuppressWarnings("unchecked")
	public static void handle(FacetHolder allFacets, RtContext ctx) {
		allFacets.forEach(Register.class, r -> ctx.register(r.latch, r.thing));
	}
}
