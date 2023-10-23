package net.oilcake.mitelros.mixins.item;

import net.minecraft.*;
import net.oilcake.mitelros.item.Materials;
import net.oilcake.mitelros.util.ExperimentalConfig;
import net.oilcake.mitelros.util.StuckTagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ItemMeat.class)
public class ItemMeatMixin extends ItemFood {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectInit(CallbackInfo callbackInfo){
        this.setWater(-1);
    }
    @Shadow public boolean is_cooked;

    public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
        if (player.onServer()) {
            if(!this.is_cooked){
                Random rand;
                rand = new Random();
                if(rand.nextInt(!ExperimentalConfig.TagConfig.Realistic.ConfigValue ? 1 : 2) != 0){
                    player.addPotionEffect(new MobEffect(MobEffectList.hunger.id,600,0));
                }
            }
        }
        super.onItemUseFinish(item_stack, world, player);
    }
}
