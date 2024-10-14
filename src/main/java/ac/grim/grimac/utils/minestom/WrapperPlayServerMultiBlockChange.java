package ac.grim.grimac.utils.minestom;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.network.packet.server.play.MultiBlockChangePacket;

// from packetevents
public class WrapperPlayServerMultiBlockChange {
    private Point chunkPosition;
    private EncodedBlock[] blockData;

    public WrapperPlayServerMultiBlockChange(MultiBlockChangePacket packet) {
        read(packet);
    }

    public WrapperPlayServerMultiBlockChange(Point chunkPosition, EncodedBlock[] blockData) {
        this.chunkPosition = chunkPosition;
        this.blockData = blockData;
    }

    public void read(MultiBlockChangePacket packet) {
        long encodedPosition = packet.chunkSectionPosition();

        int sectionX = (int) (encodedPosition >> 42);
        int sectionY = (int) (encodedPosition << 44 >> 44);
        int sectionZ = (int) (encodedPosition << 22 >> 42);
        chunkPosition = new Vec(sectionX, sectionY, sectionZ);

        blockData = new EncodedBlock[packet.blocks().length];
        for (int i = 0; i < blockData.length; i++) {
            blockData[i] = new EncodedBlock(chunkPosition, packet.blocks().length);
        }
    }

    public MultiBlockChangePacket toMinestomPacket() {
        long encodedPos = 0;
        encodedPos |= (chunkPosition.blockX() & 0x3FFFFFL) << 42;
        encodedPos |= (chunkPosition.blockZ() & 0x3FFFFFL) << 20;
        encodedPos |= (chunkPosition.blockY() & 0xFFFFFL);

        long[] blocks = new long[blockData.length];
        for (int i = 0; i < blockData.length; i++) {
            blocks[i] = blockData[i].toLong();
        }
        return new MultiBlockChangePacket(encodedPos, blocks);
    }

    public EncodedBlock[] getBlocks() {
        return blockData;
    }

    public void setBlocks(EncodedBlock[] blocks) {
        this.blockData = blocks;
    }

    @Getter
    @Setter
    public static class EncodedBlock {
        private int blockID;
        private int x;
        private int y;
        private int z;

        public EncodedBlock(int blockID, int x, int y, int z) {
            this.blockID = blockID;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public EncodedBlock(MinestomWrappedBlockState blockState, int x, int y, int z) {
            this(blockState.getGlobalId(), x, y, z);
        }

        public EncodedBlock(Point chunk, long data) {
            short position = (short) (data & 0xFFFL);

            x = (chunk.blockX() << 4) + (position >>> 8 & 0xF);
            y = (chunk.blockY() << 4) + (position & 0xF);
            z = (chunk.blockZ() << 4) + (position >>> 4 & 0xF);

            this.blockID = (int) (data >>> 12);
        }

        public long toLong() {
            return (long) blockID << 12 | (x & 0xF) << 8 | (z & 0xF) << 4 | (y & 0xF);
        }

        public int getBlockId() {
            return blockID;
        }

        public void setBlockId(int blockID) {
            this.blockID = blockID;
        }

        public MinestomWrappedBlockState getBlockState() {
            return MinestomWrappedBlockState.getByGlobalId(blockID);
        }

        public void setBlockState(MinestomWrappedBlockState blockState) {
            blockID = blockState.getGlobalId();
        }
    }
}
