package	net.sourceforge.seqware.common.dao;
import java.util.List;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;

public interface ExperimentSpotDesignReadSpecDAO {
	public void					insert(ExperimentSpotDesignReadSpec obj);
  public void         update(ExperimentSpotDesignReadSpec obj);
  public void         delete(ExperimentSpotDesignReadSpec obj);
	public ExperimentSpotDesignReadSpec		findByID(Integer id);
  public ExperimentSpotDesignReadSpec updateDetached(ExperimentSpotDesignReadSpec experiment);
  public List<ExperimentSpotDesignReadSpec> list();
}
