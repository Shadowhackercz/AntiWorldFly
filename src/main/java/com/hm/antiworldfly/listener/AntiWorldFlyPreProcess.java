package com.hm.antiworldfly.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.hm.antiworldfly.AntiWorldFly;
import com.hm.antiworldfly.AntiWorldFlyRunnable;
import com.hm.antiworldfly.language.Lang;

public class AntiWorldFlyPreProcess implements Listener {

	private AntiWorldFly plugin;

	public AntiWorldFlyPreProcess(AntiWorldFly awf) {

		this.plugin = awf;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {

		if (plugin.isDisabled() || event.getPlayer().hasPermission("antiworldfly.fly"))
			return;

		String command = event.getMessage().toLowerCase();

		// Check for most common fly commands and aliases.
		if (command.startsWith("/fly") || command.startsWith("/essentials:fly") || command.startsWith("/efly")) {

			blockCommand(event);
		}
		// Check for creative mode commands.
		else if (command.startsWith("/gm 1") || command.startsWith("/gamemode c") || command.startsWith("/gm c")) {

			if (!this.plugin.isAntiFlyCreative())
				return;

			for (String world : plugin.getAntiFlyWorlds()) {
				if (event.getPlayer().getWorld().getName().equalsIgnoreCase(world)) {
					// Schedule runnable to disable flying.
					Bukkit.getServer()
							.getScheduler()
							.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("AntiWorldFly"),
									new AntiWorldFlyRunnable(event.getPlayer(), plugin), 20);

					break;

				}
			}
		} else {
			// Check if other commands were blocked by the user.
			boolean otherBlockedCommand = false;
			if (plugin.getOtherBlockedCommands().size() != 0)
				for (String blockedCommand : plugin.getOtherBlockedCommands()) {
					if (blockedCommand.equalsIgnoreCase(command))
						otherBlockedCommand = true;
				}
			if (otherBlockedCommand)
				blockCommand(event);
		}

	}

	/**
	 * Block a command and cancel corresponding event.
	 */
	private void blockCommand(PlayerCommandPreprocessEvent event) {

		if (!this.plugin.isAntiFlyCreative() && event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;

		for (String world : plugin.getAntiFlyWorlds()) {
			if (event.getPlayer().getWorld().getName().equalsIgnoreCase(world)) {
				event.getPlayer().sendMessage(plugin.getChatHeader() + Lang.COMMAND_DISABLED_CHAT);
				event.setCancelled(true);
				break;

			}
		}
	}
}