package agency.highlysuspect.i4.nf.ignos;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.Reg;
import com.google.common.base.Preconditions;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DeferredReg<T> extends Reg<T> {
	public DeferredReg(I4 i4, Registry<T> registry, IEventBus modBus) {
		super(i4, registry);
		
		dr = DeferredRegister.create(registry, I4.MODID);
		dr.register(modBus);
		
		modBus.addListener((RegisterEvent e) -> {
			if(e.getRegistry() == registry) {
				finishRegistration();
			}
		});
	}

	protected final DeferredRegister<T> dr;
	protected List<DeferredHolder<T, ?>> holders = new ArrayList<>();
	
	@Override
	public <X extends T> Latch<X> defer(Latch<X> latch, Supplier<X> sup) {
		Preconditions.checkArgument(latch.id.namespace.equals(I4.MODID), "NF DeferredReg only supports one namespace");
		Preconditions.checkArgument(!latch.isShut(), "can't register an already-shut latch");
		
		//keep track of this latch
		addLatch(latch);
		
		//create a DeferredHolder which closes any relevant latches when registered
		//strictly speaking, this is too early to call "shut" (since it's not registered yet),
		//but it's like 1 millisecond from being registered and this is as close as we can get
		DeferredHolder<T, X> dholder = dr.register(latch.id.path, () -> {
			X thing = sup.get();
			shutLatches(latch.id, thing);
			return thing;
		});
		
		holders.add(dholder);
		
		return latch;
	}
	
	public void finishRegistration() {
		if(holders == null) throw new IllegalStateException("finishRegistration called twice");
		
		//shut any straggling latches
		//TODO: should be impossible?
		holders.forEach(dh -> shutLatches(new Id(dh.getId()), dh.get()));
		holders = null;
	}
}
