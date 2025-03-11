package agency.highlysuspect.i4.dgen.gen;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import agency.highlysuspect.i4.ignos.Id;
import org.jetbrains.annotations.Nullable;

public class GenSupport {
	public static @Nullable Id reflectivelyFindId(Object what) {
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
