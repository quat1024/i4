package agency.highlysuspect.i4.dgen.gens;

import agency.highlysuspect.i4.dgen.facets.AddServiceLoader;
import agency.highlysuspect.i4.dgen.gen.FindGen;
import agency.highlysuspect.i4.dgen.gen.Gen;
import agency.highlysuspect.i4.dgen.gen.GenContext;

/**
 * This gen looks at all the other gens, and if they require attention at runtime, adds it
 * to a serviceloader where ServiceLoaderFinder can pick up on it. That is the gen finder
 * used at runtime.
 *
 * At gen-time I use a way hackier method of finding gens (scanning the `classes/` dir)
 * which is not appropriate at runtime.
 *
 * @see agency.highlysuspect.i4.dgen.gen.GenFinder.ServiceLoaderFinder
 */
@FindGen
public class GenFindGenServiceLoader extends Gen {
	public void gen(GenContext ctx) {
		for(Gen gen : ctx.getGens()) {
			if(!gen.rtActions.isEmpty()) {
				put(new AddServiceLoader().genService().className(gen.getClass().getName()));
			}
		}
	}
}
