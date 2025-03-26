package com.mladich.thegnomemod.entity;

import com.mladich.thegnomemod.TheGnomeMod;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TheGnomeMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ModItems.ROCKEATERGNOME_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }
}
