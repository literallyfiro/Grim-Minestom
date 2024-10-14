package ac.grim.grimac.utils.data.tags;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.minestom.BlockTags;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.common.TagsPacket;
import net.minestom.server.utils.NamespaceID;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class stores tags that the client is aware of.
 */
public final class SyncedTags {

    private static final NamespaceID BLOCK = NamespaceID.from("minecraft:block");
    public static final NamespaceID CLIMBABLE = NamespaceID.from("minecraft:climbable");

    private final GrimPlayer player;
    private final Map<NamespaceID, Map<NamespaceID, SyncedTag<?>>> synced;

    public SyncedTags(GrimPlayer player) {
        this.player = player;
        this.synced = new HashMap<>();
        trackTags(BLOCK, Block::fromNamespaceId,
                SyncedTag.<Block>builder(CLIMBABLE).defaults(BlockTags.CLIMBABLE.getStates()));
    }

    @SafeVarargs
    private <T> void trackTags(NamespaceID location, Function<NamespaceID, T> remapper, SyncedTag.Builder<T>... syncedTags) {
        final Map<NamespaceID, SyncedTag<?>> tags = new HashMap<>(syncedTags.length);
        for (SyncedTag.Builder<T> syncedTag : syncedTags) {
            syncedTag.remapper(remapper);
            final SyncedTag<T> built = syncedTag.build();
            tags.put(built.location(), built);
        }
        synced.put(location, tags);
    }

    public SyncedTag<Block> block(Key tag) {
        final Map<NamespaceID, SyncedTag<?>> blockTags = synced.get(BLOCK);
        return (SyncedTag<Block>) blockTags.get(tag);
    }

    public void handleTagSync(TagsPacket tags) {
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_13)) return;
        tags.tagsMap().forEach((basicType, tagsList) -> {
            NamespaceID id = NamespaceID.from(basicType.getIdentifier());
            if (!synced.containsKey(id)) return;
            final Map<NamespaceID, SyncedTag<?>> syncedTags = synced.get(id);
            tagsList.forEach(tag -> {
                NamespaceID tagId = NamespaceID.from(tag.key());
                if (!syncedTags.containsKey(tagId)) return;
                syncedTags.get(tagId).readTagValues(tag);
            });
        });
//        tags.getTagMap().forEach((location, tagList) -> {
//            if (!synced.containsKey(location)) return;
//            final Map<ResourceLocation, SyncedTag<?>> syncedTags = synced.get(location);
//            tagList.forEach(tag -> {
//                if (!syncedTags.containsKey(tag.getKey())) return;
//                syncedTags.get(tag.getKey()).readTagValues(tag);
//            });
//        });
    }
}
