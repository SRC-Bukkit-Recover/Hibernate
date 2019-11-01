package com.woutwoot.hibernate;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {

  private boolean enabled = true;
  private BukkitTask task;

  @Override
  public void onDisable() {
    if (task != null && !task.isCancelled()) {
      task.cancel();
    }
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    if (command.getName().equalsIgnoreCase("hibernate") && (sender.isOp() || sender
        .hasPermission("hibernate.toggle"))) {
      sender.sendMessage(
          "[Hibernate] Hibernate is now " + (this.toggleEnabled() ? "enabled" : "disabled"));
      return true;
    }
    return false;
  }

  private boolean toggleEnabled() {
    this.enabled = !this.enabled;
    return this.enabled;
  }

  @Override
  public void onEnable() {
    task = (new BukkitRunnable() {
      boolean firstRun = true;

      @Override
      public void run() {
        if (!(Bukkit.getServer().getOnlinePlayers().isEmpty() && Main.this.enabled)) {
          return;
        }
        if (this.firstRun) {
          for (final World w : Bukkit.getWorlds()) {
            for (final Chunk c : w.getLoadedChunks()) {
              c.unload(true);
            }
          }
          this.firstRun = false;
        }
        try {
          Thread.sleep(1000L);
          this.firstRun = false;
        } catch (Exception ignored) {
          // IGNORED
        }
      }
    }).runTaskTimer(this, 0, 1);
  }
}
