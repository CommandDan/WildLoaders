package com.bgsoftware.wildloaders.handlers;

import com.bgsoftware.wildloaders.WildLoadersPlugin;
import com.bgsoftware.wildloaders.api.managers.LoadersManager;
import com.bgsoftware.wildloaders.api.loaders.ChunkLoader;
import com.bgsoftware.wildloaders.api.loaders.LoaderData;
import com.bgsoftware.wildloaders.loaders.WChunkLoader;
import com.bgsoftware.wildloaders.loaders.WLoaderData;
import com.bgsoftware.wildloaders.utils.chunks.ChunkPosition;
import com.bgsoftware.wildloaders.utils.database.Query;
import com.google.common.collect.Maps;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class LoadersHandler implements LoadersManager {

    private final Map<Location, ChunkLoader> chunkLoaders = Maps.newConcurrentMap();
    private final Map<ChunkPosition, ChunkLoader> chunkLoadersByChunks = Maps.newConcurrentMap();
    private final Map<UUID, List<ChunkLoader>> chunkLoadersByPlacerUUID = Maps.newConcurrentMap();
    private final Map<String, LoaderData> loadersData = Maps.newConcurrentMap();
    private final WildLoadersPlugin plugin;

    public LoadersHandler(WildLoadersPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public Optional<ChunkLoader> getChunkLoader(Chunk chunk) {
        return Optional.ofNullable(chunkLoadersByChunks.get(ChunkPosition.of(chunk)));
    }

    @Override
    public Optional<ChunkLoader> getChunkLoader(Location location) {
        return Optional.ofNullable(chunkLoaders.get(location));
    }

    @Override
    public List<ChunkLoader> getChunkLoaders() {
        return Collections.unmodifiableList(new ArrayList<>(chunkLoaders.values()));
    }

    @Override
    public List<ChunkLoader> getChunkLoaders(UUID placer) {
        return Collections.unmodifiableList(new ArrayList<>(chunkLoadersByPlacerUUID.get(placer)));
    }

    @Override
    public Optional<LoaderData> getLoaderData(String name) {
        return Optional.ofNullable(loadersData.get(name));
    }

    @Override
    public List<LoaderData> getLoaderDatas() {
        return new ArrayList<>(loadersData.values());
    }

    @Override
    public ChunkLoader addChunkLoader(LoaderData loaderData, Player whoPlaced, Location location, long timeLeft) {
        return addChunkLoader(loaderData, whoPlaced, location, timeLeft, false);
    }

    @Override
    public ChunkLoader addChunkLoader(LoaderData loaderData, Player whoPlaced, Location location, long timeLeft, boolean startPaused) {
        WChunkLoader chunkLoader = addChunkLoader(loaderData, whoPlaced.getUniqueId(), location, timeLeft, startPaused);

        Query.INSERT_CHUNK_LOADER.insertParameters()
                .setLocation(location)
                .setObject(whoPlaced.getUniqueId().toString())
                .setObject(loaderData.getName())
                .setObject(timeLeft)
                .queue(location);

        return chunkLoader;
    }

    public WChunkLoader addChunkLoader(LoaderData loaderData, UUID placer, Location location, long timeLeft, boolean startPaused){
        WChunkLoader chunkLoader = new WChunkLoader(loaderData, placer, location, timeLeft, startPaused);
        chunkLoaders.put(location, chunkLoader);
        for (Chunk loadedChunk : chunkLoader.getLoadedChunks()) {
            chunkLoadersByChunks.put(ChunkPosition.of(loadedChunk), chunkLoader);
        }
        List<ChunkLoader> placerChunkLoaders = chunkLoadersByPlacerUUID.getOrDefault(placer, new ArrayList<>());
        placerChunkLoaders.add(chunkLoader);
        chunkLoadersByPlacerUUID.put(placer, placerChunkLoaders);
        plugin.getNPCs().createNPC(location);
        return chunkLoader;
    }

    @Override
    public void removeChunkLoader(ChunkLoader chunkLoader) {
        Location location = chunkLoader.getLocation();
        chunkLoaders.remove(location);
        for (Chunk loadedChunk : chunkLoader.getLoadedChunks()) {
            chunkLoadersByChunks.remove(ChunkPosition.of(loadedChunk));
        }
        UUID placer = chunkLoader.getWhoPlaced().getUniqueId();
        List<ChunkLoader> placerChunkLoaders = chunkLoadersByPlacerUUID.getOrDefault(placer, new ArrayList<>());
        placerChunkLoaders.remove(chunkLoader);
        chunkLoadersByPlacerUUID.put(placer, placerChunkLoaders);
        chunkLoader.getNPC().ifPresent(npc -> plugin.getNPCs().killNPC(npc));

        Query.DELETE_CHUNK_LOADER.insertParameters()
                .setLocation(location)
                .queue(location);
    }

    @Override
    public LoaderData createLoaderData(String name, long timeLeft, ItemStack itemStack) {
        LoaderData loaderData = new WLoaderData(name, timeLeft, itemStack);
        loadersData.put(name, loaderData);
        return loaderData;
    }

    @Override
    public void removeLoadersData() {
        loadersData.clear();
    }

    @Override
    public void removeChunkLoaders() {
        chunkLoaders.values().forEach(chunkLoader -> plugin.getNMSAdapter().removeLoader(chunkLoader, false));
        chunkLoaders.clear();
        chunkLoadersByChunks.clear();
        chunkLoadersByPlacerUUID.clear();
    }

    @Override
    public void pauseChunkLoader(ChunkLoader chunkLoader) {
        plugin.getNMSAdapter().pauseLoader(chunkLoader);
    }

    @Override
    public void unpauseChunkLoader(ChunkLoader chunkLoader) {
        plugin.getNMSAdapter().unpauseLoader(chunkLoader);
    }

    @Override
    public void pauseAllChunkLoaders() {
        chunkLoaders.values().forEach(chunkLoader -> plugin.getNMSAdapter().pauseLoader(chunkLoader));
    }

    @Override
    public void unpauseAllChunkLoaders() {
        chunkLoaders.values().forEach(chunkLoader -> plugin.getNMSAdapter().unpauseLoader(chunkLoader));
    }

    @Override
    public void pauseChunkLoaders(UUID placer) {
        if (!chunkLoadersByPlacerUUID.containsKey(placer)) return;
        List<ChunkLoader> placersChunkLoaders = chunkLoadersByPlacerUUID.get(placer);
        for (ChunkLoader chunkLoader : placersChunkLoaders) {
            plugin.getNMSAdapter().pauseLoader(chunkLoader);
        }
    }

    @Override
    public void unpauseChunkLoaders(UUID placer) {
        if (!chunkLoadersByPlacerUUID.containsKey(placer)) return;
        List<ChunkLoader> placersChunkLoaders = chunkLoadersByPlacerUUID.get(placer);
        for (ChunkLoader chunkLoader : placersChunkLoaders) {
            plugin.getNMSAdapter().unpauseLoader(chunkLoader);
        }
    }


}
