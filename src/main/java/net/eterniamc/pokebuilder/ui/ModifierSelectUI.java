package net.eterniamc.pokebuilder.ui;

import com.google.common.collect.ImmutableMap;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.eterniamc.dynamicui.DynamicUI;
import net.eterniamc.pokebuilder.ui.component.PokeballComponent;
import net.eterniamc.pokebuilder.controller.*;
import net.eterniamc.pokebuilder.util.ItemUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class ModifierSelectUI extends DynamicUI {

    private static final Map<Integer, ModifierController> MODIFIERS = ImmutableMap.<Integer, ModifierController>builder()
            .put(10, NatureController.INSTANCE)
            .put(19, HiddenAbilityController.INSTANCE)
            .put(28, ShinyController.INSTANCE)
            .put(16, IVController.INSTANCE)
            .put(25, GrowthController.INSTANCE)
            .put(34, GenderController.INSTANCE)
            .build();

    private final Pokemon pokemon;

    public ModifierSelectUI(Pokemon pokemon) {
        this.pokemon = pokemon;

        addComponent(new PokeballComponent(12, pokemon));
    }

    @Override
    public void generateInventory() {
        inventory = createInventory("Choose A Modifier", 5);
    }

    @Override
    public void loadListeners() {
        super.loadListeners();

        for (Integer slot : MODIFIERS.keySet()) {
            ModifierController controller = MODIFIERS.get(slot);

            if (controller.canApply(pokemon)) {
                addListener(slot, (player, action) -> {
                    controller.process(pokemon);
                    if (isValid()) {
                        render();
                    }
                });
            }
        }
    }

    @Override
    public void render() {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.WHITE.getMetadata());
            ItemUtils.setDisplayName(stack, " ");
            inventory.setInventorySlotContents(i, stack);
        }

        for (Integer slot : MODIFIERS.keySet()) {
            ModifierController controller = MODIFIERS.get(slot);

            ItemStack display = controller.getDisplay(pokemon);
            if (ConfigController.INSTANCE.isBlacklisted(controller.getType())) {
                ItemUtils.setItemLore(display, "&cThis modifier is blacklisted");
            }
            setItem(slot, display);
        }

        super.render();
    }
}
