package ac.grim.grimac.utils.data;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minestom.server.entity.EntityType;

@Data
@RequiredArgsConstructor
public class TrackerData {
    @NonNull
    double x, y, z;
    @NonNull
    float xRot, yRot;
    @NonNull
    EntityType entityType;
    @NonNull
    int lastTransactionHung;
    int legacyPointEightMountedUpon;
}
