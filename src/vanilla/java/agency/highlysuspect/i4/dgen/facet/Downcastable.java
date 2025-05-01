package agency.highlysuspect.i4.dgen.facet;

@SuppressWarnings("unchecked")
public interface Downcastable<D> {
	default D downcast() {
		return (D) this;
	}
}
