package agency.highlysuspect.i4.fabric.ignos;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.Registry;

public class FabricReg<T> extends Reg<T> {
	public FabricReg(I4 i4, Registry<T> registry) {
		super(i4, registry);
	}

	private final Map<Id, Supplier<? extends T>> creators = new HashMap<>();

	@Override
	public <X extends T> Latch<X> defer(Latch<X> latch, Supplier<X> creator) {
		if(latch.isShut()) {
			throw new IllegalStateException("Can't register already-shut latch " + latch);
		} else if(creators.containsKey(latch.id)) {
			throw new IllegalStateException("Duplicate registration of " + latch);
		} else {
			creators.put(latch.id, creator);
		}
		return addLatch(latch);
	}

	public void registerAll() {
		creators.forEach((id, creator) -> {
			T thing = creator.get();
			Registry.register(registry, id.toMinecraft(), thing);
			shutLatches(id, thing);
		});
	}
}
