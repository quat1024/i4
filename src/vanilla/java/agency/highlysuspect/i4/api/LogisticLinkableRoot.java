package agency.highlysuspect.i4.api;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface LogisticLinkableRoot {
	boolean addConnection(BlockEntity other);
	void removeConnection(BlockEntity other);
	boolean isConnectedTo(BlockEntity other);
}
