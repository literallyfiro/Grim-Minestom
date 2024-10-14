package ac.grim.grimac.utils.minestom;

import lombok.Getter;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ac.grim.grimac.utils.minestom.enums.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class MinestomWrappedBlockState {

    private Block block;
    private final Map<StateValue, Object> data = new HashMap<>(0);

    public MinestomWrappedBlockState(@Nullable Block block) {
        if (block == null) {
            System.out.println("block is null");
            return;
        }
        this.block = block;

        for (Map.Entry<String, String> stringStringEntry : block.properties().entrySet()) {
            final String key = stringStringEntry.getKey();
            final String value = stringStringEntry.getValue();

            try {
                StateValue stateValue = StateValue.byName(key);
                if (stateValue == null) {
                    System.out.println("Unknown block state: " + key);
                    continue;
                }
                this.data.put(stateValue, stateValue.getParser().apply(value.toUpperCase(Locale.ROOT)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @NotNull
    public static MinestomWrappedBlockState getDefaultState(Block type) {
        if (type == Block.AIR) return new MinestomWrappedBlockState(Block.AIR);
        final Block defaultState = type.defaultState();
        return new MinestomWrappedBlockState(defaultState);
    }

    public boolean isReplaceable() {
        return getType().equals(Block.AIR)
                || getType().equals(Block.CAVE_AIR)
                || getType().equals(Block.VOID_AIR)
                || getType().equals(Block.KELP_PLANT)
                || getType().equals(Block.CAVE_VINES_PLANT)
                || getType().equals(Block.PITCHER_PLANT)
                || getType().equals(Block.CHORUS_PLANT)
                || getType().equals(Block.TWISTING_VINES_PLANT)
                || getType().equals(Block.WEEPING_VINES_PLANT)
                || getType().equals(Block.WATER)
                || getType().equals(Block.BUBBLE_COLUMN)
                || getType().equals(Block.LAVA)
                || getType().equals(Block.SNOW)
                || getType().equals(Block.FIRE);
    }

    public Map<StateValue, Object> getInternalData() {
        return data;
    }

    // todo minestom block state
    public boolean isBlocking() {
        return true;
    }

    public int getGlobalId() {
        return block.id();
    }

    public Block getType() {
        return block;
    }

    // Begin all block data types
    public int getAge() {
        return (int) data.get(StateValue.AGE);
    }

    public void setAge(int age) {
        data.put(StateValue.AGE, age);
        block.properties().put(StateValue.AGE.getName(), String.valueOf(age));
    }

    public boolean isAttached() {
        return (boolean) data.get(StateValue.ATTACHED);
    }

    public void setAttached(boolean attached) {
        data.put(StateValue.ATTACHED, attached);
        block.properties().put(StateValue.ATTACHED.getName(), String.valueOf(attached));
    }

    public Attachment getAttachment() {
        return (Attachment) data.get(StateValue.ATTACHMENT);
    }

    public void setAttachment(Attachment attachment) {
        data.put(StateValue.ATTACHMENT, attachment);
        block.properties().put(StateValue.ATTACHMENT.getName(), attachment.name());
    }

    public Axis getAxis() {
        return (Axis) data.get(StateValue.AXIS);
    }

    public void setAxis(Axis axis) {
        data.put(StateValue.AXIS, axis);
        block.properties().put(StateValue.AXIS.getName(), axis.name());
    }

    public boolean isBerries() {
        return (boolean) data.get(StateValue.BERRIES);
    }

    public void setBerries(boolean berries) {
        data.put(StateValue.BERRIES, berries);
        block.properties().put(StateValue.BERRIES.getName(), String.valueOf(berries));
    }

    public int getBites() {
        return (int) data.get(StateValue.BITES);
    }

    public void setBites(int bites) {
        data.put(StateValue.BITES, bites);
        block.properties().put(StateValue.BITES.getName(), String.valueOf(bites));
    }

    public boolean isBottom() {
        return (boolean) data.get(StateValue.BOTTOM);
    }

    public void setBottom(boolean bottom) {
        data.put(StateValue.BOTTOM, bottom);
        block.properties().put(StateValue.BOTTOM.getName(), String.valueOf(bottom));
    }

    public int getCandles() {
        return (int) data.get(StateValue.CANDLES);
    }

    public void setCandles(int candles) {
        data.put(StateValue.CANDLES, candles);
        block.properties().put(StateValue.CANDLES.getName(), String.valueOf(candles));
    }

    public int getCharges() {
        return (int) data.get(StateValue.CHARGES);
    }

    public boolean isConditional() {
        return (boolean) data.get(StateValue.CONDITIONAL);
    }

    public int getDelay() {
        return (int) data.get(StateValue.DELAY);
    }

    public boolean isDisarmed() {
        return (boolean) data.get(StateValue.DISARMED);
    }

    public int getDistance() {
        return (int) data.get(StateValue.DISTANCE);
    }

    public boolean isDown() {
        return (boolean) data.get(StateValue.DOWN);
    }

    public void setDown(boolean down) {
        data.put(StateValue.DOWN, down);
        block.properties().put(StateValue.DOWN.getName(), String.valueOf(down));
    }

    public boolean isDrag() {
        return (boolean) data.get(StateValue.DRAG);
    }

    public boolean isDusted() {
        return (boolean) data.get(StateValue.DUSTED);
    }

    public int getEggs() {
        return (int) data.get(StateValue.EGGS);
    }

    public boolean isEnabled() {
        return (boolean) data.get(StateValue.ENABLED);
    }

    public boolean isExtended() {
        return (boolean) data.get(StateValue.EXTENDED);
    }

    public boolean isEye() {
        return (boolean) data.get(StateValue.EYE);
    }

    public Face getFace() {
        return (Face) data.get(StateValue.FACE);
    }

    public void setFace(Face face) {
        data.put(StateValue.FACE, face);
        block.properties().put(StateValue.FACE.getName(), face.name());
    }

    public BlockFace getFacing() {
        return (BlockFace) data.get(StateValue.FACING);
    }

    public void setFacing(BlockFace facing) {
        data.put(StateValue.FACING, facing);
        block.properties().put(StateValue.FACING.getName(), facing.name());
    }

    public int getFlowerAmount() {
        return (int) data.get(StateValue.FLOWER_AMOUNT);
    }

    public Half getHalf() {
        return (Half) data.get(StateValue.HALF);
    }

    public void setHalf(Half half) {

        data.put(StateValue.HALF, half);

    }

    public boolean isHanging() {
        return (boolean) data.get(StateValue.HANGING);
    }

    public void setHanging(boolean hanging) {
        data.put(StateValue.HANGING, hanging);
        block.properties().put(StateValue.HANGING.getName(), String.valueOf(hanging));
    }

    public boolean isHasBook() {
        return (boolean) data.get(StateValue.HAS_BOOK);
    }

    public void setHasBook(boolean hasBook) {
        data.put(StateValue.HAS_BOOK, hasBook);
        block.properties().put(StateValue.HAS_BOOK.getName(), String.valueOf(hasBook));
    }

    public boolean isHasBottle0() {
        return (boolean) data.get(StateValue.HAS_BOTTLE_0);
    }

    public boolean isHasBottle1() {
        return (boolean) data.get(StateValue.HAS_BOTTLE_1);
    }

    public boolean isHasBottle2() {
        return (boolean) data.get(StateValue.HAS_BOTTLE_2);
    }

    public boolean isHasRecord() {
        return (boolean) data.get(StateValue.HAS_RECORD);
    }

    public int getHatch() {
        return (int) data.get(StateValue.HATCH);
    }

    public Hinge getHinge() {
        return (Hinge) data.get(StateValue.HINGE);
    }

    public void setHinge(Hinge hinge) {
        data.put(StateValue.HINGE, hinge);
        block.properties().put(StateValue.HINGE.getName(), hinge.name());
    }

    public int getHoneyLevel() {
        return (int) data.get(StateValue.HONEY_LEVEL);
    }

    public boolean isInWall() {
        return (boolean) data.get(StateValue.IN_WALL);
    }

    public Instrument getInstrument() {
        return (Instrument) data.get(StateValue.INSTRUMENT);
    }

    public boolean isInverted() {
        return (boolean) data.get(StateValue.INVERTED);
    }

    public int getLayers() {
        return (int) data.get(StateValue.LAYERS);
    }

    public void setLayers(int layers) {
        data.put(StateValue.LAYERS, layers);
        block.properties().put(StateValue.LAYERS.getName(), String.valueOf(layers));
    }

    public Leaves getLeaves() {
        return (Leaves) data.get(StateValue.LEAVES);
    }

    public void setLeaves(Leaves leaves) {
        data.put(StateValue.LEAVES, leaves);
        block.properties().put(StateValue.LEAVES.getName(), leaves.name());
    }

    public int getLevel() {
        return (int) data.get(StateValue.LEVEL);
    }

    public void setLevel(int level) {
        data.put(StateValue.LEVEL, level);
        block.properties().put(StateValue.LEVEL.getName(), String.valueOf(level));
    }

    public boolean isLit() {
        return (boolean) data.get(StateValue.LIT);
    }

    public void setLit(boolean lit) {
        data.put(StateValue.LIT, lit);
        block.properties().put(StateValue.LIT.getName(), String.valueOf(lit));
    }

    public boolean isLocked() {
        return (boolean) data.get(StateValue.LOCKED);
    }

    public Mode getMode() {
        return (Mode) data.get(StateValue.MODE);
    }

    public int getMoisture() {
        return (int) data.get(StateValue.MOISTURE);
    }

    public North getNorth() {
        return (North) data.get(StateValue.NORTH);
    }

    public void setNorth(North north) {
        data.put(StateValue.NORTH, north);
        block.properties().put(StateValue.NORTH.getName(), north.name());
    }

    public int getNote() {
        return (int) data.get(StateValue.NOTE);
    }

    public boolean isOccupied() {
        return (boolean) data.get(StateValue.OCCUPIED);
    }

    public boolean isShrieking() {
        return (boolean) data.get(StateValue.SHRIEKING);
    }

    public boolean isCanSummon() {
        return (boolean) data.get(StateValue.CAN_SUMMON);
    }

    public boolean isOpen() {
        return (boolean) data.get(StateValue.OPEN);
    }

    public void setOpen(boolean open) {
        data.put(StateValue.OPEN, open);
        block.properties().put(StateValue.OPEN.getName(), String.valueOf(open));
    }

    public Orientation getOrientation() {
        return (Orientation) data.get(StateValue.ORIENTATION);
    }

    public Part getPart() {
        return (Part) data.get(StateValue.PART);
    }

    public boolean isPersistent() {
        return (boolean) data.get(StateValue.PERSISTENT);
    }

    public int getPickles() {
        return (int) data.get(StateValue.PICKLES);
    }

    public void setPickles(int pickles) {
        data.put(StateValue.PICKLES, pickles);
        block.properties().put(StateValue.PICKLES.getName(), String.valueOf(pickles));
    }

    public int getPower() {
        return (int) data.get(StateValue.POWER);
    }

    public boolean isPowered() {
        return (boolean) data.get(StateValue.POWERED);
    }

    public void setPowered(boolean powered) {
        data.put(StateValue.POWERED, powered);
        block.properties().put(StateValue.POWERED.getName(), String.valueOf(powered));
    }

    public int getRotation() {
        return (int) data.get(StateValue.ROTATION);
    }

    public SculkSensorPhase getSculkSensorPhase() {
        return (SculkSensorPhase) data.get(StateValue.SCULK_SENSOR_PHASE);
    }

    public Shape getShape() {
        return (Shape) data.get(StateValue.SHAPE);
    }

    public boolean isShort() {
        return (boolean) data.get(StateValue.SHORT);
    }

    public boolean isSignalFire() {
        return (boolean) data.get(StateValue.SIGNAL_FIRE);
    }

    public boolean isSlotZeroOccupied() {
        return (boolean) data.get(StateValue.SLOT_0_OCCUPIED);
    }

    public boolean isSlotOneOccupied() {
        return (boolean) data.get(StateValue.SLOT_1_OCCUPIED);
    }

    public boolean isSlotTwoOccupied() {
        return (boolean) data.get(StateValue.SLOT_2_OCCUPIED);
    }

    public boolean isSlotThreeOccupied() {
        return (boolean) data.get(StateValue.SLOT_3_OCCUPIED);
    }

    public boolean isSlotFourOccupied() {
        return (boolean) data.get(StateValue.SLOT_4_OCCUPIED);
    }

    public boolean isSlotFiveOccupied() {
        return (boolean) data.get(StateValue.SLOT_5_OCCUPIED);
    }

    public boolean isSnowy() {
        return (boolean) data.get(StateValue.SNOWY);
    }

    public int getStage() {
        return (int) data.get(StateValue.STAGE);
    }

    public South getSouth() {
        return (South) data.get(StateValue.SOUTH);
    }

    public void setSouth(South south) {
        data.put(StateValue.SOUTH, south);
        block.properties().put(StateValue.SOUTH.getName(), south.name());
    }

    public Thickness getThickness() {
        return (Thickness) data.get(StateValue.THICKNESS);
    }

    public void setThickness(Thickness thickness) {
        data.put(StateValue.THICKNESS, thickness);
        block.properties().put(StateValue.THICKNESS.getName(), thickness.name());
    }

    public Tilt getTilt() {
        return (Tilt) data.get(StateValue.TILT);
    }

    public boolean isTriggered() {
        return (boolean) data.get(StateValue.TRIGGERED);
    }

    public Type getTypeData() {
        return (Type) data.get(StateValue.TYPE);
    }

    public void setTypeData(Type type) {
        data.put(StateValue.TYPE, type);
        block.properties().put(StateValue.TYPE.getName(), type.name());
    }

    public boolean isUnstable() {
        return (boolean) data.get(StateValue.UNSTABLE);
    }

    public boolean isUp() {
        return (boolean) data.get(StateValue.UP);
    }

    public void setUp(boolean up) {
        data.put(StateValue.UP, up);
        block.properties().put(StateValue.UP.getName(), String.valueOf(up));
    }

    public VerticalDirection getVerticalDirection() {
        return (VerticalDirection) data.get(StateValue.VERTICAL_DIRECTION);
    }

    public void setVerticalDirection(VerticalDirection verticalDirection) {
        data.put(StateValue.VERTICAL_DIRECTION, verticalDirection);
        block.properties().put(StateValue.VERTICAL_DIRECTION.getName(), verticalDirection.name());
    }

    public boolean isWaterlogged() {
        return (boolean) data.get(StateValue.WATERLOGGED);
    }

    public void setWaterlogged(boolean waterlogged) {
        data.put(StateValue.WATERLOGGED, waterlogged);
        block.properties().put(StateValue.WATERLOGGED.getName(), String.valueOf(waterlogged));
    }

    public East getEast() {
        return (East) data.get(StateValue.EAST);
    }

    public void setEast(East east) {
        data.put(StateValue.EAST, east);
        block.properties().put(StateValue.EAST.getName(), east.name());
    }

    public West getWest() {
        return (West) data.get(StateValue.WEST);
    }

    public void setWest(West west) {
        data.put(StateValue.WEST, west);
        block.properties().put(StateValue.WEST.getName(), west.name());
    }

    public Bloom getBloom() {
        return (Bloom) data.get(StateValue.BLOOM);
    }

    public boolean isCracked() {
        return (boolean) data.get(StateValue.CRACKED);
    }

    public boolean isCrafting() {
        return (boolean) data.get(StateValue.CRAFTING);
    }

    public TrialSpawnerState getTrialSpawnerState() {
        return (TrialSpawnerState) data.get(StateValue.TRIAL_SPAWNER_STATE);
    }

}
