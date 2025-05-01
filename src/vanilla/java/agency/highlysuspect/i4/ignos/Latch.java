package agency.highlysuspect.i4.ignos;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a latch is like a ResourceKey, pairing a registry with an id inside that registry,
 * but additionally it might hold an instance of the corresponding object.
 * we say a latch is "open" when it doesn't have the object yet, and "shut" when it does.
 * the only thing you can do to an open latch is shut it with a registered object.
 *
 * tbh it's like Holder but not mojang controlled...
 */
public class Latch<T> {
	private Latch(RegType<? super T> regType, Id id, @Nullable T thing) {
		this.regType = regType;
		this.id = id;
		this.thing = thing;
	}

	public static <T> Latch<T> open(RegType<? super T> regType, Id id) {
		Preconditions.checkNotNull(regType, "regtype is null");
		Preconditions.checkNotNull(id, "id is null");

		return new Latch<>(regType, id, null);
	}

	public static <T> Latch<T> shut(RegType<? super T> regType, Id id, @NotNull T thing) {
		Preconditions.checkNotNull(regType, "regtype is null");
		Preconditions.checkNotNull(id, "id is null");
		Preconditions.checkNotNull(thing, "can't construct shut latch for %s with null object", id);
		return new Latch<>(regType, id, thing);
	}

	public final RegType<? super T> regType;
	public final Id id;
	private @Nullable T thing;
	//private Supplier<T> lazy;

	public T get() {
//		if(lazy != null) {
//			thing = lazy.get();
//			lazy = null;
//		}
//
		if(thing == null) throw new IllegalStateException("tried to read from open latch " + this);
		return thing;
	}

	public void shut(T registered) {
		if(thing != null) throw new IllegalStateException("already shut latch " + this);
		if(registered == null) throw new IllegalArgumentException("tried to shut latch with null " + this);

		thing = registered;
	}
	
//	public void lazilyShut(Supplier<T> lazy) {
//		if(thing != null) throw new IllegalStateException("already shut latch " + this);
//		if(lazy == null) throw new IllegalArgumentException("tried to lazy-shut latch with null " + this);
//
//		this.lazy = lazy;
//	}

	public boolean isOpen() {
		return thing == null;
	}

	public boolean isShut() {
		return thing != null;
	}

	@Override
	public String toString() {
		return regType.toString() + "->" + id + " " + (thing == null ? "(open)" : thing);
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Latch<?> latch = (Latch<?>) o;
		return regType.equals(latch.regType) && id.equals(latch.id);
	}

	@Override
	public int hashCode() {
		return 31 * regType.hashCode() + id.hashCode();
	}
}
