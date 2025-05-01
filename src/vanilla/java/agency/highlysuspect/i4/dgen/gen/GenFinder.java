package agency.highlysuspect.i4.dgen.gen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class GenFinder {
	public Collection<Gen> findGens() {
		return findGensInternal()
			.flatMap(gen -> {
				List<Gen> fanout = new ArrayList<>(2);
				gen.fanout(g -> {
					g.toplevel = false;
					fanout.add(g);
				});
				gen.toplevel = true; //incase fanout(this) is called
				return fanout.stream();
			}).toList();
	}

	public abstract Stream<Gen> findGensInternal();

	/**
	 * @see agency.highlysuspect.i4.dgen.facets.AddServiceLoader
	 * @see agency.highlysuspect.i4.dgen.gens.GenFindGenServiceLoader
	 */
	public static class ServiceLoaderFinder extends GenFinder {
		@Override
		public Stream<Gen> findGensInternal() {
			return ServiceLoader.load(Gen.class).stream()
				.map(prov -> GenSupport.makeAndSetInstance(prov.type()));
		}
	}

	public static class ClassFileFinder extends GenFinder {
		public ClassFileFinder(Path base) {
			this.base = base;
		}

		private final Path base;

		@Override
		public Stream<Gen> findGensInternal() {
			try(Stream<Path> paths = Files.walk(base)) {
				return paths
					.filter(p -> p.getFileName().toString().endsWith(".class"))
					.map(this::classNameIfHasFindGen)
					.filter(Objects::nonNull)
					.map(this::loadAndConstruct)
					.toList() //buffer the list in-memory before we close the paths :/
					.stream();
			} catch (Exception e) {
				throw new RuntimeException("Failed to read gens from classpath", e);
			}
		}

		private @Nullable String classNameIfHasFindGen(Path path) {
			try {
				byte[] bytes = Files.readAllBytes(path);
				ClassReader reader = new ClassReader(bytes);
				reader.accept(new FindGenFinderVisitor(), ClassReader.SKIP_CODE);
				return null;
			} catch (Yes yes) {
				if(yes.classInternalName == null) return null;
				else return yes.classInternalName.replace('/', '.');
			} catch (No no) {
				return null;
			} catch (Exception e) {
				throw new RuntimeException("Failed to parse class " + path, e);
			}
		}

		@SuppressWarnings("unchecked")
		private @NotNull Gen loadAndConstruct(String className) {
			try {
				Class<?> clazz = Class.forName(className);
				if(Gen.class.isAssignableFrom(clazz)) {
					return GenSupport.makeAndSetInstance((Class<? extends Gen>) clazz);
				} else {
					throw new RuntimeException("class " + className + " not an instance of Gen");
				}
			} catch (Exception e) {
				throw new RuntimeException("failed to load class " + className, e);
			}
		}

		private static class FindGenFinderVisitor extends ClassVisitor {
			private static final String findGenDesc = Type.getDescriptor(FindGen.class);
			private String classInternalName;

			public FindGenFinderVisitor() {
				super(Opcodes.ASM9);
			}

			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
				this.classInternalName = name;
			}

			@Override
			public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
				if(findGenDesc.equals(descriptor)) throw new Yes(classInternalName); //found @FindGen on this class
				return null;
			}

			//if we reach these there is definitely no @FindGen annotation
			@Override
			public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
				throw new No();
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
				throw new No();
			}

			@Override
			public void visitEnd() {
				throw new No();
			}
		}

		private static class Yes extends RuntimeException {
			public Yes(String classInternalName) {
				this.classInternalName = classInternalName;
			}
			String classInternalName;
		}
		private static class No extends RuntimeException {}
	}
}
