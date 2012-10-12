package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSLink;

/**
 * <p>IUSLinkDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface IUSLinkDAO {

  /**
   * <p>insert.</p>
   *
   * @param IUSLink a {@link net.sourceforge.seqware.common.model.IUSLink} object.
   */
  public abstract void insert(IUSLink IUSLink);

  /**
   * <p>update.</p>
   *
   * @param IUSLink a {@link net.sourceforge.seqware.common.model.IUSLink} object.
   */
  public abstract void update(IUSLink IUSLink);

  /**
   * <p>delete.</p>
   *
   * @param IUSLink a {@link net.sourceforge.seqware.common.model.IUSLink} object.
   */
  public abstract void delete(IUSLink IUSLink);

  /**
   * <p>findAll.</p>
   *
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<IUSLink> findAll(IUS ius);

}
