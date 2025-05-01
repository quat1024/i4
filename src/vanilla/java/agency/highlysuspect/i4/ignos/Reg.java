package agency.highlysuspect.i4.ignos;

import agency.highlysuspect.i4.I4;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Reg<T> {
	public Reg(I4 i4, Registry<T> registry) {
		this.i4 = i4;
		this.registry = registry;
	}

	protected final I4 i4;
	protected final Registry<T> registry;
	protected final Map<Id, List<Latch<?>>> openLatches = new HashMap<>();
	
	/**
	 * at some later time, construct "sup" and register it under "latch"
	 */
	public abstract <X extends T> Latch<X> defer(Latch<X> latch, Supplier<X> sup);
	
	/**
	 * populate a latch without pairing it with a thing to register
	 * TODO: is this useful to expose? do we ever need to create two latches for the same thing?
	 */
	@SuppressWarnings("unchecked")
	public <X extends T> Latch<X> addLatch(Latch<X> latch) {
		if(latch.isOpen()) {
			ResourceLocation idMc = latch.id.toMinecraft();
			if(registry.containsKey(idMc)) {
				//already registered -> shut the latch.
				//unsound cast b/c the ID being correct doesn't guarantee the resource has the right type
				latch.shut((X) registry.get(idMc));
			} else {
				//not registered yet -> save this latch for later
				openLatches.computeIfAbsent(latch.id, __ -> new ArrayList<>()).add(latch);
			}
		}

		return latch;
	}
	
	/**
	 * shut all latches for this thing, because it has now been constructed and we have access to it.
	 */
	@SuppressWarnings("unchecked")
	public void shutLatches(Id id, T registered) {
		//are there any latches to begin with
		List<Latch<T>> latches = (List<Latch<T>>) (Object) openLatches.get(id);
		if(latches == null) return;

		//clear any already-shut latches (idk how this would happen tbh)
		latches.removeIf(Latch::isShut);
		if(latches.isEmpty()) {
			openLatches.remove(id); //all latches were shut
			return;
		}

		//now all we have is open latches, shut them all
		latches.forEach(latch -> latch.shut(registered));
		openLatches.remove(id); //all latches are now shut
	}
}
