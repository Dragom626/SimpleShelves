package net.pinaz993.simpleshelves;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.pinaz993.simpleshelves.SimpleShelves.SHELF_BLOCK_ENTITY;

/**
 * A block entity for shelves. Only contains methods that could not be implemented in ShelfInventory. Pretty much all
 * inventory stuff lives over there.
 */

public class ShelfEntity extends BlockEntity implements ShelfInventory {

    DefaultedList<ItemStack> items;    // The items that are in the inventory.
    private boolean hasGenericItems;   // Referring to this should be faster than querying the inventory every frame.
    public boolean hasGenericItems() { return hasGenericItems;} // Private with getter, because nothing should be
                                                                // setting it except for markDirtyInWorld().
    private int redstoneValue; // Cached value so we don't have to query the inventory for every redstone update.
    public int getRedstoneValue() {return this.redstoneValue;} // Private with getter for same reason as above.


    public ShelfEntity(BlockPos pos, BlockState state) {
        super(SHELF_BLOCK_ENTITY, pos, state);
        // Initialize the list of items that are stored in this inventory.
        this.items = DefaultedList.ofSize(16, ItemStack.EMPTY);
        this.hasGenericItems = false;
        this.redstoneValue = 0;
    }

    // Item getter provided because I couldn't figure out a way to implement the item field in ShelfInventory, but that
    // class still needs to refer to the list. This is one instance where Java causes a little bit of bloat. If I could
    // extend multiple classes, I wouldn't need a separate block entity class, and thus I'd be able to just reference
    // the field.
    @Override
    public DefaultedList<ItemStack> getItems() {return items;}

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, items);
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
    }

    /**
     * Serializes the state of this block entity. I don't know if I need the id, x, y, and z, but ultimately, they're
     * not all that much to put in the NBT, and better safe than sorry. Can be used for saving and syncing.
     * @return The state of this block entity in NBT form.
     */
    public NbtCompound toNbt(){
        NbtCompound rtn = new NbtCompound();
        Inventories.writeNbt(rtn, items);
        // Sometimes I hate @Nullable.
        rtn.putString("id", Objects.requireNonNull(BlockEntityType.getId(SHELF_BLOCK_ENTITY)).toString());
        rtn.putInt("x", this.pos.getX());
        rtn.putInt("y", this.pos.getY());
        rtn.putInt("z", this.pos.getZ());
        return rtn;
    }

    // Lifted almost directly from BlockEntity. We can't get a World object from in ShelfInventory, so we have to
    // implement marking dirty here.
    @Override
    public void markDirty() {if(this.world != null) markDirtyInWorld(this.world, this.pos, this.getCachedState());}

    // In BlockEntity, this method has the same name as the one above. Java doesn't want me to override that, as it's
    // 'pRoTeCtEd'. Bah!
    /**
     * Tell the world that the inventory changed, so that inventory monitoring blocks/entities can be notified.
     */
    protected void markDirtyInWorld(World world, BlockPos pos, BlockState state){
        // Verify that no quadrant has both generic items and books.
        for(ShelfQuadrant quad: ShelfQuadrant.class.getEnumConstants())
            if(quadrantHasGenericItem(quad) && quadrantHasBook(quad)) { // If one does...
                world.spawnEntity(new ItemEntity(world,
                    pos.getX() +.5, pos.getY()+1.5, pos.getZ() +.5,
                    removeStack(quad.GENERIC_ITEM_SLOT))); // Spit the generic item out the top of the shelf.
                LogManager.getLogger().warn("Shelf quadrant " + quad + " at " + pos
                    + " contains both book-like items and generic items. "
                    + "Ejecting Generic item to block space above."); // Log the anomaly.
            }
        this.hasGenericItems = this.shelfHasGenericItem(); // Are there any generic items to render?
        this.redstoneValue = 0; // Reset the redstone value.
        // Iterate through all block positions, updating state.
        BlockState newState = state; // New block state to be implemented in world.
        // Iterate over all positions and record the new state values, updating redstone value if needed.
        for(BookPosition bp: BookPosition.class.getEnumConstants()){
            ItemStack stack = getStack(bp.SLOT); // Get the stack in the slot.
            newState = newState.with(bp.BLOCK_STATE_PROPERTY, !stack.isEmpty());
            // If the stack is of redstone books, update redstone value if this is higher than what we've seen thus far.
            if(stack.isOf(SimpleShelves.REDSTONE_BOOK))
                this.redstoneValue = Math.max(this.redstoneValue, stack.getCount());
        }
        // Set the new state, notify the block's neighbors (if on server), but don't recalculate lighting updates.
        // Don't update pathfinding entities. Don't pass GO. Don't collect $200.
        world.setBlockState(pos, newState, Block.NOTIFY_NEIGHBORS | Block.SKIP_LIGHTING_UPDATES);
        // Super calls World.markDirty() and possibly World.updateComparators(). We're already updating all neighbors,
        world.markDirty(pos); // so we'll just call world.markDirty().
        if(!world.isClient()) // If this is running on the server...
            ((ServerWorld)world).getChunkManager().markForUpdate(pos); // Mark changes to be synced to the client.
    }


    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.toNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}

