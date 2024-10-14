package ac.grim.grimac.utils.nmsutil;

import net.minestom.server.entity.Metadata;

import java.util.Map;

public class WatchableIndexUtil {
    public static Metadata.Entry<?> getIndex(Map<Integer, Metadata.Entry<?>> objects, int index) {
        for (Map.Entry<Integer, Metadata.Entry<?>> integerEntryEntry : objects.entrySet()) {
            if (integerEntryEntry.getKey() == index) return integerEntryEntry.getValue();
        }
        return null;
    }
}
