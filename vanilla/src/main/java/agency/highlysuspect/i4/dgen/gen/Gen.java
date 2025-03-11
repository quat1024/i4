package agency.highlysuspect.i4.dgen.gen;

import java.util.function.Consumer;

import agency.highlysuspect.i4.dgen.facet.FacetHolder;

public class Gen extends FacetHolder {
	public boolean toplevel = true;

	/**
	 * add more gens that weren't found via annotation
	 */
	public void fanout(Consumer<Gen> fanout) {
		fanout.accept(this);
	}

	public void gen(GenContext ctx) {
		//no-op
	}

	public void rt(RtContext rt) {
		//no-op
	}
}
