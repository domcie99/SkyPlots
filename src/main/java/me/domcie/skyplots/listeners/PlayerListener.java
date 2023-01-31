package me.domcie.skyplots.listeners;

import me.domcie.skyplots.data.IslandData;
import me.domcie.skyplots.data.config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class PlayerListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getBlock().getWorld().equals(Bukkit.getWorld(config.getInst().island_world))) {
            Player player = event.getPlayer();
            Location blockLocation = event.getBlock().getLocation();
            IslandData island = IslandData.getIslandByLocation(blockLocation);
            if (island == null || (!island.isOwner(player) && !island.isMember(player))) {
                event.setCancelled(true);
                player.sendMessage("You are not allowed to break blocks there.");
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getBlock().getWorld().equals(Bukkit.getWorld(config.getInst().island_world))) {
            Player player = event.getPlayer();
            Location blockLocation = event.getBlock().getLocation();
            IslandData island = IslandData.getIslandByLocation(blockLocation);
            if (island == null || (!island.isOwner(player) && !island.isMember(player))) {
                event.setCancelled(true);
                player.sendMessage("You are not allowed to place blocks there.");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        World ew = Objects.requireNonNull(event.getClickedBlock()).getWorld();
        World iw = Bukkit.getWorld(config.getInst().island_world);
        if(ew.equals(iw)) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (block == null) {
                return;
            }
            Material blockType = block.getType();
            IslandData island = IslandData.getIslandByLocation(block.getLocation());
            if (blockType != Material.OAK_DOOR && blockType != Material.IRON_DOOR && blockType != Material.ACACIA_DOOR &&
                blockType != Material.BIRCH_DOOR && blockType != Material.DARK_OAK_DOOR && blockType != Material.JUNGLE_DOOR &&
                blockType != Material.SPRUCE_DOOR && blockType != Material.STONE_BUTTON) {
                if (island == null || !island.isMember(player) || !island.isOwner(player)) {
                    event.setCancelled(true);
                    player.sendMessage("You are not allowed to interact with anything besides doors and buttons there.");
                }
            }
        }
    }

}
