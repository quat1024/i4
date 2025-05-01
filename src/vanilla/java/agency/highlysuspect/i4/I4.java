package agency.highlysuspect.i4;

import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.facets.AddBlockEntity;
import agency.highlysuspect.i4.dgen.facets.Register;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenFinder;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.Reg;
import agency.highlysuspect.i4.ignos.RegType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class I4 implements RtContext {
	public static final String MODID = "i4";
	public static final Logger LOG = LoggerFactory.getLogger("i4");

	public static I4 INSTANCE;

	protected Map<RegType<?>, Reg<?>> regHelpers = new HashMap<>();

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
		FacetHolder everyFacet = new FacetHolder().merge(gens);

		//handle facets
		AddBlockEntity.handle(everyFacet, this); //has to come before Register, since it works by adding Register facets
		Register.handle(everyFacet, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> Reg<T> getOrCreateRegHelper(RegType<T> type) {
		return (Reg<T>) regHelpers.computeIfAbsent(type, __ -> createRegHelper(this, type));
	}
	
	@Override
	public <T, X extends T> Latch<X> register(Latch<X> latch, Supplier<X> s) {
		return getOrCreateRegHelper(latch.regType).defer(latch, s);
	}
	
	@Override
	public <T, X extends T> Latch<X> addLatch(Latch<X> latch) {
		return getOrCreateRegHelper(latch.regType).addLatch(latch);
	}
	
	public abstract <T> Reg<T> createRegHelper(I4 i4, RegType<T> regType);
}
