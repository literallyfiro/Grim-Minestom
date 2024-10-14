package ac.grim.grimac.utils.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Point;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BlockPrediction {
    List<Point> forBlockUpdate;
    Point blockPosition;
    int originalBlockId;
    Point playerPosition;
}
