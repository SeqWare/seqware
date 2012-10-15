package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignReadSpecDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;

/**
 * <p>ExperimentSpotDesignReadSpecService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentSpotDesignReadSpecService {
  /** Constant <code>NAME="ExperimentSpotDesignReadSpecService"</code> */
  public static final String NAME = "ExperimentSpotDesignReadSpecService";

  /**
   * <p>setExperimentSpotDesignReadSpecDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ExperimentSpotDesignReadSpecDAO} object.
   */
  public void setExperimentSpotDesignReadSpecDAO(ExperimentSpotDesignReadSpecDAO dao);

  /**
   * <p>insert.</p>
   *
   * @param obj a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   */
  public void insert(ExperimentSpotDesignReadSpec obj);

  /**
   * <p>update.</p>
   *
   * @param obj a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   */
  public void update(ExperimentSpotDesignReadSpec obj);

  /**
   * <p>delete.</p>
   *
   * @param obj a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   */
  public void delete(ExperimentSpotDesignReadSpec obj);

  /**
   * <p>findByID.</p>
   *
   * @param expID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   */
  public ExperimentSpotDesignReadSpec findByID(Integer expID);

  /**
   * <p>updateDetached.</p>
   *
   * @param experiment a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   */
  ExperimentSpotDesignReadSpec updateDetached(ExperimentSpotDesignReadSpec experiment);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ExperimentSpotDesignReadSpec> list();
}

// ex:sw=4:ts=4:
