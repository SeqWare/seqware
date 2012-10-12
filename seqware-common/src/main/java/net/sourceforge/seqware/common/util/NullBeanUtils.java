package net.sourceforge.seqware.common.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * <p>NullBeanUtils class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class NullBeanUtils extends BeanUtilsBean {

  /** {@inheritDoc} */
  @Override
  public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException,
      InvocationTargetException {
    if (value == null)
      return;
    super.copyProperty(dest, name, value);
  }

}
