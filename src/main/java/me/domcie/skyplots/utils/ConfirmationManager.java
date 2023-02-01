package me.domcie.skyplots.utils;

import me.domcie.skyplots.SkyPlots;
import me.domcie.skyplots.data.config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ConfirmationManager {

    public static HashMap<UUID, String> confirmations = new HashMap<>();
    static config cfg = config.getInst();
    public static void SendConfirmation(Player p, String type){
        UUID uuid = p.getUniqueId();
        confirmations.remove(uuid);

        p.sendMessage(cfg.msg_confirm_send);

        Bukkit.getScheduler().runTaskLater(SkyPlots.getInst(), () -> {
            if (confirmations.containsKey(uuid)) {
                p.sendMessage(cfg.msg_confirm_expired);
                confirmations.remove(uuid);
            }
        }, 20 * 20);

        confirmations.put(p.getUniqueId(), type);
    }

    public static String hasPendingConfirmation(Player p){
        UUID uuid = p.getUniqueId();
        if (confirmations.containsKey(uuid)) {
            return confirmations.get(uuid);
        }
        return null;
    }

    public static boolean confirmationCommand(Player p, String s){
        UUID uuid = p.getUniqueId();
        if (confirmations.containsKey(uuid)) {
            if(confirmations.get(uuid) == s){
                return true;
            }
        }
        return false;
    }
}
