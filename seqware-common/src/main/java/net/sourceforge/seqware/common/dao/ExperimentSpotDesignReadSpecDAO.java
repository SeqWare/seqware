package	net.sourceforge.seqware.common.dao;
import java.util.List;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;

/**
 * <p>ExperimentSpotDesignReadSpecDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentSpotDesignReadSpecDAO {
	/**
	 * <p>insert.</p>
	 *
	 * @param obj a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
	 */
	public void					insert(ExperimentSpotDesignReadSpec obj);
  /**
   * <p>update.</p>
   *
   * @param obj a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   */
  public void         update(ExperimentSpotDesignReadSpec obj);
  /**
   * <p>delete.</p>
   *
   * @param obj a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   */
  public void         delete(ExperimentSpotDesignReadSpec obj);
	/**
	 * <p>findByID.</p>
	 *
	 * @param id a {@link java.lang.Integer} object.
	 * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
	 */
	public ExperimentSpotDesignReadSpec		findByID(Integer id);
  /**
   * <p>updateDetached.</p>
   *
   * @param experiment a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
   */
  public ExperimentSpotDesignReadSpec updateDetached(ExperimentSpotDesignReadSpec experiment);
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ExperimentSpotDesignReadSpec> list();
}
