package fr.rodofire.ewc.mixin.world.gen;

import fr.rodofire.ewc.world.chunk.ChunkRegionUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Locale;

@Mixin(WorldGenRegion.class)
public class ChunkRegionMixin implements ChunkRegionUtil {

    @Final
    @Shadow
    private ChunkAccess center;

    @Final
    @Shadow
    private ChunkStep generatingStep;
    @Final
    @Shadow
    private StaticCache2D<GenerationChunkHolder> cache;


    @Override
    public ChunkAccess ewc_main$getNullableChunk(int chunkX, int chunkZ, ChunkStatus chunkStatus, boolean create) {
        int i = this.center.getPos().getChessboardDistance(chunkX, chunkZ);
        ChunkStatus chunkstatus = i >= this.generatingStep.directDependencies().size() ? null : this.generatingStep.directDependencies().get(i);
        GenerationChunkHolder generationchunkholder;
        if (chunkstatus != null) {
            generationchunkholder = this.cache.get(chunkX, chunkZ);
            if (chunkStatus.isOrBefore(chunkstatus)) {
                ChunkAccess chunkaccess = generationchunkholder.getChunkIfPresentUnchecked(chunkstatus);
                if (chunkaccess != null) {
                    return chunkaccess;
                }
            }
        } else {
            generationchunkholder = null;
        }

        if (!create) {
            return null;
        }

        CrashReport crashreport = CrashReport.forThrowable(
                new IllegalStateException("Requested chunk unavailable during world generation"), "Exception generating new chunk"
        );
        CrashReportCategory crashreportcategory = crashreport.addCategory("Chunk request details");
        crashreportcategory.setDetail("Requested chunk", String.format(Locale.ROOT, "%d, %d", chunkX, chunkZ));
        crashreportcategory.setDetail("Generating status", () -> this.generatingStep.targetStatus().getName());
        crashreportcategory.setDetail("Requested status", chunkStatus::getName);
        crashreportcategory.setDetail(
                "Actual status", () -> generationchunkholder == null ? "[out of cache bounds]" : generationchunkholder.getPersistedStatus().getName()
        );
        crashreportcategory.setDetail("Maximum allowed status", () -> chunkstatus == null ? "null" : chunkstatus.getName());
        crashreportcategory.setDetail("Dependencies", this.generatingStep.directDependencies()::toString);
        crashreportcategory.setDetail("Requested distance", i);
        crashreportcategory.setDetail("Generating chunk", this.center.getPos()::toString);
        throw new ReportedException(crashreport);
    }


}
