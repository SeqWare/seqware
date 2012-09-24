package	net.sourceforge.seqware.common.dao;
import java.util.List;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;

public interface ExperimentSpotDesignDAO {
	public void					insert(ExperimentSpotDesign obj);
	public void					update(ExperimentSpotDesign obj);
	public ExperimentSpotDesign		findByID(Integer id);
  public ExperimentSpotDesign updateDetached(ExperimentSpotDesign experiment);
  public List<ExperimentSpotDesign> list();
}
