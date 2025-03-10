package agency.highlysuspect.i4.nf.ignos;

import java.util.function.Supplier;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Reg;
import com.google.common.base.Preconditions;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeferredReg<T> extends Reg<T> {
	public DeferredReg(I4 i4, Registry<T> registry) {
		super(i4, registry);
		dr = DeferredRegister.create(registry, I4.MODID);
	}

	DeferredRegister<T> dr;

	@Override
	public <X extends T> Handle<X> reg(Id id, Supplier<X> sup) {
		Preconditions.checkArgument(id.namespace.equals(I4.MODID), "NF ReferredReg only supports one namespace");

		DeferredHolder<T, X> dholder = dr.register(id.path, sup);
		return new DeferredHolderHandle<>(id, dholder);
	}

	public record DeferredHolderHandle<R, T extends R>(Id getId, DeferredHolder<R, T> dh) implements Reg.Handle<T> {
		@Override
		public T get() {
			return dh.get();
		}
	}
}
