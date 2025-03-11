package agency.highlysuspect.i4.fabric.ignos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.Registry;

public class FabricReg<T> extends Reg<T> {
	public FabricReg(I4 i4, Registry<T> registry) {
		super(i4, registry);
	}

	private final List<FabricHandle<? extends T>> handles = new ArrayList<>();

	@Override
	public <X extends T> Handle<X> reg(Id id, Supplier<X> sup) {
		FabricHandle<X> handle = new FabricHandle<>(id, sup);
		handles.add(handle);
		return handle;
	}

	public void forEach(Consumer<FabricHandle<? extends T>> op) {
		handles.forEach(op);
	}

	public class FabricHandle<X extends T> implements Reg.Handle<X> {
		public FabricHandle(Id id, Supplier<X> supp) {
			this.id = id;
			this.supp = supp;
		}

		private final Id id;
		private X thing;
		private Supplier<X> supp;

		public void doRegister() {
			thing = supp.get();
			supp = null;
			Registry.register(registry, id.toMinecraft(), thing);
		}

		@Override
		public Id getId() {
			return id;
		}

		@Override
		public X get() {
			if(thing == null) throw new RuntimeException("Can't access " + id + " before it's registered");
			return thing;
		}
	}
}
