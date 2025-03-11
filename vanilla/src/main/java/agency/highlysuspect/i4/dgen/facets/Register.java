package agency.highlysuspect.i4.dgen.facets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import agency.highlysuspect.i4.dgen.facet.Facet;
import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.Registry;

@Facet
public class Register<T, X extends T> extends Idable<Register<T, X>> {
	public Registry<T> registry; //todo, resourcekeys and stuff, alternate ways of specifying the registry
	public Supplier<X> thing;
	public List<Consumer<Reg.Handle<X>>> callbacks = new ArrayList<>(1);

	public Register<T, X> registry(Registry<T> registry) {
		this.registry = registry;
		return this;
	}

	public Register<T, X> thing(Supplier<X> thing) {
		this.thing = thing;
		return this;
	}

	public Register<T, X> handleCallback(Consumer<Reg.Handle<X>> consumer) {
		this.callbacks.add(consumer);
		return this;
	}

	private void runCallbacks(Reg.Handle<X> handle) {
		if(callbacks == null) throw new IllegalStateException("already ran registry callbacks for " + id);
		callbacks.forEach(c -> c.accept(handle));
		callbacks = null;
	}

	@SuppressWarnings("unchecked")
	public static void handle(FacetHolder allFacets, RtContext ctx) {
		allFacets.forEach(Register.class, r -> r.runCallbacks(ctx.register(r.registry, r.id, r.thing)));
	}
}
