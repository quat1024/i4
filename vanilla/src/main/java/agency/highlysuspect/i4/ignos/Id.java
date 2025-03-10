package agency.highlysuspect.i4.ignos;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.UnaryOperator;

import agency.highlysuspect.i4.I4;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

//mutable btw
public final class Id implements Comparable<Id> {
	public Id(ResourceLocation minecraft) {
		this(minecraft.getNamespace(), minecraft.getPath());
	}

	public Id(@NotNull String namespace, @NotNull String path) {
		this.namespace = Objects.requireNonNull(namespace);
		this.path = Objects.requireNonNull(path);
	}

	public static Id parse(@NotNull String both) {
		String[] split = both.split(":");
		if(split.length == 2) return new Id(split[0], split[1]);
		else throw new IllegalArgumentException("weird id: " + both);
	}

	public static Id mc(@NotNull String path) {
		return new Id("minecraft", path);
	}

	public static Id i4(@NotNull String path) {
		return new Id(I4.MODID, path);
	}

	public @NotNull String namespace, path;
	//name clashes with the getters >.>
	public static final Comparator<Id> COMPARATOR = Comparator.<Id, String>comparing(Id::namespace).thenComparing(id -> id.path());

	public @NotNull String namespace() {
		return namespace;
	}

	public @NotNull String path() {
		return path;
	}

	public Id namespace(@NotNull String namespace) {
		this.namespace = namespace;
		return this;
	}

	public Id path(@NotNull String path) {
		this.path = path;
		return this;
	}

	public Id mapPath(UnaryOperator<@NotNull String> op) {
		return new Id(namespace, op.apply(path));
	}

	public Id prefixPath(@NotNull String prefix) {
		if(prefix.endsWith("/")) return new Id(namespace, prefix + path);
		else return new Id(namespace, prefix + "/" + path);
	}

	public String toLangKey(@NotNull String domain) {
		return domain + "." + namespace + "." + path.replace('/', '.');
	}

	public ResourceLocation toMinecraft() {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

	public String toStringOmitMc() {
		return "minecraft".equals(namespace) ? path : toString();
	}

	@Override
	public String toString() {
		return namespace + ":" + path;
	}

	@Override
	public int compareTo(@NotNull Id o) {
		return COMPARATOR.compare(this, o);
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		else if(!(o instanceof Id id)) return false;
		else return namespace.equals(id.namespace) && path.equals(id.path);
	}

	@Override
	public int hashCode() {
		return 31 * namespace.hashCode() + path.hashCode();
	}
}
