package agency.highlysuspect.i4.dgen.gen;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

import agency.highlysuspect.i4.ignos.Id;
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

	public static @Nullable Id reflectivelyFindId(Gen what) {
		for(Field field : what.getClass().getFields()) {
			if(!field.getName().equals("ID")) continue;
			if(Modifier.isAbstract(field.getDeclaringClass().getModifiers())) continue;
			if(Modifier.isTransient(field.getModifiers())) continue;

			if(field.getType() == Id.class) {
				try {
					return (Id) field.get(what);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			if(field.getType() == String.class) {
				try {
					return Id.parse((String) field.get(what));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}
}
