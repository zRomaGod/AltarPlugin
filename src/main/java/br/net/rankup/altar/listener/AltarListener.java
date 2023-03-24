package br.net.rankup.altar.listener;

import br.net.rankup.altar.AltarPlugin;
import br.net.rankup.altar.inventory.ContentsInventory;
import br.net.rankup.altar.misc.InventoryUtils;
import br.net.rankup.altar.model.AltarModel;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class AltarListener implements Listener {


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(entity.hasMetadata("altar")) {
            event.setCancelled(true);
        }
     }


    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {
        for (final Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata("altar")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked().hasMetadata("altar")) {
            String name = event.getRightClicked().getMetadata("altar").get(0).asString();
            AltarModel altarModel = AltarPlugin.getInstance().getAltarManager().getAltars().get(name);
            if(!InventoryUtils.getList().contains(event.getPlayer().getName())) {
                RyseInventory inventory = new ContentsInventory(altarModel).build();
                inventory.open(event.getPlayer());
                InventoryUtils.addDelay(event.getPlayer());
            }
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            event.setCancelled(true);
        }
    }

}
