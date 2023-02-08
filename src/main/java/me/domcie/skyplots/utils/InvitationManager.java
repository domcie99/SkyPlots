package me.domcie.skyplots.utils;

import me.domcie.skyplots.SkyPlots;
import me.domcie.skyplots.data.IslandData;
import me.domcie.skyplots.data.config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class InvitationManager {
    public static HashMap<UUID, UUID> invitations = new HashMap<>();
    static config cfg = config.getInst();

    public static void sendInvitation(Player owner, Player invited){
        UUID oid = owner.getUniqueId();
        UUID iid = invited.getUniqueId();
        invitations.remove(iid);

        owner.sendMessage(String.format(cfg.msg_invitation_send.replace("<player>", invited.getName())));
        invited.sendMessage(String.format(cfg.msg_invitation_receive.replace("<player>", owner.getName())));


        Bukkit.getScheduler().runTaskLater(SkyPlots.getInst(), () -> {
            if (invitations.containsKey(iid)) {
                invited.sendMessage(cfg.msg_invitation_expired.replace("<player>", owner.getName()));
                invitations.remove(iid);
            }
        }, 20 * 20);

        invitations.put(iid, oid);
    }

    public static boolean hasPendingInvitation(Player p){
        UUID uuid = p.getUniqueId();
        if (invitations.containsKey(uuid)) {
            return true;
        }
        return false;
    }

    public static void accept(Player player) {
        UUID playerId = player.getUniqueId();

        if (invitations.containsKey(playerId)) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(invitations.get(playerId).toString());
            IslandData island = IslandData.getIslandByOwnerId(owner.getName());
            if(island != null) {
                island.addMember(player.getUniqueId().toString());
                if (owner.isOnline()) {
                    owner.getPlayer().sendMessage(cfg.msg_invitation_accepted.replace("<player>", player.getName()));
                }
                invitations.remove(playerId);
                player.sendMessage(cfg.msg_invitation_accept);
            } else {
                player.sendMessage(cfg.msg_player_not_owner.replace("<player>", owner.getName()));
            }
        } else {
            player.sendMessage(cfg.msg_no_invitation);
        }
    }

    public static void decline(Player player) {
        UUID playerId = player.getUniqueId();

        if (invitations.containsKey(playerId)) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(invitations.get(playerId).toString());
            if (owner.isOnline()) {
                owner.getPlayer().sendMessage(cfg.msg_invitation_declined.replace("<player>", player.getName()));
            }
            invitations.remove(playerId);
            player.sendMessage(cfg.msg_invitation_decline);
        } else {
            player.sendMessage(cfg.msg_no_invitation);
        }
    }
}