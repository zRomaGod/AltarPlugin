package br.net.rankup.altar.inventory;

import br.net.rankup.altar.AltarPlugin;
import br.net.rankup.altar.misc.ItemBuilder;
import br.net.rankup.altar.model.AltarModel;
import com.google.common.collect.ImmutableList;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import io.github.rysefoxx.inventory.plugin.pattern.SlotIteratorPattern;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ContentsInventory implements InventoryProvider {

    private final AltarModel altarModel;
    public ContentsInventory(AltarModel altarModel) {
        this.altarModel = altarModel;
    }

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Conteúdos do Altar".replace("&", "§"))
                .rows(5)
                .provider(this)
                .disableUpdateTask()
                .build(AltarPlugin.getInstance());
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        if (altarModel.getItems().size() == 0) {
            ItemStack empty = new ItemBuilder(Material.WEB, 1, 0)
                    .owner(player.getName()).setName("§cVazio").setLore(ImmutableList.of(
                            "§7Não existe nenhúm drop configurado."
                    )).build();
            contents.set(22, empty);
        } else {

            Pagination pagination = contents.pagination();
            pagination.iterator(SlotIterator.builder().withPattern(SlotIteratorPattern.builder().define(
                                    "XXXXXXXXX",
                                    "XOOOOOOOX",
                                    "XOOOOOOOX",
                                    "XOOOOOOOX",
                                    "XXXXXXXXX",
                                    "XXXXXXXXX")
                            .attach('O')
                            .buildPattern())
                    .build());
            pagination.setItemsPerPage(21);


            altarModel.getItems().forEach(ItemStack -> {
                if(ItemStack != null) {
                    IntelligentItem intelligentItem = IntelligentItem.of(ItemStack, event -> {
                       event.setCancelled(true);
                    });
                    pagination.addItem(intelligentItem);
                }
            });


            if (!pagination.isFirst()) {
                ItemStack itemStack = new ItemBuilder(Material.ARROW).setName("§aPágina anterior").toItemStack();
                IntelligentItem intelligentItem = IntelligentItem.of(itemStack, event -> {
                    if (event.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                        pagination.inventory().open(player, pagination.previous().page());
                    }
                });
                contents.set(36, intelligentItem);
            }

            if (!pagination.isLast()) {
                ItemStack itemStack = new ItemBuilder(Material.ARROW).setName("§aPróxima página").toItemStack();
                IntelligentItem intelligentItem = IntelligentItem.of(itemStack, event -> {
                    if (event.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                        pagination.inventory().open(player, pagination.next().page());
                    }
                });
                contents.set(44, intelligentItem);
            }
        }
    }

}
