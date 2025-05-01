package agency.highlysuspect.i4.nf.ignos;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NfReg<T> extends Reg<T> {
	public NfReg(I4 i4, Registry<T> registry) {
		super(i4, registry);
	}
	
	protected Map<Id, Supplier<? extends T>> thunks = new HashMap<>();
	
	@Override
	public <X extends T> Latch<X> defer(Latch<X> latch, Supplier<X> sup) {
		if(latch.isShut()) {
			throw new IllegalStateException("can't register already-shut latch " + latch);
		} else if(thunks.containsKey(latch.id)) {
			throw new IllegalStateException("already registered something using id " + latch);
		}
		
		addLatch(latch); //add this latch to the bookkeeping (shutLatches, etc)
		thunks.put(latch.id, sup);
		return latch;
	}
	
	public void doRegister(RegisterEvent e) {
		if(!e.getRegistryKey().equals(registry.key())) return;
		
		if(thunks == null) throw new IllegalStateException("NfReg#force called twice for " + registry.key());
		thunks.forEach((id, thunk) -> {
			//VANILLA REGISTRY on NEOFORGE???? It's more likely than you think.
			//Basically RegisterEvent.register just forces your thunk for you
			//but doesn't return the object it creates. I want access to the
			//object so I can call shutLatches
			T thing = thunk.get();
			Registry.register(registry, id.toMinecraft(), thing);
			
			//populate latches with the newly-created object
			shutLatches(id, thing);
		});
		thunks = null; //done
	}
}
