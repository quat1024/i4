package agency.highlysuspect.i4.ignos;

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

	public abstract <X extends T> Handle<X> reg(Id id, Supplier<X> sup);

	public interface Handle<T> extends Supplier<T> {
		Id getId();
	}

	/**
	 * Calls Registry.register immediately and doesn't bother with supplier nonsense.
	 * Ok for fabric
	 */
	public static class Immediate<T> extends Reg<T> {
		public Immediate(I4 i4, Registry<T> registry) {
			super(i4, registry);
		}

		@Override
		public <X extends T> Handle<X> reg(Id id, Supplier<X> sup) {
			X thing = sup.get();
			Registry.register(registry, id.toMinecraft(), thing);
			return new ImmediateHandle<>(id, thing);
		}
	}

	public record ImmediateHandle<T>(Id getId, T get) implements Handle<T> {}
}
