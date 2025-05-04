package fr.rodofire.ewc.mixin.world.gen.surfacebuilder;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("UnusedReturnValue")
@Mixin(SurfaceRules.class)
public interface SurfaceRulesMixin {
    @Invoker("register")
    static <A> MapCodec<? extends A> register(Registry<MapCodec<? extends A>> registry, String name, KeyDispatchDataCodec<? extends A> codec) {
        throw new AssertionError();
    }
}
