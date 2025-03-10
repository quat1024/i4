package agency.highlysuspect.i4.dgen.gen;

import java.util.function.Supplier;

import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Reg;
import net.minecraft.core.Registry;

public interface RtContext {
	<T, X extends T> Reg.Handle<X> register(Registry<T> registry, Id id, Supplier<X> s);
}
