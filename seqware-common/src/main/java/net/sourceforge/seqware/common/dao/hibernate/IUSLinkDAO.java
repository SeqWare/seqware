package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSLink;

public interface IUSLinkDAO {

  public abstract void insert(IUSLink IUSLink);

  public abstract void update(IUSLink IUSLink);

  public abstract void delete(IUSLink IUSLink);

  @SuppressWarnings("unchecked")
  public abstract List<IUSLink> findAll(IUS ius);

}