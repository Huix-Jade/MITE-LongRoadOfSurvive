package net.oilcake.mitelros.mixins.world.biome;

import net.minecraft.*;
import net.oilcake.mitelros.entity.EntityEvil;
import net.oilcake.mitelros.entity.EntityPigmanLord;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeHell.class)
public class BiomeHellMixin extends BiomeBase {
    protected BiomeHellMixin(int par1) {
        super(par1);
    }

    @Inject(method = "<init>",at = @At("RETURN"))
    public void injectCtor(CallbackInfo callbackInfo) {
        this.spawnableMonsterList.clear();
        this.spawnableMonsterList.add(new BiomeMeta(EntityInfernalCreeper.class, 20, 1, 3));
        this.spawnableMonsterList.add(new BiomeMeta(EntityDemonSpider.class, 20, 1, 4));
        this.spawnableMonsterList.add(new BiomeMeta(EntityHellhound.class, 20, 1, 4));
        this.spawnableMonsterList.add(new BiomeMeta(EntityEvil.class, 50, 1, 4));
        this.spawnableMonsterList.add(new BiomeMeta(EntityPigmanLord.class, 5, 1, 1));
    }
}
