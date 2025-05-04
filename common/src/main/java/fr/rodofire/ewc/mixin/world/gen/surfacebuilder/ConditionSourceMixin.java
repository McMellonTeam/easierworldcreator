package fr.rodofire.ewc.mixin.world.gen.surfacebuilder;

import com.mojang.serialization.MapCodec;
import fr.rodofire.ewc.world.gen.surfacebuilder.ExtendedSurfaceRules;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SurfaceRules.ConditionSource.class)
public interface ConditionSourceMixin {
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void addCustomConditions(Registry<MapCodec<? extends SurfaceRules.ConditionSource>> registry, CallbackInfoReturnable<MapCodec<? extends SurfaceRules.ConditionSource>> cir) {
        SurfaceRulesMixin.register(registry, "full_noise_threshold", ExtendedSurfaceRules.FullNoiseThresholdConditionSource.CODEC);
        SurfaceRulesMixin.register(registry, "random", ExtendedSurfaceRules.RandomConditionSource.CODEC);
    }
}
