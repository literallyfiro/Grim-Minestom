package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.manager.init.Initable;

public class ViaVersion implements Initable {
    @Override
    public void start() {
        // Viaversion does not exist on minestom
        /*if (!ViaVersionUtil.isAvailable()) return;
        if (Via.getConfig().getValues().containsKey("fix-1_21-placement-rotation") && Via.getConfig().fix1_21PlacementRotation()) {
            LogUtil.warn("GrimAC has detected that you are using ViaVersion with the `fix-1_21-placement-rotation` option enabled.");
            LogUtil.warn("This option is known to cause issues with GrimAC and may result in false positives and bypasses.");
            LogUtil.warn("Please disable this option in your ViaVersion configuration to prevent these issues.");
        }*/
    }
}
