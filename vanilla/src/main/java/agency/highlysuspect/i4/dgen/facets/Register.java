package agency.highlysuspect.i4.dgen.facets;

import java.util.function.Supplier;

import agency.highlysuspect.i4.dgen.facet.Facet;
import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import net.minecraft.core.Registry;

@Facet
public class Register<T, X extends T> extends Idable<Register<T, X>> {
	//todo, resourcekeys and stuff, alternate ways of specifying the registry
	public Registry<T> registry;
	public Supplier<X> thing;

	//TODO registry callbacks?

	public Register<T, X> registry(Registry<T> registry) {
		this.registry = registry;
		return this;
	}

	public Register<T, X> thing(Supplier<X> thing) {
		this.thing = thing;
		return this;
	}

	@SuppressWarnings("unchecked")
	public static void handle(FacetHolder allFacets, RtContext ctx) {
		allFacets.forEach(Register.class, r -> ctx.register(r.registry, r.id, r.thing));
	}
}
