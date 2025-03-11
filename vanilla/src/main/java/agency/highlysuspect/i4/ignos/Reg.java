package agency.highlysuspect.i4.ignos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import agency.highlysuspect.i4.I4;
import net.minecraft.core.Registry;

public abstract class Reg<T> {
	public Reg(I4 i4, Registry<T> registry) {
		this.i4 = i4;
		this.registry = registry;
	}

	protected final I4 i4;
	protected final Registry<T> registry;
	protected final Map<Id, List<Latch<?>>> openLatches = new HashMap<>();

	public abstract <X extends T> Latch<X> defer(Latch<X> latch, Supplier<X> sup);

	@SuppressWarnings("unchecked")
	public <X extends T> Latch<X> addLatch(Latch<X> latch) {
		if(latch.isOpen()) {
			if(registry.containsKey(latch.id.toMinecraft())) {
				//already registered -> shut the latch
				T thing = registry.get(latch.id.toMinecraft());
				//unsound cast b/c the ID being correct doesn't guarantee the resource has the right type
				latch.shut((X) thing);
			} else {
				//not registered yet -> save this latch for later
				openLatches.computeIfAbsent(latch.id, __ -> new ArrayList<>()).add(latch);
			}
		}

		return latch;
	}

	@SuppressWarnings("unchecked")
	public void shutLatches(Id id, T thing) {
		//are there any latches to begin with
		List<Latch<T>> latches = (List<Latch<T>>) (Object) openLatches.get(id);
		if(latches == null) return;

		//clear any already-shut latches (idk how this would happen tbh)
		latches.removeIf(Latch::isShut);
		if(latches.isEmpty()) {
			openLatches.remove(id);
			return;
		}

		//now all we have is open latches, shut them all
		latches.forEach(latch -> latch.shut(thing));

		openLatches.remove(id); //all latches are now shut
	}

	public interface Handle<T> extends Supplier<T> {
		Id getId();
	}

}
