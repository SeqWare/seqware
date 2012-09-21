package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.LaneDAO;
import net.sourceforge.seqware.common.dao.SequencerRunDAO;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;
import net.sourceforge.seqware.common.util.Log;

public class SequencerRunServiceImpl implements SequencerRunService {
  private SequencerRunDAO sequencerRunDAO = null;
  private LaneDAO laneDAO = null;
  private FileDAO fileDAO = null;

  public SequencerRunServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * SequencerRunDAO. This method is called by the Spring framework at run time.
   * 
   * @param sequencerRunDAO
   *          implementation of SequencerRunDAO
   * @see SequencerRunDAO
   */
  public void setSequencerRunDAO(SequencerRunDAO sequencerRunDAO) {
    this.sequencerRunDAO = sequencerRunDAO;
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * LaneDAO. This method is called by the Spring framework at run time.
   * 
   * @param laneDAO
   *          implementation of LaneDAO
   * @see LaneDAO
   */
  public void setLaneDAO(LaneDAO laneDAO) {
    this.laneDAO = laneDAO;
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * FileDAO. This method is called by the Spring framework at run time.
   * 
   * @param fileDAO
   *          implementation of FileDAO
   * @see FileDAO
   */
  public void setFileDAO(FileDAO fileDAO) {
    this.fileDAO = fileDAO;
  }

  /**
   * Inserts an instance of SequencerRun into the database.
   * 
   * @param sequencerRunDAO
   *          instance of SequencerRunDAO
   */
  public void insert(SequencerRun sequencerRun) {
    /*
     * //FIXME: need to set the names of each lane!! TreeSet <Lane> list = new
     * TreeSet<Lane>(); //FIXME: why do I have to manually set these!?!?!
     * sequencerRun.getLane1().setCreateTimestamp(new Date());
     * sequencerRun.getLane2().setCreateTimestamp(new Date());
     * sequencerRun.getLane3().setCreateTimestamp(new Date());
     * sequencerRun.getLane4().setCreateTimestamp(new Date());
     * sequencerRun.getLane5().setCreateTimestamp(new Date());
     * sequencerRun.getLane6().setCreateTimestamp(new Date());
     * sequencerRun.getLane7().setCreateTimestamp(new Date());
     * sequencerRun.getLane8().setCreateTimestamp(new Date());
     * sequencerRun.getLane1().setSequencerRun(sequencerRun);
     * sequencerRun.getLane2().setSequencerRun(sequencerRun);
     * sequencerRun.getLane3().setSequencerRun(sequencerRun);
     * sequencerRun.getLane4().setSequencerRun(sequencerRun);
     * sequencerRun.getLane5().setSequencerRun(sequencerRun);
     * sequencerRun.getLane6().setSequencerRun(sequencerRun);
     * sequencerRun.getLane7().setSequencerRun(sequencerRun);
     * sequencerRun.getLane8().setSequencerRun(sequencerRun);
     * list.add(sequencerRun.getLane1()); list.add(sequencerRun.getLane2());
     * list.add(sequencerRun.getLane3()); list.add(sequencerRun.getLane4());
     * list.add(sequencerRun.getLane5()); list.add(sequencerRun.getLane6());
     * list.add(sequencerRun.getLane7()); list.add(sequencerRun.getLane8());
     * sequencerRun.setLanes(list);
     */
    sequencerRun.setCreateTimestamp(new Date());

    if (sequencerRun.getReadyToProcess() != null && sequencerRun.getReadyToProcess().equals("Y")) {
      sequencerRun.setStatus("ready_to_process");
    } else {
      sequencerRun.setStatus("not_ready_to_process");
    }
    sequencerRunDAO.insert(sequencerRun);
  }

  /**
   * This is being used to create a new SequencerRun object and the correct
   * number of assocated Lanes
   * 
   * @param sequencerRunDAO
   *          instance of SequencerRunDAO
   */
  public void insert(SequencerRunWizardDTO sequencerRun) {

    Log.stderr("Counts: " + sequencerRun.getLaneCount());
    TreeSet<Lane> list = new TreeSet<Lane>();
    for (int i = 1; i <= sequencerRun.getLaneCount(); i++) {
      Log.stderr("HERE: " + i);
      Lane lane = new Lane();
      // need to fill out the name field since that is used to distinguish in
      // the treeset
      // which doesn't allow for duplicates!
      if (lane.getName() == null || "".equals(lane.getName())) {
        // do this if the LIMS hasn't already filled this in
        lane.setName(sequencerRun.getName() + "_lane_" + i);
        lane.setLaneIndex(i - 1);
      }
      lane.setCreateTimestamp(new Date());
      lane.setSequencerRun(sequencerRun);
      laneDAO.insert(lane);
      list.add(lane);
    }
    sequencerRun.setLanes(list);
    Log.stderr("Lanes count: " + list.size());
    /*
     * //FIXME: need to set the names of each lane!! TreeSet <Lane> list = new
     * TreeSet<Lane>(); //FIXME: why do I have to manually set these!?!?!
     * sequencerRun.getLane1().setCreateTimestamp(new Date());
     * sequencerRun.getLane2().setCreateTimestamp(new Date());
     * sequencerRun.getLane3().setCreateTimestamp(new Date());
     * sequencerRun.getLane4().setCreateTimestamp(new Date());
     * sequencerRun.getLane5().setCreateTimestamp(new Date());
     * sequencerRun.getLane6().setCreateTimestamp(new Date());
     * sequencerRun.getLane7().setCreateTimestamp(new Date());
     * sequencerRun.getLane8().setCreateTimestamp(new Date());
     * sequencerRun.getLane1().setSequencerRun(sequencerRun);
     * sequencerRun.getLane2().setSequencerRun(sequencerRun);
     * sequencerRun.getLane3().setSequencerRun(sequencerRun);
     * sequencerRun.getLane4().setSequencerRun(sequencerRun);
     * sequencerRun.getLane5().setSequencerRun(sequencerRun);
     * sequencerRun.getLane6().setSequencerRun(sequencerRun);
     * sequencerRun.getLane7().setSequencerRun(sequencerRun);
     * sequencerRun.getLane8().setSequencerRun(sequencerRun);
     * list.add(sequencerRun.getLane1()); list.add(sequencerRun.getLane2());
     * list.add(sequencerRun.getLane3()); list.add(sequencerRun.getLane4());
     * list.add(sequencerRun.getLane5()); list.add(sequencerRun.getLane6());
     * list.add(sequencerRun.getLane7()); list.add(sequencerRun.getLane8());
     * sequencerRun.setLanes(list);
     */
    sequencerRun.setCreateTimestamp(new Date());

    if (sequencerRun.getReadyToProcess() != null && sequencerRun.getReadyToProcess().equals("Y")) {
      sequencerRun.setStatus("ready_to_process");
    } else {
      sequencerRun.setStatus("not_ready_to_process");
    }
    sequencerRunDAO.insert(sequencerRun);
  }

  /**
   * Updates an instance of SequencerRun in the database.
   * 
   * @param sequencerRun
   *          instance of SequencerRun
   */
  public void update(SequencerRun sequencerRun) {

    if (sequencerRun.getReadyToProcess() != null && sequencerRun.getReadyToProcess().equals("Y")) {
      sequencerRun.setStatus("ready_to_process");
    } else {
      sequencerRun.setStatus("not_ready_to_process");
    }

    sequencerRunDAO.update(sequencerRun);
  }

  /**
   * Deletes an instance of SequencerRun in the database.
   * 
   * @param sequencerRun
   *          instance of SequencerRun
   */
  public void delete(SequencerRun sequencerRun) {
    // get lanes
    SortedSet<Lane> lanes = sequencerRun.getLanes();

    // delete lane, ius, ius_attribute, ius_link
    laneDAO.delete(lanes);

    // delete Sequence Run
    sequencerRunDAO.delete(sequencerRun);
  }

  @Override
  public List<SequencerRun> setProcCountInfo(List<SequencerRun> list) {
    for (SequencerRun sequencerRun : list) {

      // FIX ME: What faster? One sql query or Java code...

      // List<Integer> statuses = sequencerRunDAO.getProcStatuses(sequencerRun);
      // sequencerRun.setProcessedCount(statuses.get(0));
      // sequencerRun.setProcessingCount(statuses.get(1));
      // sequencerRun.setErrorCount(statuses.get(2));

      sequencerRun.setProcessedCount(sequencerRun.getProcessedCnt());
      sequencerRun.setProcessingCount(sequencerRun.getProcessingCount());
      sequencerRun.setErrorCount(sequencerRun.getErrorCnt());
    }
    return list;
  }

  public List<SequencerRun> list(Registration registration) {
    // get list and delete dublicate
    // Set<SequencerRun> tempList = new
    // TreeSet<SequencerRun>(sequencerRunDAO.list(registration, true));
    // return new ArrayList<SequencerRun>(tempList);
    return sequencerRunDAO.list(registration, true);
  }

  public List<SequencerRun> list(Registration registration, Boolean isAsc) {
    // get list and delete dublicate
    // Set<SequencerRun> tempList = new
    // TreeSet<SequencerRun>(sequencerRunDAO.list(registration, isAsc));
    // return new ArrayList<SequencerRun>(tempList);
    return sequencerRunDAO.list(registration, isAsc);
  }

  /**
   * Finds an instance of SequencerRun in the database by the SequencerRun
   * emailAddress, and copies the SequencerRun properties to an instance of
   * SequencerRun.
   * 
   * @return instance of SequencerRun, or null if a SequencerRun cannot be found
   */
  public SequencerRun findByName(String name) {
    SequencerRun sequencerRun = null;
    if (name != null) {
      try {
        sequencerRun = sequencerRunDAO.findByName(name.trim());
      } catch (Exception exception) {
        Log.debug("Cannot find SequencerRun by name " + name);
      }
    }
    return sequencerRun;
  }

  public SequencerRun findByID(Integer expID) {
    SequencerRun sequencerRun = null;
    if (expID != null) {
      try {
        sequencerRun = sequencerRunDAO.findByID(expID);
        // fillInLanes(sequencerRun);
      } catch (Exception exception) {
        Log.error("Cannot find SequencerRun by expID " + expID);
        Log.error(exception.getMessage());
      }
    }
    return sequencerRun;
  }

  @Override
  public SequencerRun findBySWAccession(Integer swAccession) {
    SequencerRun sequencerRun = null;
    if (swAccession != null) {
      try {
        sequencerRun = sequencerRunDAO.findBySWAccession(swAccession);
      } catch (Exception exception) {
        Log.error("Cannot find SequencerRun by swAccession " + swAccession);
        Log.error(exception.getMessage());
      }
    }
    return sequencerRun;
  }

  @Override
  public List<SequencerRun> findByOwnerID(Integer registrationId) {
    List<SequencerRun> sequencerRuns = null;
    if (registrationId != null) {
      try {
        sequencerRuns = sequencerRunDAO.findByOwnerID(registrationId);
      } catch (Exception exception) {
        Log.error("Cannot find SequencerRun by registrationId " + registrationId);
        Log.error(exception.getMessage());
      }
    }
    return sequencerRuns;
  }

  /**
   * Determines if an email address has already been used.
   * 
   * @param oldEmail
   *          The previous email address, or null if this method is being called
   *          for a new email address
   * 
   * @param newEmail
   *          The email address that is being checked
   * 
   * @return true if the newEmail has already been used, and false otherwise
   */
  public boolean hasNameBeenUsed(String oldName, String newName) {
    boolean nameUsed = false;
    boolean checkName = true;

    if (newName != null) {
      if (oldName != null) {
        /*
         * We do not want to check if an name address has been used if the user
         * is updating an existing sequencer run and has not changed the
         * nameAddress.
         */
        checkName = !newName.trim().equalsIgnoreCase(oldName.trim());
      }

      if (checkName) {
        SequencerRun sequencerRun = this.findByName(newName.trim());
        if (sequencerRun != null) {
          nameUsed = true;
        }
      }
    }
    return nameUsed;
  }

  @Override
  public List<SequencerRun> findByCriteria(String criteria, boolean isCaseSens) {
    return sequencerRunDAO.findByCriteria(criteria, isCaseSens);
  }

  @Override
  public SequencerRun updateDetached(SequencerRun sequencerRun) {
    return sequencerRunDAO.updateDetached(sequencerRun);
  }

  /*
   * private void fillInLanes(SequencerRun sequencerRun) { Object[] lanes =
   * sequencerRun.getLanes().toArray(); Debug.put(": Array Length is " +
   * sequencerRun.getLanes().size()); if (lanes.length >= 8) {
   * sequencerRun.setLane1((Lane)lanes[7]);
   * sequencerRun.setLane2((Lane)lanes[6]);
   * sequencerRun.setLane3((Lane)lanes[5]);
   * sequencerRun.setLane4((Lane)lanes[4]);
   * sequencerRun.setLane5((Lane)lanes[3]);
   * sequencerRun.setLane6((Lane)lanes[2]);
   * sequencerRun.setLane7((Lane)lanes[1]);
   * sequencerRun.setLane8((Lane)lanes[0]); } }
   */

    @Override
    public List<SequencerRun> list() {
        return sequencerRunDAO.list();
    }

    @Override
    public void update(Registration registration, SequencerRun sequencerRun) {
        sequencerRunDAO.update(registration, sequencerRun);
    }

    @Override
    public void insert(Registration registration, SequencerRun sequencerRun) {
        sequencerRun.setCreateTimestamp(new Date());
        sequencerRunDAO.insert(registration, sequencerRun);
    }

    @Override
    public void insert(Registration registration, SequencerRunWizardDTO sequencerRun) {
        sequencerRun.setCreateTimestamp(new Date());
        sequencerRunDAO.insert(registration, sequencerRun);
    }

    @Override
    public SequencerRun updateDetached(Registration registration, SequencerRun sequencerRun) {
        return sequencerRunDAO.updateDetached(registration, sequencerRun);
    }
}

// ex:sw=4:ts=4:
