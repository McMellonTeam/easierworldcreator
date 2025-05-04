package fr.rodofire.ewc.mixin.world.structure;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplateManager.class)
public interface StructureTemplateManagerInvoker {
    @Accessor("sources")
    List<StructureTemplateManager.Source> getSources();
}
