package br.net.rankup.altar.commands;

import br.net.rankup.altar.AltarPlugin;
import br.net.rankup.altar.manager.AltarManager;
import br.net.rankup.altar.misc.BukkitUtils;
import br.net.rankup.altar.misc.LocationSerializer;
import br.net.rankup.altar.model.AltarModel;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class AltarCommand {

    @Command(name = "altar")
    public void handleAltarCommand(Context<CommandSender> context) {
        CommandSender execution = context.getSender();

    }

    @Command(name = "altar.setitem")
    public void handleSetItemCommand(Context<CommandSender> context, String name) {
        CommandSender execution = context.getSender();
        Player player = (Player) execution;
        AltarModel altarModel = AltarPlugin.getInstance().getAltarManager().getAltars().get(name);

        if(altarModel == null) {
            BukkitUtils.sendMessage(player,"&cNão existe um altar com esse nome.");
            return;
        }
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 54, "Definir os items - "+name.replace('&', '§'));
        if (altarModel.getItems() != null && !altarModel.getItems().isEmpty()) {
            inventory.setContents(altarModel.getItems().toArray(new ItemStack[0]));
        }
        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
    }

    @Command(name = "altar.setlocation")
    public void handleSetLocationCommand(Context<CommandSender> context, String name) {
        CommandSender execution = context.getSender();
        Player player = (Player) execution;
        AltarModel altarModel = AltarPlugin.getInstance().getAltarManager().getAltars().get(name);

        if(altarModel == null) {
            BukkitUtils.sendMessage(player,"&cNão existe um altar com esse nome.");
            return;
        }
        altarModel.setLocation(player.getLocation());
        altarModel.getEntity().teleport(altarModel.getLocation());
        altarModel.getHologram().teleport(altarModel.getLocation().add(0, altarModel.getHologramHeight(), 0));
        AltarPlugin.getConfiguration().set("altars."+name+".location", LocationSerializer.serialize(player.getLocation()));
        AltarPlugin.getConfigUtils().saveConfig();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        BukkitUtils.sendMessage(player,"&aVocê definiu o local do altar.");
    }

}
