package ac.grim.grimac.utils.data;

import ac.grim.grimac.utils.RelativeFlag;
import ac.grim.grimac.utils.vector.Vector3d;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class TeleportData {
    Vector3d location;
    RelativeFlag flags;
    @Setter
    int transaction;
    @Setter
    int teleportId;

    public boolean isRelative() {
        return isRelativeX() || isRelativeY() || isRelativeZ();
    }

    public boolean isRelativeX() {
        return flags.isSet(RelativeFlag.X.getMask());
    }

    public boolean isRelativeY() {
        return flags.isSet(RelativeFlag.Y.getMask());
    }

    public boolean isRelativeZ() {
        return flags.isSet(RelativeFlag.Z.getMask());
    }
}
