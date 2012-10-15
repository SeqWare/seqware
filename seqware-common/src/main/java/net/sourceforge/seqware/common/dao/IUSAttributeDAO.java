package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;

/**
 * <p>IUSAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface IUSAttributeDAO {

  /**
   * <p>insert.</p>
   *
   * @param IUSAttribute a {@link net.sourceforge.seqware.common.model.IUSAttribute} object.
   */
  public abstract void insert(IUSAttribute IUSAttribute);

  /**
   * <p>update.</p>
   *
   * @param IUSAttribute a {@link net.sourceforge.seqware.common.model.IUSAttribute} object.
   */
  public abstract void update(IUSAttribute IUSAttribute);

  /**
   * <p>delete.</p>
   *
   * @param IUSAttribute a {@link net.sourceforge.seqware.common.model.IUSAttribute} object.
   */
  public abstract void delete(IUSAttribute IUSAttribute);

  /**
   * <p>findAll.</p>
   *
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<IUSAttribute> findAll(IUS ius);
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<IUSAttribute> list();
  

}
