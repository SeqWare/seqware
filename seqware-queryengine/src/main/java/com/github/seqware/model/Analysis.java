package com.github.seqware.model;

/**
 * An Analysis object represents analysis components, most of which will be
 * implemented as an analysis plugin on the backend. An example would be a 
 * coding consequence plugin. 
 *
 * @author dyuen
 */
public abstract class Analysis extends Particle {

    /**
     * Create a new reference
     */
    public Analysis() {
        super();
    }
}
