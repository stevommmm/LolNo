package com.c45y.LolNo;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LolNoHandle implements Listener {
	public final LolNo plugin;

	public LolNoHandle(LolNo instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOWEST , ignoreCancelled = true)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if ( plugin.command_enabled ) {
			if (!event.getPlayer().hasPermission("LolNo.admin") && !event.getPlayer().hasPermission("LolNo.mod")) {
				plugin.log.info("LolNo blocked: " + event.getMessage() + " from " + event.getPlayer().getName());
				event.setCancelled(true);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (plugin.isHardMuteUser(event.getPlayer().getName().toLowerCase())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.DARK_RED + "You are not allowed to teleport.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if ( plugin.chat_enabled ) {
			plugin.log.info("LolNo blocked: " + event.getPlayer().getName() + " said: " + event.getMessage());
			event.setCancelled(true);
		}
		if (plugin.isMuteUser(event.getPlayer().getName().toLowerCase())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.DARK_RED + "You are muted, try politely requesting a mod to unmute you");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if ( plugin.join_enabled ) {
			event.setJoinMessage("");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if ( plugin.join_enabled ) {
			event.setQuitMessage("");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if ( plugin.block_enabled ) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if ( plugin.block_enabled ) {
			event.setCancelled(true);
		}
	}
}