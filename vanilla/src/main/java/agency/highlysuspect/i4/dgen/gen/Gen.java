package agency.highlysuspect.i4.dgen.gen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import agency.highlysuspect.i4.dgen.facet.FacetHolder;

public class Gen extends FacetHolder {
	public Gen() {
		this.genActions = findActions(genParameterShape);
		this.rtActions = findActions(rtParameterShape);
	}

	public List<Consumer<GenContext>> genActions;
	public List<Consumer<RtContext>> rtActions;

	public void invokeGenActions(GenContext ctx) {
		genActions.forEach(act -> act.accept(ctx));
	}

	public void invokeRtActions(RtContext rt) {
		rtActions.forEach(act -> act.accept(rt));
	}

	private static final Class<?>[] genParameterShape = new Class<?>[] { GenContext.class };
	private static final Class<?>[] rtParameterShape = new Class<?>[] { RtContext.class };

	public <T> List<Consumer<T>> findActions(Class<?>[] shape) {
		List<Consumer<T>> actions = new ArrayList<>(4);
		for(Method method : getClass().getMethods()) {
			//skip my own methods, lol
			if(method.getDeclaringClass() == Gen.class) continue;

			if(Arrays.equals(method.getParameterTypes(), shape)) {
				actions.add(makeCall(this, method));
			}
		}
		return actions;
	}

	private static <T> Consumer<T> makeCall(Object receiver, Method m) {
		return t -> {
			try {
				m.invoke(receiver, t);
			} catch (Exception e) {
				throw new RuntimeException("Failed to call action", e);
			}
		};
	}
}
