package me.domcie.skyplots.utils;

import me.domcie.skyplots.SkyPlots;
import me.domcie.skyplots.data.IslandData;
import me.domcie.skyplots.data.config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class InvitationManager {

    public static HashMap<UUID, BukkitTask> invitations = new HashMap<>();
    public static HashMap<UUID, UUID> invitation = new HashMap<>();

    static config cfg = config.getInst();
    public static void invite(Player player1, Player player2) {
        UUID player2Id = player2.getUniqueId();

        // Cancel any previous invitation tasks
        if (invitations.containsKey(player2Id)) {
            invitations.get(player2Id).cancel();
        }

        // Send confirmation to player 1
        player1.sendMessage(String.format(cfg.msg_invitation_send.replace("<player>", player2.getName())));
        // Send invitation message to player 2
        player2.sendMessage(String.format(cfg.msg_invitation_receive.replace("<player>", player1.getName())));

        // Schedule a task to automatically decline the invitation after 20 seconds
        BukkitTask task = Bukkit.getScheduler().runTaskLater(SkyPlots.getInst(), () -> {
            Player p = Bukkit.getPlayer(player2Id);
            if (p != null) {
                p.sendMessage(cfg.msg_invitation_expired.replace("<player>", player1.getName()));
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

            if (invitation.containsKey(playerId)) {
                Player owner = Bukkit.getPlayer(invitation.get(playerId).toString());
                IslandData island = IslandData.getIslandByOwnerId(invitation.get(playerId).toString());
                island.addMember(player.getUniqueId().toString());

                if (owner.isOnline()) {
                    owner.sendMessage(cfg.msg_invitation_accepted.replace("<player>", player.getName()));
                }
                invitation.remove(playerId);
            }
            player.sendMessage(cfg.msg_invitation_accept);
        } else {
            player.sendMessage(cfg.msg_no_invitation);
        }
    }

    public static void decline(Player player) {
        UUID playerId = player.getUniqueId();

        // Cancel the invitation task
        if (invitations.containsKey(playerId)) {
            invitations.get(playerId).cancel();
            invitations.remove(playerId);


            if (invitation.containsKey(playerId)) {
                Player owner = Bukkit.getPlayer(invitation.get(playerId).toString());
                if (owner.isOnline()) {
                    owner.sendMessage(cfg.msg_invitation_declined.replace("<player>", player.getName()));
                }
                invitation.remove(playerId);
            }

            player.sendMessage(cfg.msg_invitation_decline);
        } else {
            player.sendMessage(cfg.msg_no_invitation);
        }
    }

    public static boolean hasPendingInvitation(Player p){
        if (invitations.containsKey(p.getUniqueId())) {
            return true;
        }
        return false;
    }
}