package com.c45y.LolNo;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LolNo extends JavaPlugin {
	// Some toggle variables.
	public boolean chat_enabled;
	public boolean command_enabled;
	public boolean join_enabled;
	public boolean block_enabled;
	public ArrayList<String> mutedUsers = new ArrayList<String>(); 
	public ArrayList<String> hardMutedUsers = new ArrayList<String>(); 

	private final LolNoHandle loglistener = new LolNoHandle(this);
	Logger log = Logger.getLogger("Minecraft");

	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(loglistener, this);

		getConfig().options().copyDefaults(true);
		getConfig().addDefault("LolNo.blocks.chat", false);
		getConfig().addDefault("LolNo.blocks.command", false);
		getConfig().addDefault("LolNo.blocks.part", false);
		getConfig().addDefault("LolNo.blocks.block", false);

		mutedUsers.addAll(getConfig().getStringList("muted.users"));
		hardMutedUsers.addAll(getConfig().getStringList("muted.hard"));

		chat_enabled = getConfig().getBoolean("LolNo.blocks.chat");
		command_enabled = getConfig().getBoolean("LolNo.blocks.command");
		join_enabled = getConfig().getBoolean("LolNo.blocks.part");
		block_enabled = getConfig().getBoolean("LolNo.blocks.block");

		saveConfig();

		log.info("LolNo enabled.");
	}

	public void onDisable(){
		log.info("LolNo disabled.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("LolNo.admin") || sender.isOp()) {
			if (cmd.getName().equalsIgnoreCase("lolno")) {
				if (args.length == 1){
					if(args[0].equals("status")) {
						printStatus(sender);
						return true;
					}
					if(args[0].equals("chat")) {
						chat_enabled = !chat_enabled;
						toggleConfig("LolNo.blocks.chat");
						printStatus(sender);
						return true;
					}
					if(args[0].equals("part")) {
						join_enabled = !join_enabled;
						toggleConfig("LolNo.blocks.part");
						printStatus(sender);
						return true;
					}
					if(args[0].equals("blocks")) {
						block_enabled = !block_enabled;
						toggleConfig("LolNo.blocks.block");
						printStatus(sender);
						return true;
					}
					if(args[0].equals("commands")) {
						command_enabled = !command_enabled;
						toggleConfig("LolNo.blocks.command");
						printStatus(sender);
						return true;
					}
				}
				printHelp(sender);
			}
		}
		if (sender.hasPermission("LolNo.mod") || sender.isOp()) {
			/* Change command name, this is to stop conflicts right now */
			if (cmd.getName().equalsIgnoreCase("mutedlist")) {
				sender.sendMessage("Muted users:");
				for (String player : mutedUsers) {
					if (hardMutedUsers.contains(player)) {
						sender.sendMessage(ChatColor.GRAY + " *- " + player);
					} else {
						sender.sendMessage(ChatColor.GRAY + "  - " + player);
					}
					
		    	}
			}
			if (cmd.getName().equalsIgnoreCase("mute")) {
				if (args.length == 1) {
					if(getServer().getPlayer(args[0]) != null) {
						addMuteUser(args[0]);
						sender.sendMessage(ChatColor.RED + args[0] + " should be muted now");
						return true;
					}
				}
			}
			if (cmd.getName().equalsIgnoreCase("denytp")) {
				if (args.length == 1) {
					if(getServer().getPlayer(args[0]).isOnline()) {
						addMuteUser(args[0],true);
						sender.sendMessage(ChatColor.RED + args[0] + " has been denied teleport");
						return true;
					}
				}
			}
			if (cmd.getName().equalsIgnoreCase("unmute")) {
				if (args.length == 1){
					removeMuteUser(args[0]);
					sender.sendMessage(ChatColor.RED + args[0] + " has been unmuted");
					return true;
				}
			}
		}
		return true;
	}

	private void addMuteUser(String player) {
		addMuteUser(player,false);
	}

	private void addMuteUser(String player,boolean hard) {
		if(!mutedUsers.contains(player)) {
			mutedUsers.add(player.toLowerCase());
			getConfig().set("muted.users", mutedUsers.toArray());
		}
		if (hard && !hardMutedUsers.contains(player)) {
			hardMutedUsers.add(player.toLowerCase());
			getConfig().set("muted.hard", hardMutedUsers.toArray());
		}
		saveConfig();
	}

	private void removeMuteUser(String player) {
		if(mutedUsers.contains(player.toLowerCase())) {
			mutedUsers.remove(player.toLowerCase());
			getConfig().set("muted.users", mutedUsers.toArray());
		}
		if(hardMutedUsers.contains(player.toLowerCase())) {
			hardMutedUsers.remove(player.toLowerCase());
			getConfig().set("muted.hard", hardMutedUsers.toArray());
		}
		saveConfig();
	}

	public boolean isMuteUser(String player) {
		if (mutedUsers.contains(player.toLowerCase())) {
			return true;
		}
		return false;
	}

	public boolean isHardMuteUser(String player) {
		if (hardMutedUsers.contains(player.toLowerCase())) {
			return true;
		}
		return false;
	}

	private void toggleConfig(String node) {
		getConfig().set(node, !getConfig().getBoolean(node));
		saveConfig();
	}

	private void printHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GRAY + "Usage: /lolno [param]:");
		sender.sendMessage(ChatColor.GRAY + "    chat - Disable all chat messages.");
		sender.sendMessage(ChatColor.GRAY + "    commands - Disable the use of all commands for non staff.");
		sender.sendMessage(ChatColor.GRAY + "    part - Disable all join/part messages.");
		sender.sendMessage(ChatColor.GRAY + "    blocks - Disable all block creation/destruction.");
		sender.sendMessage(ChatColor.GRAY + "    status - Show the current status of lockdown.");
	}

	public void printStatus(CommandSender sender) {
		sender.sendMessage(ChatColor.GRAY + "Lockdown status:");
		sender.sendMessage(ChatColor.GRAY + "    Commands: " + (command_enabled ? "blocked" : "allowed"));
		sender.sendMessage(ChatColor.GRAY + "    Chat: " + (chat_enabled ? "blocked" : "allowed"));	
		sender.sendMessage(ChatColor.GRAY + "    Part: " + (join_enabled ? "blocked" : "allowed"));
		sender.sendMessage(ChatColor.GRAY + "    Blocks: " + (block_enabled ? "blocked" : "allowed"));
	}
}