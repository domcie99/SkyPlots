package me.domcie.skyplots.utils;

import me.domcie.skyplots.data.IslandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class Invitation {

    public static HashMap<UUID, BukkitTask> invitations = new HashMap<>();
    public static HashMap<UUID, UUID> invitation = new HashMap<>();

    public static void invite(Player player1, Player player2) {
        UUID player2Id = player2.getUniqueId();

        // Cancel any previous invitation tasks
        if (invitations.containsKey(player2Id)) {
            invitations.get(player2Id).cancel();
        }

        // Send confirmation to player 1
        player1.sendMessage(String.format("§6You invited %s to your island.", player2.getName()));
        // Send invitation message to player 2
        player2.sendMessage(String.format("§6Player %s has invited you to join their island. Type /is accept or /is decline to respond.", player1.getName()));

        // Schedule a task to automatically decline the invitation after 20 seconds
        BukkitTask task = Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("MyPlugin"), () -> {
            Player p = Bukkit.getPlayer(player2Id);
            if (p != null) {
                p.sendMessage("§4Invitation from " + player1.getName() + " has expired.");
            }
            invitations.remove(player2Id);
            invitation.remove(player1.getUniqueId());
        }, 20 * 20);

        invitation.put(player1.getUniqueId(), player2.getUniqueId());
        invitations.put(player2Id, task);
    }

    public static void accept(Player player) {
        UUID playerId = player.getUniqueId();

        // Cancel the invitation task
        if (invitations.containsKey(playerId)) {
            invitations.get(playerId).cancel();
            invitations.remove(playerId);
        }
        if (invitation.containsKey(playerId)) {
            Player owner = Bukkit.getPlayer(invitation.get(playerId).toString());
            IslandData island = IslandData.getIslandByOwnerId(invitation.get(playerId).toString());
            island.addMember(player.getUniqueId().toString());

            if(owner.isOnline()){
                owner.sendMessage("§6Player " + player.getName() + " accepted your invitation!");
            }
            invitation.remove(playerId);
        }
        player.sendMessage("§6You have accepted the invitation and joined the island as a member.");
    }

    public static void decline(Player player) {
        UUID playerId = player.getUniqueId();

        // Cancel the invitation task
        if (invitations.containsKey(playerId)) {
            invitations.get(playerId).cancel();
            invitations.remove(playerId);
        }

        if (invitation.containsKey(playerId)) {
            Player owner = Bukkit.getPlayer(invitation.get(playerId).toString());
            if(owner.isOnline()){
                owner.sendMessage("§4Player " + player.getName() + " declined your invitation!");
            }
            invitation.remove(playerId);
        }

        player.sendMessage("§4You have declined the invitation.");
    }

    public static boolean hasPendingInvitation(Player p){
        if (invitations.containsKey(p.getUniqueId())) {
            return true;
        }
        return false;
    }
}