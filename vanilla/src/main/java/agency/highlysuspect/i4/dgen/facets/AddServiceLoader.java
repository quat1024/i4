package agency.highlysuspect.i4.dgen.facets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agency.highlysuspect.i4.dgen.facet.Downcastable;
import agency.highlysuspect.i4.dgen.facet.Facet;
import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenContext;

@Facet
public class AddServiceLoader implements Downcastable<AddServiceLoader> {
	public String service;
	public String className;

	public AddServiceLoader service(String service) {
		this.service = service;
		return this;
	}

	public AddServiceLoader service(Class<?> service) {
		this.service = service.getName();
		return this;
	}

	/**
	 * The service loader used by GenFinder.ServiceLoaderFinder.
	 * @see agency.highlysuspect.i4.dgen.gen.GenFinder.ServiceLoaderFinder
	 */
	public AddServiceLoader genService() {
		return service(Gen.class);
	}

	public AddServiceLoader className(String className) {
		this.className = className;
		return this;
	}

	public static void handle(FacetHolder everyFacet, GenContext ctx) {
		Map<String, List<String>> allServices = new HashMap<>();

		everyFacet.forEach(AddServiceLoader.class, asl -> {
			if(asl.service == null || asl.className == null) throw new IllegalStateException("Null classname or service in " + asl);
			allServices.computeIfAbsent(asl.service, __ -> new ArrayList<>()).add(asl.className);
		});

		allServices.forEach((svc, services) -> ctx.writeFile("/META-INF/services/" + svc, String.join("\r\n", services)));
	}
}
