package	net.sourceforge.seqware.common.dao;
import java.util.List;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;

/**
 * <p>ExperimentSpotDesignDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentSpotDesignDAO {
	/**
	 * <p>insert.</p>
	 *
	 * @param obj a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
	 */
	public void					insert(ExperimentSpotDesign obj);
	/**
	 * <p>update.</p>
	 *
	 * @param obj a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
	 */
	public void					update(ExperimentSpotDesign obj);
	/**
	 * <p>findByID.</p>
	 *
	 * @param id a {@link java.lang.Integer} object.
	 * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
	 */
	public ExperimentSpotDesign		findByID(Integer id);
  /**
   * <p>updateDetached.</p>
   *
   * @param experiment a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
   * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
   */
  public ExperimentSpotDesign updateDetached(ExperimentSpotDesign experiment);
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ExperimentSpotDesign> list();
}
