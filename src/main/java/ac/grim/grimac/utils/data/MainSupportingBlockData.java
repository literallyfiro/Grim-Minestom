package ac.grim.grimac.utils.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
public class MainSupportingBlockData {
    @Nullable
    Point blockPos;
    boolean onGround;

    public boolean lastOnGroundAndNoBlock() {
        return blockPos == null && onGround;
    }
}
