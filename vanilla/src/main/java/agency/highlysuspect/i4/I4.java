package agency.highlysuspect.i4;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.facets.Register;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenFinder;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class I4 implements RtContext {
	public static final String MODID = "i4";
	public static final Logger LOG = LoggerFactory.getLogger("i4");

	public static I4 INSTANCE;

	protected Map<Registry<?>, Reg<?>> defers = new HashMap<>();

	public I4() {
		INSTANCE = this;
		LOG.info("Hello from i4");
	}

	public static Id id(String path) {
		return new Id(MODID, path);
	}

	public void handleGens() {
		//find gens
		GenFinder finder = new GenFinder.ServiceLoaderFinder();
		Collection<Gen> gens = finder.findGens();
		LOG.info("Found {} gens", gens.size());

		//invoke gens
		for(Gen gen : gens) gen.rt(this);
		FacetHolder allFacets = new FacetHolder().merge(gens);

		//handle facets
		Register.handle(allFacets, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, X extends T> Reg.Handle<X> register(Registry<T> registry, Id id, Supplier<X> s) {
		Reg<T> reg = (Reg<T>) defers.computeIfAbsent(registry, __ -> createReg(this, registry));
		return reg.reg(id, s);
	}

	public abstract <T> Reg<T> createReg(I4 i4, Registry<T> registry);
}
