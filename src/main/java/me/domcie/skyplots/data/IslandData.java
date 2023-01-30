package me.domcie.skyplots.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class IslandData {
    private String userId;
    private Location islandLocation;
    private List<String> members;

    public IslandData(String userId, Location islandLocation, List<String> members) {
        this.userId = userId;
        this.islandLocation = islandLocation;
        this.members = members;
    }

    public String getUserId() {
        return userId;
    }

    public Location getLocation() {
        return islandLocation;
    }
    public List<String> getMembers() {
        return members;
    }

    public void setIslandLocation(Location islandLocation) {
        this.islandLocation = islandLocation;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void addMember(String member) {
        this.members.add(member);
    }

    public void removeMember(String member) {
        this.members.remove(member);
    }
    static config cfg = config.getInst();

    public static Location getBukkitLocation(String locationString) {
        String worldName = locationString.substring(locationString.indexOf("world=CraftWorld{name=") + "world=CraftWorld{name=".length(), locationString.indexOf("},x="));
        double x = Double.parseDouble(locationString.substring(locationString.indexOf("x=") + "x=".length(), locationString.indexOf(",y=")));
        double y = Double.parseDouble(locationString.substring(locationString.indexOf("y=") + "y=".length(), locationString.indexOf(",z=")));
        double z = Double.parseDouble(locationString.substring(locationString.indexOf("z=") + "z=".length(), locationString.indexOf(",pitch=")));
        float pitch = Float.parseFloat(locationString.substring(locationString.indexOf("pitch=") + "pitch=".length(), locationString.indexOf(",yaw=")));
        float yaw = Float.parseFloat(locationString.substring(locationString.indexOf("yaw=") + "yaw=".length(), locationString.length() - 1));
        World world = Bukkit.getWorld(worldName);
        Location islandLocation = new Location(world, x, y, z, yaw, pitch);
        return islandLocation;
    }

    public boolean hasPlayer(String playerId) {
        if (userId.equals(playerId)) {
            return true;
        }
        return members.contains(playerId);
    }

    public static IslandData getIslandByOwnerId(String ownerId) {
        for (IslandData island : DataStorage.islands) {
            if (island.getUserId().equals(ownerId)) {
                return island;
            }
        }
        return null;
    }

    public static IslandData getIslandByMemberId(String memberId) {
        for (IslandData island : DataStorage.islands) {
            if (island.getMembers().contains(memberId)) {
                return island;
            }
        }
        return null;
    }

    public static IslandData getIslandByLocation(Location location){
        for (IslandData island : DataStorage.islands) {
            int halfSize = (cfg.island_size)/2;
            Location islandLocation = island.getLocation();
            if (Math.abs(location.getX() - islandLocation.getX()) < halfSize &&
                    Math.abs(location.getZ() - islandLocation.getZ()) < halfSize) {
                return island;
            }
        }
        return null;
    }

    public static boolean isIslandLocationAvailable(Location location){
        if(getIslandByLocation(location)==null){
            return true;
        } else {
            return false;
        }
    }
}

