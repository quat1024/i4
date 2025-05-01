package agency.highlysuspect.i4;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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

public abstract class I4 implements RtContext {
	public static final String MODID = "i4";
	public static final Logger LOG = LoggerFactory.getLogger("i4");

	public static I4 INSTANCE;

	protected Map<RegType<?>, Reg<?>> defers = new HashMap<>();

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
		AddBlockEntity.handle(everyFacet, this); //has to come before Register.handle
		Register.handle(everyFacet, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, X extends T> Latch<X> register(Latch<X> latch, Supplier<X> s) {
		Reg<T> reg = (Reg<T>) defers.computeIfAbsent(latch.regType, __ -> createReg(this, latch.regType));
		return reg.defer(latch, s);
	}

	public abstract <T> Reg<T> createReg(I4 i4, RegType<T> regType);
}
