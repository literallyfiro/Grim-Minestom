package ac.grim.grimac.utils.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minestom.server.network.packet.client.ClientPacket;

@Data
@AllArgsConstructor
public class BlockPlaceSnapshot {
    ClientPacket wrapper;
    boolean sneaking;
}
