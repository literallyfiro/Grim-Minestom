package ac.grim.grimac.utils.data;

import ac.grim.grimac.utils.vector.Vector3d;
import ac.grim.grimac.utils.vector.Vector3i;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BlockPrediction {
    List<Vector3i> forBlockUpdate;
    Vector3i blockPosition;
    int originalBlockId;
    Vector3d playerPosition;
}
