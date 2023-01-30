package me.domcie.skyplots.data;

import me.domcie.skyplots.SkyPlots;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class DataStorage {
    private static boolean saveToFile;
    private static String mysqlUrl;
    private static String mysqlUsername;
    private static String mysqlPassword;
    private static Connection mysqlConnection;

    public static List<IslandData> islands = new ArrayList<>();

    config cfg = config.getInst();

    public DataStorage() {
        String type = cfg.DataType;
        if (type.equalsIgnoreCase("mysql")) {
            saveToFile = false;
            mysqlUrl = "jdbc:mysql://" + cfg.host + ":" + cfg.port + "/" + cfg.dbname;
            mysqlUsername = cfg.user;
            mysqlPassword = cfg.password;
        } else {
            saveToFile = true;
        }
    }

    public void initialize(){
        if (!saveToFile) {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://" + cfg.host + ":" + cfg.port + "/" + cfg.dbname, cfg.user, cfg.password);
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE IF NOT EXISTS islands (userId VARCHAR(36), islandLocation CHAR(255), members VARCHAR(36))");
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Error connecting to MySQL");
                saveToFile = true;
            }
        } else {

            File configFile = new File(SkyPlots.getInst().getDataFolder(), "skyblockdata.yml");
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                    SkyPlots.getInst().getLogger().info("skyblockdata.yml has been created!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        load();
    }
    public void load() {
        if (saveToFile) {
            // load data from file
            try (FileInputStream input = new FileInputStream(SkyPlots.getInst().getDataFolder() + "/skyblockdata.yml")) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(input);
                if(Objects.nonNull(data)) {
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        String userId = entry.getKey();
                        Map<String, Object> userData = (Map<String, Object>) entry.getValue();

                        Location islandLocation = IslandData.getBukkitLocation((String) userData.get("location"));

                        String membersString = (String) userData.get("members");
                        if(membersString == null) membersString = "";
                        List<String> membersList = Arrays.asList(membersString.split(";"));


                        IslandData islandData = new IslandData(userId, islandLocation, membersList);
                        islands.add(islandData);

                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        } else {
            // load data from mysql
            try {
                mysqlConnection = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);
                Statement stmt = mysqlConnection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT userId, islandLocation, members FROM islands");
                while (rs.next()) {
                    String userId = rs.getString("userId");
                    Location islandLocation = IslandData.getBukkitLocation(rs.getString("islandLocation"));

                    String members = rs.getString("members");
                    List<String> membersList = Arrays.asList(members.split(";"));
                    if(membersList == null) membersList = new ArrayList<>();

                    IslandData islandData = new IslandData(userId, islandLocation, membersList);
                    islands.add(islandData);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        if (saveToFile) {
            try {
                FileWriter fw = new FileWriter(SkyPlots.getInst().getDataFolder() + "/skyblockdata.yml", false);
                BufferedWriter bw = new BufferedWriter(fw);

                for (IslandData island : islands) {
                    bw.write(island.getUserId()+":");
                    bw.newLine();
                    bw.write("  location: "+island.getLocation().toString());
                    bw.newLine();
                    bw.write("  members: "+String.join(",", island.getMembers()));
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mysqlConnection = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);
                for (IslandData island : islands) {
                    PreparedStatement stmt = mysqlConnection.prepareStatement("INSERT INTO islands(userId, islandLocation, members) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE islandLocation = ?, members = ?");
                    stmt.setString(1, island.getUserId());
                    stmt.setString(2, island.getLocation().toString());
                    stmt.setString(3, String.join(";", island.getMembers()));
                    stmt.setString(4, island.getLocation().toString());
                    stmt.setString(5, String.join(";", island.getMembers()));
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void delete(String userId) {
        if (saveToFile) {
            // delete data from file
            try {
                List<String> lines = new ArrayList<>();
                BufferedReader br = new BufferedReader(new FileReader("skyblockdata.yml"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(userId + ":")) {
                        lines.add(line);
                    }
                }
                BufferedWriter bw = new BufferedWriter(new FileWriter("skyblockdata.yml"));
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            load();
        } else {
            // delete data from mysql
            try {
                mysqlConnection = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);
                PreparedStatement stmt = mysqlConnection.prepareStatement("DELETE FROM skyblockdata WHERE userId = ?");
                stmt.setString(1, userId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            load();
        }
    }

}