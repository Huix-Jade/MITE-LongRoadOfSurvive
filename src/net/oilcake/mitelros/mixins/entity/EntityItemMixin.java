package net.oilcake.mitelros.mixins.entity;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityItem.class)
public class EntityItemMixin extends Entity{
    public EntityItemMixin(World par1World) {
        super(par1World);
    }
    @Overwrite
    public EntityDamageResult attackEntityFrom(Damage damage) {
        EntityDamageResult result = super.attackEntityFrom(damage);
        if (result != null && !result.entityWasDestroyed()) {
            ItemStack item_stack = this.getEntityItem();
            if (item_stack == null) {
                Minecraft.setErrorMessage("attackEntityFrom: EntityItem had null item_stack");
                return null;
            } else {
                Item item = item_stack.getItem();
                if (item == null) {
                    Minecraft.setErrorMessage("attackEntityFrom: EntityItem had null item");
                    return null;
                } else if (item == Item.netherStar && damage.isExplosion()) {
                    return null;
                } else if (damage.isLavaDamage() && this.isHarmedByLava()) {
                    return this.destroyItem(damage.getSource()) ? result.setEntityWasDestroyed() : result.setEntityWasAffected();
                } else if (damage.isFireDamage() && this.getEntityItem().canDouseFire()) {
                    return this.destroyItem(damage.getSource()) ? result.setEntityWasDestroyed() : result.setEntityWasAffected();
                } else if (damage.getSource() == DamageSource.pepsin && !this.isHarmedByPepsin()) {
                    return null;
                } else if (damage.getSource() == DamageSource.acid && !this.isHarmedByAcid()) {
                    return null;
                } else {
                    this.setBeenAttacked();
                    if (item_stack.isItemStackDamageable()) {
                        float scaled_damage = damage.getAmount() * 20.0F * 5.0F;
                        if (item instanceof ItemArmor) {
                            scaled_damage *= (float)Item.plateIron.getMaxDamage(EnumQuality.average) / (float)Item.swordIron.getMaxDamage(EnumQuality.average);
                        } else if (!(item instanceof ItemTool)) {
                            scaled_damage = damage.getAmount();
                        }

                        if (scaled_damage < 1.0F) {
                            scaled_damage = 1.0F;
                        }

                        result.startTrackingHealth((float)item_stack.getRemainingDurability());
                        ItemDamageResult idr = item_stack.tryDamageItem(this.worldObj, Math.round(scaled_damage), false);
                        result.finishTrackingHealth((float)item_stack.getRemainingDurability());
                        if (idr != null && idr.itemWasDestroyed()) {
                            this.health = 0;
                        } else {
                            this.health = 5 * item_stack.getItemDamage() / item_stack.getMaxDamage();
                            if (this.health < 1) {
                                this.health = 1;
                            }
                        }
                    } else {
                        if (damage.isFireDamage() && item instanceof ItemFood) {
                            ItemFood item_food = (ItemFood)item;
                            if (item_food.getCookedItem() != null || item_food.getUncookedItem() != null) {
                                int xp_reward;
                                int xp_share;
                                if (item_food.getCookedItem() != null) {
                                    int x = this.getBlockPosX();
                                    xp_reward = this.getBlockPosY();
                                    xp_share = this.getBlockPosZ();

                                    for(int dx = -1; dx <= 1; ++dx) {
                                        for(int dz = -1; dz <= 1; ++dz) {
                                            Block block = this.worldObj.getBlock(x + dx, xp_reward, xp_share + dz);
                                            if (block == Block.fire) {
                                                this.worldObj.getAsWorldServer().addScheduledBlockOperation(EnumBlockOperation.try_extinguish_by_items, x + dx, xp_reward, xp_share + dz, (this.worldObj.getTotalWorldTime() / 10L + 1L) * 10L, false);
                                            }
                                        }
                                    }
                                }

                                this.cooking_progress += damage.getAmount() * 3.0F;
                                if (this.cooking_progress >= 100.0F) {
                                    ItemStack cooked_item_stack = item.getItemProducedWhenDestroyed(item_stack, damage.getSource());
                                    if (cooked_item_stack == null) {
                                        this.setDead();
                                        return result.setEntityWasDestroyed();
                                    }

                                    if (item instanceof ItemMeat) {
                                        this.playSound("imported.random.sizzle", 1.0F, 1.0F);
                                    }

                                    this.setEntityItemStack(cooked_item_stack);
                                    xp_reward = cooked_item_stack.getExperienceReward();

                                    while(xp_reward > 0) {
                                        xp_share = EntityExperienceOrb.getXPSplit(xp_reward);
                                        xp_reward -= xp_share;
                                        this.worldObj.spawnEntityInWorld(new EntityExperienceOrb(this.worldObj, this.posX, this.posY + 0.5, this.posZ + 0.5, xp_share));
                                    }
                                }

                                return result.setEntityWasAffected();
                            }
                        }

                        result.startTrackingHealth((float)this.health);
                        this.health = (int)((float)this.health - damage.getAmount());
                        result.finishTrackingHealth((float)this.health);
                    }

                    if (result.entityWasNegativelyAffected() && (damage.isPepsinDamage() || damage.isAcidDamage())) {
                        if (this.health <= 0) {
                            this.entityFX(damage.isAcidDamage() ? EnumEntityFX.smoke_and_steam_with_hiss : EnumEntityFX.steam_with_hiss);
                        } else {
                            this.entityFX(EnumEntityFX.item_vanish);
                        }
                    }

                    if (this.health <= 0) {
                        if (damage.isFireDamage()) {
                            this.entityFX(EnumEntityFX.smoke);
                        }

                        if (!this.getEntityItem().hasSignature() && this.getEntityItem().getItem().hasContainerItem()) {
                            Item container = this.getEntityItem().getItem().getContainerItem();
                            if (!container.isHarmedBy(damage.getSource())) {
                                this.convertItem(container);
                                return result;
                            }
                        }

                        this.setDead();
                        if (item_stack.hasSignatureThatHasBeenAddedToWorld(this.worldObj)) {
                            this.tryRemoveFromWorldUniques();
                        }

                        result.setEntityWasDestroyed();
                    }

                    return result;
                }
            }
        } else {
            return result;
        }
    }
    @Shadow
    protected void entityInit() {
    }
    @Shadow
    public ItemStack getEntityItem() {
        return null;
    }
    @Shadow
    private boolean destroyItem(DamageSource damage_source) {
        return false;
    }
    @Shadow
    public int age;
    @Shadow
    public int delayBeforeCanPickup;
    @Shadow
    private int health;
    @Shadow
    public float hoverStart;
    @Shadow
    public boolean dropped_by_player;
    @Shadow
    private float cooking_progress;

    @Shadow
    public void setEntityItemStack(ItemStack par1ItemStack) {
    }
    @Shadow
    public void tryRemoveFromWorldUniques() {
    }
    @Shadow
    public void convertItem(Item item) {
    }

    @Shadow
    protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {

    }

    @Shadow
    protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {

    }
}