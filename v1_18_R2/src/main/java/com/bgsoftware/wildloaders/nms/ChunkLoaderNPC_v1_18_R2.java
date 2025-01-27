package com.bgsoftware.wildloaders.nms;

import com.bgsoftware.wildloaders.api.npc.ChunkLoaderNPC;
import com.bgsoftware.wildloaders.handlers.NPCHandler;
import com.bgsoftware.wildloaders.npc.DummyChannel;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInBlockPlace;
import net.minecraft.network.protocol.game.PacketPlayInChat;
import net.minecraft.network.protocol.game.PacketPlayInFlying;
import net.minecraft.network.protocol.game.PacketPlayInHeldItemSlot;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayInWindowClick;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.phys.AxisAlignedBB;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

import java.util.UUID;

import static com.bgsoftware.wildloaders.nms.NMSMappings_v1_18_R2.*;

public final class ChunkLoaderNPC_v1_18_R2 extends EntityPlayer implements ChunkLoaderNPC {

    private static final AxisAlignedBB EMPTY_BOUND = new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D);

    private boolean dieCall = false;

    public ChunkLoaderNPC_v1_18_R2(MinecraftServer minecraftServer, Location location, UUID uuid){
        super(minecraftServer, ((CraftWorld) location.getWorld()).getHandle(),
                new GameProfile(uuid, NPCHandler.getName(location.getWorld().getName())));

        this.b = new DummyPlayerConnection(minecraftServer, this);

        NMSMappings_v1_18_R2.setGameModeForPlayer(this.d, EnumGamemode.b);
        clientViewDistance = 1;

        fauxSleeping = true;

        spawnIn(getWorld(this));
        moveTo(this, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        addNewPlayer(getLevel(this), this);

        super.a(EMPTY_BOUND);
    }

    @Override
    public UUID getUniqueId() {
        return super.cm();
    }

    @Override
    public void die() {
        ah();
    }

    @Override
    public AxisAlignedBB cx() {
        return EMPTY_BOUND;
    }

    @Override
    public void a(RemovalReason removalReason) {
        if(!dieCall) {
            dieCall = true;
            removePlayer(getLevel(this), this);
            dieCall = false;
        }
        else {
            super.a(removalReason);
        }
    }

    @Override
    public Location getLocation() {
        return getBukkitEntity().getLocation();
    }

    private static void removePlayer(WorldServer worldServer, EntityPlayer entityPlayer){
        worldServer.a(entityPlayer, RemovalReason.d);
    }

    public static class DummyNetworkManager extends NetworkManager {

        DummyNetworkManager(){
            super(EnumProtocolDirection.a);
            this.m = new DummyChannel();
            this.n = null;
        }

    }

    public static class DummyPlayerConnection extends PlayerConnection {

        DummyPlayerConnection(MinecraftServer minecraftServer, EntityPlayer entityPlayer) {
            super(minecraftServer, new DummyNetworkManager(), entityPlayer);
        }

        public void a(PacketPlayInWindowClick packetPlayInWindowClick) {

        }

        public void a(PacketPlayInFlying packetPlayInFlying) {

        }

        public void a(PacketPlayInUpdateSign packetPlayInUpdateSign) {

        }

        public void a(PacketPlayInBlockDig packetPlayInBlockDig) {

        }

        public void a(PacketPlayInBlockPlace packetPlayInBlockPlace) {

        }

        public void disconnect(String s) {

        }

        public void a(PacketPlayInHeldItemSlot packetPlayInHeldItemSlot) {

        }

        public void a(PacketPlayInChat packetPlayInChat) {

        }

        public void a(Packet<?> packet) {

        }

    }

}
