package com.bgsoftware.wildloaders.nms;

import com.bgsoftware.wildloaders.api.holograms.Hologram;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public final class EntityHolograms_v1_17_R1 extends EntityArmorStand implements Hologram {

    private static final AxisAlignedBB EMPTY_BOUND = new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D);

    private CraftEntity bukkitEntity;

    public EntityHolograms_v1_17_R1(World world, double x, double y, double z){
        super(world, x, y, z);
        setInvisible(true);
        setSmall(true);
        setArms(false);
        setNoGravity(true);
        setBasePlate(true);
        setMarker(true);
        super.collides = false;
        super.setCustomNameVisible(true);
        super.a(EMPTY_BOUND);
    }

    @Override
    public void setHologramName(String name) {
        super.setCustomName(CraftChatMessage.fromString(name)[0]);
    }

    @Override
    public void removeHologram() {
        super.a(Entity.RemovalReason.b);
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }

    @Override
    public void tick() {
        // Disable normal ticking for this entity.

        // Workaround to force EntityTrackerEntry to send a teleport packet immediately after spawning this entity.
        if (this.z) {
            this.z = false;
        }
    }

    @Override
    public void inactiveTick() {
        // Disable normal ticking for this entity.

        // Workaround to force EntityTrackerEntry to send a teleport packet immediately after spawning this entity.
        if (this.z) {
            this.z = false;
        }
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
        return false;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        // Do not load NBT.
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        // Do not load NBT.
    }

    @Override
    public boolean isInvulnerable(DamageSource source) {
        /*
         * The field Entity.invulnerable is private.
         * It's only used while saving NBTTags, but since the entity would be killed
         * on chunk unload, we prefer to override isInvulnerable().
         */
        return true;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        // Locks the custom name.
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        // Locks the custom name.
    }

    @Override
    public EnumInteractionResult a(EntityHuman human, Vec3D vec3d, EnumHand enumhand) {
        // Prevent stand being equipped
        return EnumInteractionResult.d;
    }

    @Override
    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        // Prevent stand being equipped
    }

    @Override
    public AxisAlignedBB cs() {
        return EMPTY_BOUND;
    }

    public void forceSetBoundingBox(AxisAlignedBB boundingBox) {
        super.a(boundingBox);
    }

    @Override
    public void playSound(SoundEffect soundeffect, float f, float f1) {
        // Remove sounds.
    }

    @Override
    public void a(Entity.RemovalReason entity_removalreason) {
        // Prevent being killed.
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = new CraftArmorStand((CraftServer) Bukkit.getServer(), this);
        }
        return bukkitEntity;
    }

}
