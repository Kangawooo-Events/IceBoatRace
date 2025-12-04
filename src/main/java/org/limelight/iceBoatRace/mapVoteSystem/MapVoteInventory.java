package org.limelight.iceBoatRace.mapVoteSystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.util.*;

import static org.limelight.iceBoatRace.mapVoteSystem.MapVoteCommand.voteMap;

public class MapVoteInventory implements Listener {
    private JavaPlugin plugin;

    public MapVoteInventory(JavaPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!e.getView().getTitle().equals("Vote"))
            return;

        for (int i = 0; i < e.getInventory().getSize(); i++) {
            ItemStack currentItem = e.getInventory().getItem(i);
            if (currentItem == null)
                continue;
            ItemMeta currentItemMeta = currentItem.getItemMeta();
            String currentItemName = ChatColor.stripColor(currentItemMeta.getDisplayName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "" + "Votes: " + voteMap.get(currentItemName));
            currentItemMeta.setLore(lore);
            currentItem.setItemMeta(currentItemMeta);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Vote")) // need to add or player is op (but can't whilst testing)
            return;

        e.setCancelled(true);

        Player player = (Player)e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null)
            return;
        ItemMeta itemMeta = item.getItemMeta();
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        if (!player.getPersistentDataContainer().get(new NamespacedKey(plugin, "hasVoted"), PersistentDataType.BOOLEAN)) {
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "hasVoted"), PersistentDataType.BOOLEAN, true);
            voteMap.put(itemName, voteMap.get(itemName) + 1);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "" + "Votes: " + voteMap.get(itemName));

            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            syncAllPlayers(e, e.getSlot(), item);
        }
    }

    private void syncAllPlayers(InventoryEvent e, int slot, ItemStack item) {
        List<HumanEntity> viewers = e.getViewers();

        if (viewers.isEmpty())
            return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory inventory = player.getOpenInventory().getTopInventory();

            if (inventory.getSize() == e.getView().getTopInventory().getSize()) {
                player.getOpenInventory().getTopInventory().setItem(slot, item);
                player.updateInventory();
            }
        }
    }
}
