package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;

public interface IUSAttributeDAO {

  public abstract void insert(IUSAttribute IUSAttribute);

  public abstract void update(IUSAttribute IUSAttribute);

  public abstract void delete(IUSAttribute IUSAttribute);

  @SuppressWarnings("unchecked")
  public abstract List<IUSAttribute> findAll(IUS ius);
  public List<IUSAttribute> list();
  

}