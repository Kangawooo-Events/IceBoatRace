package org.limelight.iceBoatRace.mapVoteSystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.util.*;

import static org.limelight.iceBoatRace.mapVoteSystem.MapVoteCommand.voteMap;
import static org.limelight.iceBoatRace.IceBoatRace.eventStatus;

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
            String currentItemName = currentItemMeta.getDisplayName();

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
        String itemName = item.getItemMeta().getDisplayName();

        String prevMapName = player.getPersistentDataContainer().get(new NamespacedKey(plugin, "votedMap"), PersistentDataType.STRING);

        if (!prevMapName.equals(itemName)) {

            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "votedMap"), PersistentDataType.STRING, itemName);
            voteMap.put(itemName, voteMap.get(itemName) + 1);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "" + "Votes: " + voteMap.get(itemName));

            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            syncAllPlayers(e, e.getSlot(), item);

            if (voteMap.containsKey(prevMapName)) {
                voteMap.put(prevMapName, voteMap.get(prevMapName)-1);
                int prevItemSlot = FindItemSlotByName(e.getView().getTopInventory(), prevMapName);
                ItemStack prevItem = e.getView().getTopInventory().getItem(prevItemSlot);
                ItemMeta prevItemMeta = prevItem.getItemMeta();

                List<String> prevItemLore = new ArrayList<>();
                prevItemLore.add(ChatColor.AQUA + "" + "Votes: " + voteMap.get(prevMapName));

                prevItemMeta.setLore(prevItemLore);
                prevItem.setItemMeta(prevItemMeta);

                syncAllPlayers(e, FindItemSlotByName(e.getView().getTopInventory(), prevMapName), prevItem);
            }
        }
        else {
            voteMap.put(prevMapName, voteMap.get(itemName) - 1);
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "votedMap"), PersistentDataType.STRING, "");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "" + "Votes: " + voteMap.get(itemName));

            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            syncAllPlayers(e, e.getSlot(), item);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!eventStatus.equals("voting"))
            return;
        Player player = (Player) e.getPlayer();
        InventoryView closedInventoryView = e.getView();

        if (closedInventoryView.getTitle().equals("Vote")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.openInventory(closedInventoryView);
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    private void syncAllPlayers(InventoryEvent e, int slot, ItemStack item) {
        List<HumanEntity> viewers = e.getViewers();

        if (viewers.isEmpty())
            return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getOpenInventory().getTopInventory().setItem(slot, item);
            player.updateInventory();
        }
    }

    private int FindItemSlotByName(Inventory inv, String name) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && inv.getItem(i).getItemMeta().getDisplayName().equals(name))
                return i;
        }
        return -1;
    }
}
