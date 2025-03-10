package agency.highlysuspect.i4.dgen.facet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class FacetHolder {
	protected final Map<Class<?>, List<Object>> facets = new HashMap<>();

	public <T> T put(T facet) {
		Class<?> key = facetKey(facet.getClass());
		if(key == null) throw new IllegalArgumentException("No @Facet in inheritance chain: " + facet.getClass().getName());
		return putUnchecked(key, facet);
	}

	public <T> List<? extends T> get(Class<T> key) {
		if(key != facetKey(key)) throw new IllegalArgumentException("Not a @Facet class: " + key.getName());
		List<? extends T> list = (List<? extends T>) facets.get(key);
		return list == null ? List.of() : list;
	}

	public <T> void forEach(Class<T> key, Consumer<? super T> action) {
		get(key).forEach(action);
	}

	public FacetHolder addAll(FacetHolder other) {
		other.facets.forEach((key, theirFacets) -> theirFacets.forEach(theirFacet -> putUnchecked(key, theirFacet)));
		return this;
	}

	public FacetHolder addAll(Collection<? extends FacetHolder> others) {
		others.forEach(this::addAll);
		return this;
	}

	protected Class<?> facetKey(Class<?> c) {
		if(c == null) return null;
		else if(c.isAnnotationPresent(Facet.class)) return c;
		else return facetKey(c.getSuperclass());
	}

	protected <T> T putUnchecked(Class<?> key, T facet) {
		facets.computeIfAbsent(key, __ -> new ArrayList<>(4)).add(facet);
		return facet;
	}
}
