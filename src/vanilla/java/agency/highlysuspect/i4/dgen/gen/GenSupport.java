package agency.highlysuspect.i4.dgen.gen;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

import agency.highlysuspect.i4.ignos.Id;
import agency.highlysuspect.i4.ignos.Latch;
import agency.highlysuspect.i4.ignos.RegType;
import org.jetbrains.annotations.Nullable;

public class GenSupport {
	public static Gen makeAndSetInstance(Class<? extends Gen> clas) {
		try {
			Field f = findInstanceField(clas);
			//no static instance field
			if(f == null) return clas.getConstructor().newInstance();

			//existing instance (public static Gen blah = new Gen())
			Object got = f.get(null);
			if(got != null) return (Gen) got;

			//static instance field but not set -> set the field
			Gen newInst = clas.getConstructor().newInstance();
			f.set(null, newInst);
			return newInst;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static @Nullable Field findInstanceField(Class<? extends Gen> clazz) {
		for(Field field : clazz.getFields()) {
			if(
				Modifier.isStatic(field.getModifiers()) &&
				Gen.class.isAssignableFrom(field.getType()) &&
				field.getName().toLowerCase(Locale.ROOT).equals("instance")
			) {
				return field;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T, X extends T> Latch<X> reflectivelyFindLatch(RegType<T> reg, Gen what) {
		for(Field field : what.getClass().getFields()) {

			String name = field.getName().toLowerCase(Locale.ROOT);

			if(!name.equals("id") && !name.endsWith("latch") && !name.startsWith("latch")) continue;
			if(Modifier.isAbstract(field.getDeclaringClass().getModifiers())) continue;
			if(Modifier.isTransient(field.getModifiers())) continue;

			try {

				if(field.getType() == Latch.class) {
					Latch<?> hmm = (Latch<?>) field.get(null);
					if(hmm.regType == reg) return (Latch<X>) hmm;
					else continue;
				}

				if(field.getType() == Id.class) {
					return Latch.open(reg, (Id) field.get(what));
				}

				if(field.getType() == String.class) {
					return Latch.open(reg, Id.parse((String) field.get(what)));
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		throw new IllegalArgumentException("Need an 'id' or 'latch' field");
	}
}
