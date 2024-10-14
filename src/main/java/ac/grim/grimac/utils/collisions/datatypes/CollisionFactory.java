package ac.grim.grimac.utils.collisions.datatypes;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;

public interface CollisionFactory {
    CollisionBox fetch(GrimPlayer player, ClientVersion version, MinestomWrappedBlockState block, int x, int y, int z);
}
