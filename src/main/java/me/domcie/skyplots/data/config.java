package me.domcie.skyplots.data;

import me.domcie.skyplots.SkyPlots;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class config {
    private static config inst;
    public FileConfiguration cfg = SkyPlots.getInst().getConfig();
    public String DataType;
    public String host;
    public int port;
    public String dbname;
    public String user;
    public String password;

    public String island_world;
    public int island_size;
    public int island_gap;

    public List<String> playerGui;
    public List<String> adminGui;

    public String msg_player_only;
    public String msg_player_offline;
    public String msg_player_null;
    public String msg_no_permission;

    public String msg_has_pending_invitation;

    public String msg_create;
    public String msg_created;
    public String msg_teleport;
    public String msg_usage;

    public String msg_not_owner;
    public String msg_player_not_owner;

    public String msg_not_member;
    public String msg_player_not_member;

    public String msg_no_island;
    public String msg_has_island;

    public String msg_no_location;

    public String msg_no_schematic;
    public String msg_empty_clipboard;
    public String msg_error_saving;
    public String msg_error_pasting;
    public String msg_success;

    public String msg_delete;
    public String msg_delete_sender;
    public String msg_delete_receiver;

    public String msg_remove;
    public String msg_remove_sender;
    public String msg_remove_receiver;

    public String msg_confirm;
    public String msg_confirm_send;
    public String msg_confirm_expired;

    public String msg_invitation_send;
    public String msg_invitation_receive;
    public String msg_invitation_expired;
    public String msg_no_invitation;

    public String msg_invitation_accept;
    public String msg_invitation_accepted;

    public String msg_invitation_decline;
    public String msg_invitation_declined;

    public void reload(){
        //plugin.reloadConfig();
        load();
    }
    public void load(){
        this.DataType = cfg.getString("database.type");

        this.host = cfg.getString("database.host");
        this.port = cfg.getInt("database.port");
        this.dbname = cfg.getString("database.dbname");
        this.user = cfg.getString("database.user");
        this.password = cfg.getString("database.password");

        this.island_world = cfg.getString("island.world-name");
        this.island_size = cfg.getInt("island.size");
        this.island_gap = cfg.getInt("island.gap");

        this.playerGui = cfg.getStringList("messages.playerGui");
        this.adminGui = cfg.getStringList("messages.adminGui");
        playerGui.replaceAll(s -> s.replaceAll("&", "§"));
        adminGui.replaceAll(s -> s.replaceAll("&", "§"));

        this.msg_player_only = cfg.getString("messages.msg_player_only").replace("&", "§");
        this.msg_player_offline = cfg.getString("messages.msg_player_offline").replace("&", "§");
        this.msg_player_null = cfg.getString("messages.msg_player_null").replace("&", "§");
        this.msg_no_permission = cfg.getString("messages.msg_no_permission").replace("&", "§");

        this.msg_has_pending_invitation = cfg.getString("messages.msg_has_pending_invitation").replace("&", "§");

        this.msg_create = cfg.getString("messages.msg_create").replace("&", "§");
        this.msg_created = cfg.getString("messages.msg_created").replace("&", "§");
        this.msg_teleport = cfg.getString("messages.msg_teleport").replace("&", "§");
        this.msg_usage = cfg.getString("messages.msg_usage").replace("&", "§");

        this.msg_not_owner = cfg.getString("messages.msg_not_owner").replace("&", "§");
        this.msg_player_not_owner = cfg.getString("messages.msg_player_not_owner").replace("&", "§");

        this.msg_not_member = cfg.getString("messages.msg_not_member").replace("&", "§");
        this.msg_player_not_member = cfg.getString("messages.msg_player_not_member").replace("&", "§");

        this.msg_no_island = cfg.getString("messages.msg_no_island").replace("&", "§");
        this.msg_has_island = cfg.getString("messages.msg_has_island").replace("&", "§");

        this.msg_no_location = cfg.getString("messages.msg_no_location").replace("&", "§");

        this.msg_no_schematic = cfg.getString("messages.msg_no_schematic").replace("&", "§");
        this.msg_empty_clipboard = cfg.getString("messages.msg_empty_clipboard").replace("&", "§");
        this.msg_error_saving = cfg.getString("messages.msg_error_saving").replace("&", "§");
        this.msg_error_pasting = cfg.getString("messages.msg_error_pasting").replace("&", "§");
        this.msg_success = cfg.getString("messages.msg_success").replace("&", "§");

        this.msg_delete = cfg.getString("messages.msg_delete").replace("&", "§");
        this.msg_delete_sender = cfg.getString("messages.msg_delete_sender").replace("&", "§");
        this.msg_delete_receiver = cfg.getString("messages.msg_delete_receiver").replace("&", "§");

        this.msg_remove = cfg.getString("messages.msg_remove").replace("&", "§");
        this.msg_remove_sender = cfg.getString("messages.msg_remove_sender").replace("&", "§");
        this.msg_remove_receiver = cfg.getString("messages.msg_remove_receiver").replace("&", "§");

        this.msg_confirm = cfg.getString("messages.msg_confirm").replace("&", "§");
        this.msg_confirm_send = cfg.getString("messages.msg_confirm_send").replace("&", "§");
        this.msg_confirm_expired = cfg.getString("messages.msg_confirm_expired").replace("&", "§");

        this.msg_invitation_send = cfg.getString("messages.msg_invitation_send").replace("&", "§");
        this.msg_invitation_receive = cfg.getString("messages.msg_invitation_receive").replace("&", "§");
        this.msg_invitation_expired = cfg.getString("messages.msg_invitation_expired").replace("&", "§");
        this.msg_no_invitation = cfg.getString("messages.msg_no_invitation").replace("&", "§");

        this.msg_invitation_accept = cfg.getString("messages.msg_invitation_accept").replace("&", "§");
        this.msg_invitation_accepted = cfg.getString("messages.msg_invitation_accepted").replace("&", "§");

        this.msg_invitation_decline = cfg.getString("messages.msg_invitation_decline").replace("&", "§");
        this.msg_invitation_declined = cfg.getString("messages.msg_invitation_declined").replace("&", "§");
    }

    public static config getInst(){
        if(inst == null) return new config();
        return inst;
    }
    public config(){
        inst = this;
    }
}
