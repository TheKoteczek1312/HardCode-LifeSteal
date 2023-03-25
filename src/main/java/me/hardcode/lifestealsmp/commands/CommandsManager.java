package me.hardcode.lifestealsmp.commands;

public class CommandsManager {
    public static void loadCommands() {
        MainCommands.getMainCommands()
                .withSubcommand(BansCommands.getAllBansCommands())
                .withSubcommand(HeartsCommands.getAllHeartsCommands())
                .withSubcommand(ItemsCommands.getAllItemsCommands())
                .register();
    }
}
