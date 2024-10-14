package ac.grim.grimac.utils.data.tags;

import net.minestom.server.gamedata.tags.Tag;
import net.minestom.server.utils.NamespaceID;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class SyncedTag<T> {

    private final NamespaceID location;
    private final Set<T> values;
    private final Function<NamespaceID, T> remapper;

    private SyncedTag(NamespaceID location, Function<NamespaceID, T> remapper, Set<T> defaultValues) {
        this.location = location;
        this.values = new HashSet<>();
        this.remapper = remapper;
        this.values.addAll(defaultValues);
    }

    public static <T> Builder<T> builder(NamespaceID location) {
        return new Builder<>(location);
    }

    public NamespaceID location() {
        return location;
    }

    public boolean contains(T value) {
        return values.contains(value);
    }

    public void readTagValues(Tag tag) {
        // Server is sending tag replacement, clear default values.
        values.clear();
        for (NamespaceID id : tag.getValues()) {
            values.add(remapper.apply(id));
        }
    }

    public static final class Builder<T> {
        private final NamespaceID location;
        private Function<NamespaceID, T> remapper;
        private Set<T> defaultValues;

        private Builder(NamespaceID location) {
            this.location = location;
        }

        public Builder<T> remapper(Function<NamespaceID, T> remapper) {
            this.remapper = remapper;
            return this;
        }

        public Builder<T> defaults(Set<T> defaultValues) {
            this.defaultValues = defaultValues;
            return this;
        }

        public SyncedTag<T> build() {
            return new SyncedTag<>(location, remapper, defaultValues);
        }
    }
}
