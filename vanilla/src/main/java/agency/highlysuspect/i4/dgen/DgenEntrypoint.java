package agency.highlysuspect.i4.dgen;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.facets.AddServiceLoader;
import agency.highlysuspect.i4.dgen.facets.ItemModel;
import agency.highlysuspect.i4.dgen.facets.Lang;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenContext;
import agency.highlysuspect.i4.dgen.gen.GenFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DgenEntrypoint implements GenContext {
	public static final Logger LOG = LoggerFactory.getLogger("i4-dgen");

	public Path vanillaGeneratedResourcesDir;
	public Path selfScanDir;
	public Collection<Gen> gens;

	public static void main(String[] args) throws Exception {
		LOG.info("Hello from DgenEntrypoint");
		for(String arg : args) LOG.info("\tArg {}", arg);
		LOG.info("");

		new DgenEntrypoint(args).go();
	}

	public DgenEntrypoint(String[] args) {
		this.vanillaGeneratedResourcesDir = Paths.get(args[0]);
		this.selfScanDir = Paths.get(args[1]);

		//find gens
		LOG.info("Selfscanning from {}", selfScanDir);
		GenFinder finder = new GenFinder.ClassFileFinder(selfScanDir);
//		GenFinder finder = new GenFinder.ServiceLoaderFinder();
		this.gens = finder.findGens();

		LOG.info("Found {} gens", gens.size());
		for(Gen gen : gens) LOG.info("\tGen: {}", gen.getClass());
	}

	public void go() throws Exception {
		//invoke gens
		for(Gen gen : gens) gen.gen(this);
		FacetHolder everyFacet = new FacetHolder().merge(gens);

		//handle facets
		ItemModel.handle(everyFacet, this);
		AddServiceLoader.handle(everyFacet, this);
		Lang.handle(everyFacet, this);
	}

	@Override
	public Collection<Gen> getGens() {
		return gens;
	}

	@Override
	public void writeFile(String subpath, String toWrite) {
		if(subpath.startsWith("/")) subpath = subpath.substring(1); //woops

		Path dest = vanillaGeneratedResourcesDir.resolve(subpath);
		try {
			if(Files.notExists(dest)) {
				LOG.info(" FRESH {}", dest);
				Files.createDirectories(dest.getParent());
				Files.writeString(dest, toWrite, StandardCharsets.UTF_8);
			} else {
				String existing = Files.readString(dest);
				if(!toWrite.equals(existing)) {
					LOG.info("CHANGE {}", dest);
					Files.writeString(dest, toWrite, StandardCharsets.UTF_8);
				} else {
					LOG.info("  SAME {}", dest);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
