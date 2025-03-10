package agency.highlysuspect.i4.fabric;

import agency.highlysuspect.i4.I4;
import agency.highlysuspect.i4.ignos.Reg;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

public class I4Fabric extends I4 implements ModInitializer {
	public static I4Fabric LOADER_INST;

	@Override
	public void onInitialize() {
		LOADER_INST = this;
		LOG.info("Hello from I4Fabric");
		bootGame();
	}

	@Override
	public <T> Reg<T> createReg(I4 i4, Registry<T> registry) {
		return new Reg.Immediate<>(i4, registry);
	}
}
