package agency.highlysuspect.i4.dgen.facets;

import agency.highlysuspect.i4.dgen.facet.Downcastable;
import agency.highlysuspect.i4.ignos.Id;

public class Idable<D> implements Downcastable<D> {
	public Id id;

	public Id id() {
		return id;
	}

	public D id(Id id) {
		this.id = id;
		return downcast();
	}
}
