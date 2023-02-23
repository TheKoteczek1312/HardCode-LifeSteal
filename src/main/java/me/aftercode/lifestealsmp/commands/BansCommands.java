package me.aftercode.lifestealsmp.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.aftercode.lifestealsmp.Main;
import me.aftercode.lifestealsmp.bans.BanStorageUtil;
import me.aftercode.lifestealsmp.bans.models.Ban;
import me.aftercode.lifestealsmp.other.Config;
import org.bukkit.OfflinePlayer;

import java.io.IOException;

public class BansCommands {
    public static CommandAPICommand getAllBansCommands() {
        return new CommandAPICommand("bans")
                .withPermission("lifesteal.bans")
                .executes((sender, args) -> {
                    sender.sendMessage("§6§lBans:");
                    for (Ban ban : BanStorageUtil.findAllBans()) {
                        sender.sendMessage("§c" + Main.getInstance().getServer().getOfflinePlayer(ban.getPlayerUUID()).getName());
                    }
                })
                .withSubcommand(BansCommands.getRemoveBanCommand());
    }

    private static CommandAPICommand getRemoveBanCommand() {
        return new CommandAPICommand("remove")
                .withPermission("lifesteal.bans.remove")
                .withArguments(new OfflinePlayerArgument("player"))
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args[0];
                    try {
                        if (player.getName() == null) {
                            sender.sendMessage(Config.getMessage("playerNotFound"));
                        } else {
                            if (BanStorageUtil.deleteBan(player.getUniqueId())) {
                                sender.sendMessage(Config.getMessage("banRemoved").replace("${player}", player.getName()));

                            } else {
                                sender.sendMessage(Config.getMessage("playerNotBanned").replace("${player}", player.getName()));
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
