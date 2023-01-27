package io.github.mrlulu51.anvilauth;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public final class AnvilAuth extends JavaPlugin implements Listener {

    private AuthMeApi api;

    @Override
    public void onEnable() {
        if(this.getServer().getPluginManager().getPlugin("AuthMe") == null) return;

        this.api = AuthMeApi.getInstance();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getOnlinePlayers().forEach(this::join);
    }

    @EventHandler
    public void join(final PlayerJoinEvent e) {
        this.join(e.getPlayer());
    }

    private void join(final Player player) {
        if(this.api.isRegistered(player.getName())) login(player);
        register(player);
    }

    private void login(final Player player) {
        final AnvilGUI.Builder builder = new AnvilGUI.Builder()
                .onComplete(((p, s) -> {
                    if(!this.api.checkPassword(p.getName(), s)) return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Wrong password"));
                    this.api.forceLogin(p);
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }))
                .preventClose()
                .text("Insert Password")
                .plugin(this);

        this.getServer().getScheduler().runTask(this, () -> builder.open(player));
    }

    private void register(final Player player) {
        final AnvilGUI.Builder builder = new AnvilGUI.Builder()
                .onComplete((p, s) -> {
                    this.api.registerPlayer(p.getName(), s);
                    if(!this.api.isAuthenticated(p)) this.api.forceLogin(p);
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                })
                .preventClose()
                .text("Insert Password")
                .plugin(this);

        this.getServer().getScheduler().runTask(this, () -> builder.open(player));
    }
}
