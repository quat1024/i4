package agency.highlysuspect.i4.nf;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Reg;
import agency.highlysuspect.i4.nf.ignos.DeferredReg;
import net.minecraft.core.Registry;
import net.neoforged.fml.common.Mod;

@Mod("i4")
public class I4NF extends I4 {
	public static I4NF LOADER_INST;

	public I4NF() {
		LOADER_INST = this;
		LOG.info("Hello from I4NF");
		handleGens();
	}

	@Override
	public <T> Reg<T> createReg(I4 i4, Registry<T> registry) {
		return new DeferredReg<>(i4, registry);
	}
}
