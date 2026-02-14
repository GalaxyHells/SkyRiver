package com.galaxyhells.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EndermanGlowMixin {

    /**
     * Injeta no método isGlowing() da classe Entity.
     * Se a entidade for o Mutante, forçamos o retorno como 'true'.
     */
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;

        // Verifica se o nome da entidade contém "Mutante"
        // Usamos o check de null para evitar crashes em entidades sem nome
        if (entity.getCustomName() != null && entity.getCustomName().getString().contains("Mutante")) {
            cir.setReturnValue(true);
        }

        // Se você quiser que Endermans normais também brilhem, pode adicionar:
        // else if (entity instanceof EndermanEntity) { cir.setReturnValue(true); }
    }
}