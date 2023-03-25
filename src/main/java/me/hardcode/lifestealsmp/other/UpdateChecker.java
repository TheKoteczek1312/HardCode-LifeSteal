package me.hardcode.lifestealsmp.other;

import me.hardcode.lifestealsmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class UpdateChecker {

    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void init() {
        new UpdateChecker(Main.getInstance()).getVersion(version -> {
            if (Main.getInstance().getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------HardCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
                if (Main.getInstance().getDescription().getVersion().contains("Alpha") || Main.getInstance().getDescription().getVersion().contains("Beta")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- NIE UŻYWAJ TEJ WTYCZKI W PRODUKCJI!");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- NIEKTÓRE FUNKCJE NIE SĄ JESZCZE UKOŃCZONE!");
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "- Używasz aktualnej wersji pluginu.");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- Dziękuję za korzystanie z mojego pluginu!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------HardCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");

            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------HardCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
                if (Main.getInstance().getDescription().getVersion().contains("Alpha") || Main.getInstance().getDescription().getVersion().contains("Beta")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- NIE UŻYWAJ TEJ WTYCZKI W PRODUKCJI!");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- NIEKTÓRE FUNKCJE NIE SĄ JESZCZE UKOŃCZONE!");
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- Istnieje nowsza wersja niż twoja! (" + version + ")");
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- Proszę pobrać nową wersję ze SpigotMC lub Github, discord.");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- Dziękuję za korzystanie z mojej wtyczki!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------HardCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
            }
        });

        Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            new UpdateChecker(Main.getInstance()).getVersion(version -> {
                if (!Main.getInstance().getDescription().getVersion().equals(version)) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------HardCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- ZOSTAŁA WYDANA NOWA AKTUALIZACJA! (" + version + ")");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- Proszę pobrać nową wersję ze SpigotMC lub Github, discord.");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------HardCode-LifeSteal-" + Main.getInstance().getDescription().getVersion() + "--------");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.hasPermission("lifesteal.update") || player.isOp()) {
                            player.sendMessage("§a§lHardCode-LifeSteal §7§l> §c§lPobierz najnowszą wersje pluginu!");
                        }
                    });
                }
            });
        }, 0L, 36000L);
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // make api get request to https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=101967 then get response json
            try {
                URL url = new URL("https://api.github.com/repos/TheKoteczek1312/HardCode-LifeSteal/releases");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(response);
                JSONObject json = (JSONObject) array.get(0);
                consumer.accept(String.valueOf(json.get("tag_name")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
 