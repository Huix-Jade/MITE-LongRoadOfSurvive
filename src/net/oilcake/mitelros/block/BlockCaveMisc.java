package net.oilcake.mitelros.block;

import net.minecraft.*;
import net.oilcake.mitelros.enchantment.Enchantments;
import net.oilcake.mitelros.item.Items;

public class BlockCaveMisc extends Block {
    protected BlockCaveMisc(int id, Material material) {
        this(id, material, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
    }
    protected BlockCaveMisc(int id, Material material, BlockConstants constants) {
        super(id, material, constants);
        this.setTickRandomly(true);
        float var3 = 0.2F;
        this.setBlockBoundsForAllThreads((double)(0.5F - var3), 0.0, (double)(0.5F - var3), (double)(0.5F + var3), (double)(var3 * 3.0F), (double)(0.5F + var3));
        this.setMaxStackSize(32);
        this.setCreativeTab(CreativeModeTab.tabDecorations);
    }
    public int getPatchSize(BiomeBase biome) {
        return 64;
    }

    public boolean isSolid(boolean[] is_solid, int metadata) {
        return false;
    }

    public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
        return false;
    }
    public boolean onNotLegal(World world, int x, int y, int z, int metadata) {
        if (world.getClosestPlayer((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), 16.0, false) == null) {
            world.setBlockToAir(x, y, z);
            return true;
        } else {
            return super.onNotLegal(world, x, y, z, metadata);
        }
    }

    public int getRenderType() {
        return 1;
    }
    public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
        return super.isLegalAt(world, x, y, z, metadata);
    }

    public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
        return block_below == dirt || block_below == stone || block_below == Blocks.blockAzurite;
    }
    public int dropBlockAsEntityItem(BlockBreakInfo info) {
        int metadata_dropped = 0;
        int quantity_dropped = 1;
        int id_dropped = 0;
        if (info.wasExploded()) {
            id_dropped = -1;
        }
        else {
            boolean HasAbsorb = EnchantmentManager.hasEnchantment(info.responsible_item_stack, Enchantments.enchantmentAbsorb);
            if (this == Blocks.azuriteCluster) {
                id_dropped = HasAbsorb ? 0 : Items.shardAzurite.itemID;
            } else {
                id_dropped = this.blockID;
            }
        }
        boolean suppress_fortune = id_dropped == this.blockID && BitHelper.isBitSet(info.getMetadata(), 1);
        float chance = suppress_fortune ? 0.5F : 0.5F + (float) info.getHarvesterFortune() * 0.1F;
        if (EnchantmentManager.hasEnchantment(info.responsible_item_stack, Enchantments.enchantmentAbsorb)) {
            if (this == Blocks.azuriteCluster) {
                this.dropXpOnBlockBreak(info.world, info.x, info.y, info.z, (int)(5*chance));
            }
        }
        return super.dropBlockAsEntityItem(info, id_dropped, metadata_dropped, quantity_dropped, chance);
    }
}
