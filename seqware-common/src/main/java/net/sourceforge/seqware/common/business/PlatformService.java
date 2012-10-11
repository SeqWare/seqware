package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.PlatformDAO;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Registration;

public interface PlatformService {
  public static final String NAME = "PlatformService";

  public void setPlatformDAO(PlatformDAO platformDAO);

  public List<Platform> list();
  
  public List<Platform> list(Registration registration);

  public Platform findByID(Integer id);

  Platform updateDetached(Platform platform);

}
