package ac.grim.grimac.utils.collisions.datatypes;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import net.minestom.server.instance.block.Block;

public interface HitBoxFactory {
    CollisionBox fetch(GrimPlayer player, Block heldItem, ClientVersion version, MinestomWrappedBlockState block, int x, int y, int z);
}
