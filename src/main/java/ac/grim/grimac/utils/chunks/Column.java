package ac.grim.grimac.utils.chunks;

import net.minestom.server.instance.Chunk;

public class Column {
    public final int x;
    public final int z;
    public final int transaction;
    public Chunk[] chunks;

    public Column(int x, int z, Chunk[] chunks, int transaction) {
        this.chunks = chunks;
        this.x = x;
        this.z = z;
        this.transaction = transaction;
    }

    public Chunk[] getChunks() {
        return chunks;
    }

    // This ability was removed in 1.17 because of the extended world height
    // Therefore, the size of the chunks are ALWAYS 16!
    public void mergeChunks(Chunk[] toMerge) {
        for (int i = 0; i < 16; i++) {
            if (toMerge[i] != null) chunks[i] = toMerge[i];
        }
    }
}
