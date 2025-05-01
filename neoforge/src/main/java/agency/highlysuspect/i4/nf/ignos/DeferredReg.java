package agency.highlysuspect.i4.nf.ignos;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.Reg;
import com.google.common.base.Preconditions;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DeferredReg<T> extends Reg<T> {
	public DeferredReg(I4 i4, Registry<T> registry, IEventBus modBus) {
		super(i4, registry);
		
		dr = DeferredRegister.create(registry, I4.MODID);
		dr.register(modBus);
	}

	DeferredRegister<T> dr;
	Map<DeferredHolder<T, ?>, List<Latch<? extends T>>> latchMap = new HashMap<>();
	
	@Override
	public <X extends T> Latch<X> defer(Latch<X> latch, Supplier<X> sup) {
		Preconditions.checkArgument(latch.id.namespace.equals(I4.MODID), "NF DeferredReg only supports one namespace");
		
		if(latch.isShut()) {
			throw new IllegalStateException();
		}
		
		DeferredHolder<T, X> dholder = dr.register(latch.id.path, sup);
		
		return latch;
	}
	
	public void finishRegistration() {
		latchMap.forEach(this::finish2);
	}
	
	private <X extends T> void finish2(DeferredHolder<T, ?> dh, List<Latch<? extends T>> latches) {
		finish((DeferredHolder<T, X>) dh, (List<Latch<X>>) (Object) latches);
	}
	
	private <X extends T> void finish(DeferredHolder<T, X> dh, List<Latch<X>> latches) {
		X thing = dh.get();
		for(Latch<X> latch : latches) latch.shut(thing);
	}
}
