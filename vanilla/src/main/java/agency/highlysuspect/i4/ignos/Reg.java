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

	public record UnboundId<T>(Id getId, String type) implements Handle<T> {
		@Override
		public T get() {
			throw new IllegalStateException("Unbound handle for " + type + " " + getId());
		}
	}
}
