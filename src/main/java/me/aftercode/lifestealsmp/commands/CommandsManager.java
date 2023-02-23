package me.aftercode.lifestealsmp.commands;

import static me.aftercode.lifestealsmp.commands.BansCommands.getAllBansCommands;
import static me.aftercode.lifestealsmp.commands.HeartsCommands.getAllHeartsCommands;
import static me.aftercode.lifestealsmp.commands.ItemsCommands.getAllItemsCommands;
import static me.aftercode.lifestealsmp.commands.MainCommands.getMainCommands;

public class CommandsManager {
    public static void loadCommands() {
        MainCommands.getMainCommands()
                .withSubcommand(getAllBansCommands())
                .withSubcommand(HeartsCommands.getAllHeartsCommands())
                .withSubcommand(ItemsCommands.getAllItemsCommands())
                .register();
    }
}
