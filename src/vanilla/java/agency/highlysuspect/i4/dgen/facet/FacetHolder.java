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

	public void putAll(Object... facets) {
		for(Object facet : facets) if(facet != null) put(facet);
	}

	public <T> void delete(T facet) {
		Class<?> key = facetKey(facet.getClass());
		if(key == null) throw new IllegalArgumentException("No @Facet in inheritance chain: " + facet.getClass().getName());
		List<?> list = facets.get(key);
		if(list != null) list.remove(facet);
	}

	public <T> List<? extends T> getAll(Class<T> key) {
		if(key != facetKey(key)) throw new IllegalArgumentException("Not a @Facet class: " + key.getName());
		List<? extends T> list = (List<? extends T>) facets.get(key);
		return list == null ? List.of() : list;
	}

	public <T> void forEach(Class<T> key, Consumer<? super T> action) {
		getAll(key).forEach(action);
	}

	public <T> T getOne(Class<T> key) {
		List<? extends T> list = getAll(key);
		if(list.size() == 1) return list.getFirst();
		else throw new IllegalStateException("getOne failed for " + key + "; there are " + list.size() + " facets of this type");
	}

	public FacetHolder merge(FacetHolder other) {
		other.facets.forEach((key, theirFacets) -> theirFacets.forEach(theirFacet -> putUnchecked(key, theirFacet)));
		return this;
	}

	public FacetHolder merge(Collection<? extends FacetHolder> others) {
		others.forEach(this::merge);
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
