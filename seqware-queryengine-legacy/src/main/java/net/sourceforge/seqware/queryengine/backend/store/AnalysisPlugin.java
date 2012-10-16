/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.queryengine.backend.store;

/**
 *
 * @author boconnor
 * 
 * It would be awesome if we could suppor the GATK walker (at least loci walker) interface, see 
 * http://www.broadinstitute.org/gsa/wiki/index.php/Your_first_walker
 * 
 * 
 */
interface AnalysisPlugin {
  
     // need to think about settings, logger, and other util objects

    /**
     * <p>init.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue init();

    /**
     * <p>test.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue test();

    /**
     * <p>verifyParameters.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue verifyParameters();

    /**
     * <p>verifyInput.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue verifyInput();

    /**
     * <p>filterInit.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue filterInit();

    /**
     * <p>filter.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue filter();

    /**
     * <p>mapInit.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue mapInit();

    /**
     * <p>map.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue map();

    /**
     * <p>reduceInit.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue reduceInit();

    /**
     * <p>reduce.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue reduce();

    /**
     * <p>verifyOutput.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue verifyOutput();

    /**
     * <p>cleanup.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.ReturnValue} object.
     */
    public ReturnValue cleanup();
  
}
