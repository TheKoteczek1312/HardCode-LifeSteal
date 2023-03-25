package me.hardcode.lifestealsmp.events;

import me.hardcode.lifestealsmp.bans.BanStorageUtil;
import me.hardcode.lifestealsmp.other.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.IOException;
import java.util.UUID;

public class AsyncPlayerPreLogin implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) throws IOException {
        UUID uuid = e.getUniqueId();
        if (BanStorageUtil.getBan(uuid) != null) {
            if (Config.getBoolean("banOn0Hearts")) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Config.getMessage("noMoreHeartsBan"));
            }
        }
    }


}