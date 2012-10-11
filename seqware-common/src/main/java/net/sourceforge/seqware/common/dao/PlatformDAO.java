package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Registration;

public interface PlatformDAO {
  public List<Platform> list(Registration registration);

  public Platform findByID(Integer id);

  public Platform updateDetached(Platform platform);
  
  public List<Platform> list();
}
