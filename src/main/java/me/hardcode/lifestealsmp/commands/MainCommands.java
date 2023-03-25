package me.hardcode.lifestealsmp.commands;

import dev.jorel.commandapi.CommandAPICommand;
import me.hardcode.lifestealsmp.Main;
import me.hardcode.lifestealsmp.bans.BanStorageUtil;
import me.hardcode.lifestealsmp.other.Config;
import me.hardcode.lifestealsmp.other.Debug;
import me.hardcode.lifestealsmp.other.Items;
import me.hardcode.lifestealsmp.other.LootPopulator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

public class MainCommands {
    public static CommandAPICommand getMainCommands() {
        return new CommandAPICommand("lifesteal")
                .withShortDescription("Main LifeSteal command.")
                .withAliases("ls", "hcsmpls", "hcsmp-ls", "hardcodesmp", "hardcode-smp")
                .executes((sender, args) -> {
                    sender.sendMessage("§cHardCode-LSSMP §a§l" + Main.getInstance().getDescription().getVersion());
                    sender.sendMessage("§cStworzony przez §eTheKoteczek");
                    sender.sendMessage("§6https://discord.gg/Xf4FtryrWp");
                })
                .withSubcommand(MainCommands.getWithdrawCommand())
                .withSubcommand(MainCommands.getHelpCommand())
                .withSubcommand(MainCommands.getReloadCommand())
                .withSubcommand(MainCommands.getDebugInfoCommand());

    }

    private static CommandAPICommand getHelpCommand() {
        return new CommandAPICommand("help")
                .withShortDescription("Help command.")
                .executes((sender, args) -> {
                    sender.sendMessage("§aOdpowiedzi na większość pytań można znaleźć w dokumentacji:");
                    sender.sendMessage("§6§https://discord.gg/Xf4FtryrWp");
                });
    }

    public static CommandAPICommand getDebugInfoCommand() {
        return new CommandAPICommand("debug")
                .withPermission("hardcode.debug")
                .withShortDescription("Debug command.")
                .executes((sender, args) -> {

                    Debug.postDebug(Debug.makeDebug(Main.getInstance())).whenComplete((key, exception) -> {
                        if (exception != null) {
                            Main.getInstance().getLogger().log(Level.WARNING, "Nie udało się opublikować informacji debugowania.", exception);

                            sender.sendMessage("§cNie udało się opublikować informacji o debugowaniu, sprawdź konsolę.");
                            return;
                        }

                        sender.sendMessage("§aLink do informacji o debugowaniu: §e§l" + Debug.URL + key);
                    });
                });
    }

    private static CommandAPICommand getWithdrawCommand() {
        return new CommandAPICommand("withdraw")
                .withShortDescription("Withdraws heart.")
                .executes((sender, args) -> {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (!Config.getBoolean("heartItem.withdraw-enabled")) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
                            sender.sendMessage(Config.getMessage("featureDisabled"));
                            return;
                        }
                        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2 <= 0) {
                            try {
                                player.getInventory().addItem(Items.ExtraHeart.getExtraHeart(100));
                                player.updateInventory();
                                player.sendMessage(Config.getMessage("heartWithdrawn"));
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
                                BanStorageUtil.createBan(player);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2);
                            // check if player's inventory isnt full
                            if (player.getInventory().firstEmpty() == -1) {
                                player.getWorld().dropItem(player.getLocation(), Items.ExtraHeart.getExtraHeart(100));
                                player.sendMessage(Config.getMessage("heartWithdrawn"));
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
                            } else {
                                player.getInventory().addItem(Items.ExtraHeart.getExtraHeart(100));
                                player.updateInventory();
                                player.sendMessage(Config.getMessage("heartWithdrawn"));
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
                            }
                        }

                    } else {
                        sender.sendMessage(Config.getMessage("urNotPlayer"));
                    }
                });
    }

    private static CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("hardcode.reload")
                .withShortDescription("Reloads config.")
                .executes((sender, args) -> {
                    if (Config.getBoolean("recipe.enabled")) {
                        Main.getInstance().getConfig().getConfigurationSection("recipe.recipes").getKeys(false).forEach(recipe -> {
                            if (Config.getBoolean("recipe.recipes." + recipe + ".recipe-enabled")) {
                                if (Config.getBoolean("recipe.recipes." + recipe + ".discover")) {
                                    String itemName = Config.getString("recipe.recipes." + recipe + ".item");
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.undiscoverRecipe(new NamespacedKey("lifesteal", itemName + recipe));
                                    }
                                }
                            }
                        });
                    }
                    Items.Recipes.unregisterRecipes();
                    for (World world : Bukkit.getServer().getWorlds()) {
                        world.getPopulators().removeIf(blockPopulator -> blockPopulator instanceof LootPopulator);
                    }
                    Main.getInstance().reloadConfig();
                    sender.sendMessage(Config.getMessage("configReloaded"));
                    Items.Recipes.registerRecipes();
                    if (Config.getBoolean("recipe.enabled")) {
                        Main.getInstance().getConfig().getConfigurationSection("recipe.recipes").getKeys(false).forEach(recipe -> {
                            if (Config.getBoolean("recipe.recipes." + recipe + ".recipe-enabled")) {
                                if (Config.getBoolean("recipe.recipes." + recipe + ".discover")) {
                                    String itemName = Config.getString("recipe.recipes." + recipe + ".item");
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.discoverRecipe(new NamespacedKey("lifesteal", itemName + recipe));
                                    }
                                }
                            }
                        });
                    }
                    sender.sendMessage(Config.getMessage("recipesReloaded"));
                    if (Config.getBoolean("loot.enabled")) {
                        for (World world : Bukkit.getServer().getWorlds()) {
                            world.getPopulators().add(new LootPopulator(Main.getInstance()));
                        }
                    }
                    sender.sendMessage(Config.getMessage("lootReloaded"));

                });
    }
}
