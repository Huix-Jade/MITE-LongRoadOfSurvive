package net.oilcake.mitelros.client.render;

import net.minecraft.*;

public class RenderUnknown extends bgu{
    private bbj r;
    public RenderUnknown() {
        super(new bcm(), 0.5F, 1.0F);
        this.r = this.a;
    }

    @Override
    protected void setTextures() {
        this.setTexture(0, "textures/entity/steve");
    }
    protected boolean forceGlowOverride(){
        return true;
    }
    protected bjo a(EntityInsentient par1EntityLiving) {
        return this.textures[0];
    }
    protected bjo a(Entity par1Entity) {
        return this.a((EntityZombie)par1Entity);
    }
}
