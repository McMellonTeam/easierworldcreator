package fr.rodofire.ewc.particledata.layer;

import net.minecraft.client.particle.Particle;

import java.util.List;

/**
 * Not implemented yet in the structures
 * <p>
 * Represents a layer in a structure.
 * Each layer consists of a list of {@link Particle} objects and an integer.
 * The list contains all the {@link Particle} objects present in the layer,
 * while the integer represents the depth of the layer.
 * <p>
 * Be cautious with the depth parameter:
 * The depth should never be less than 0.
 * There is no benefit to having a depth equal to 0.
 */
@SuppressWarnings("unused")
public class ParticleLayer {

    private List<Particle> particles;
    private int depth = 1;

    /**
     * init the ParticleLayer
     *
     * @param particles list of Particles
     * @param depth     depth of the Particles
     */
    public ParticleLayer(List<Particle> particles, int depth) {
        this.particles = particles;
        this.depth = depth;
    }

    /**
     * init the ParticleLayer
     *
     * @param particles list of Particles
     */
    public ParticleLayer(List<Particle> particles) {
        this.particles = particles;
    }

    /**
     * init the ParticleLayer
     *
     * @param particle if the layer is only composed of one Particle, you don't necessary need to create a list (created automatically)
     * @param depth    list of Particles
     */
    public ParticleLayer(Particle particle, int depth) {
        this.particles = List.of(particle);
        this.depth = depth;
    }

    /**
     * init the ParticleLayer
     *
     * @param particle if the layer is only composed of one Particle, you don't necessary need to create a list (created automatically)
     */
    public ParticleLayer(Particle particle) {
        this.particles = List.of(particle);
    }

    /**
     * @return the depth of the layer
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param depth int to change the layer depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @param depth int added to the layer depth
     */
    public void addDepth(int depth) {
        this.depth += depth;
    }

    /**
     * @return the Particles list of the layer
     */
    public List<Particle> getParticles() {
        return particles;
    }

    /**
     * @param blocks change the Particles of a layer
     */
    public void setParticles(List<Particle> blocks) {
        this.particles = blocks;
    }

    /**
     * add a Particle to the layer
     *
     * @param particle Particle to be added
     */
    public void addParticle(Particle particle) {
        this.particles.add(particle);
    }

    /**
     * add multiple Particles to the layer
     *
     * @param particles List of Particle to be added
     */
    public void addParticles(List<Particle> particles) {
        this.particles.addAll(particles);
    }

    /**
     * removes some Particles of the Layer
     *
     * @param particle list of Particles that will be removed
     */
    public void removeParticle(List<Particle> particle) {
        this.particles.removeAll(particle);
    }

    /**
     * removes a Particle of the Layer
     *
     * @param particle Particle that will be removed
     */
    public void removeParticle(Particle particle) {
        this.particles.remove(particle);
    }

    /**
     * @param index remove the Particle at the index
     */
    public void removeParticle(int index) {
        this.particles.remove(index);
    }

    /**
     * @return the size of the Particles
     */
    public int size() {
        return particles.size();
    }
}
