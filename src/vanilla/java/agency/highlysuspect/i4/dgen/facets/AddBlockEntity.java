package agency.highlysuspect.i4.dgen.facets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import agency.highlysuspect.i4.dgen.facet.Facet;
import agency.highlysuspect.i4.dgen.facet.FacetHolder;
import agency.highlysuspect.i4.dgen.gen.RtContext;
import agency.highlysuspect.i4.ignos.Latch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Facet
public class AddBlockEntity<T extends BlockEntity> {
	public BiFunction<BlockPos, BlockState, T> factory;
	public Latch<BlockEntityType<T>> latch;
	public List<Latch<? extends Block>> blocks = new ArrayList<>();

	public AddBlockEntity<T> factory(BiFunction<BlockPos, BlockState, T> factory) {
		this.factory = factory;
		return this;
	}

	public AddBlockEntity<T> latch(Latch<BlockEntityType<T>> latch) {
		this.latch = latch;
		return this;
	}

	@SafeVarargs
	public final AddBlockEntity<T> addBlocks(Latch<? extends Block>... blocks) {
		this.blocks.addAll(List.of(blocks));
		return this;
	}

	@SuppressWarnings("unchecked")
	public static void handle(FacetHolder everyFacet, RtContext ctx) {
		//collate
		Map<Latch<BlockEntityType<?>>, List<AddBlockEntity<?>>> map = new HashMap<>();
		everyFacet.forEach(AddBlockEntity.class, abe -> map.computeIfAbsent(abe.latch, __ -> new ArrayList<>()).add(abe));

		map.values().forEach(list -> {
			//pick one with a nonnull factory
			//(this allows you to "append" blocks to BE types by leaving off the factory)
		  //TODO maybe check that the "creators" are all the same lol
			AddBlockEntity<?> rep = list.stream().filter(abe -> abe.factory != null).findAny().orElseThrow();
			//register the block entity type, broken out into a function to name T
			doIt(everyFacet, ctx, rep, list);
		});
	}

	private static <T extends BlockEntity> void doIt(FacetHolder everyFacet, RtContext ctx, AddBlockEntity<T> abe, List<AddBlockEntity<?>> everyone) {
		everyFacet.put(new Register<BlockEntityType<?>, BlockEntityType<T>>())
			.latch(abe.latch)
			.thing(() -> ctx.makeBlockEntityType(abe.factory,
				//collect the list of blocks for the block entity type
				everyone.stream()
					.flatMap(it -> it.blocks.stream())
					.map(Latch::get)
					.distinct()
					.toArray(Block[]::new)
			));
	}
}
