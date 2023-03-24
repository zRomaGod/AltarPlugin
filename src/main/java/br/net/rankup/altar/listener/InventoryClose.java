package br.net.rankup.altar.listener;

import br.net.rankup.altar.AltarPlugin;
import br.net.rankup.altar.misc.BukkitUtils;
import br.net.rankup.altar.model.AltarModel;
import org.bukkit.event.inventory.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import java.util.*;

public class InventoryClose implements Listener
{
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().getName().startsWith("Definir os items - ".replace('&', '§'))) {
            String name = event.getInventory().getName().replace("Definir os items - ", "");
            AltarModel altarModel = AltarPlugin.getInstance().getAltarManager().getAltars().get(name);
            if(altarModel == null) {
                BukkitUtils.sendMessage(event.getPlayer(), "&cNão foi possivel puxar o altar.");
                return;
            }
            if (altarModel.getItems() == null) {
                altarModel.setItems(new ArrayList<>());
            }

            altarModel.getItems().clear();
            for (final ItemStack itemStack : event.getInventory().getContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() != Material.AIR) {
                        altarModel.getItems().add(itemStack);
                    }
                }
            }

            List<String> list = new ArrayList<>();
            for(ItemStack item : altarModel.getItems()) {
                list.add(BukkitUtils.serializeItemStack(item));
            }
                AltarPlugin.getConfiguration().set("altars."+name+".items", list);
                AltarPlugin.getConfigUtils().saveConfig();
            BukkitUtils.sendMessage(event.getPlayer(), "&aOs itens do altar foram salvos com sucesso.");


        }
    }
}
