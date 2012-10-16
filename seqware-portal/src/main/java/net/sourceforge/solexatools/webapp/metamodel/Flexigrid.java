package net.sourceforge.solexatools.webapp.metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Flexigrid class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Flexigrid {

  private int total;
  private int page;
  private List<Cells> rows;

  /**
   * <p>Constructor for Flexigrid.</p>
   *
   * @param total a int.
   * @param page a int.
   */
  public Flexigrid(int total, int page) {
    this.total = total;
    this.page = page;
    rows = new ArrayList<Flexigrid.Cells>();
  }

  /**
   * <p>Constructor for Flexigrid.</p>
   *
   * @param total a int.
   */
  public Flexigrid(int total) {
    this.total = total;
    rows = new ArrayList<Flexigrid.Cells>();
  }

  /**
   * <p>Constructor for Flexigrid.</p>
   */
  public Flexigrid() {
    rows = new ArrayList<Flexigrid.Cells>();
  }

  /**
   * <p>addRow.</p>
   *
   * @param cells a {@link net.sourceforge.solexatools.webapp.metamodel.Flexigrid.Cells} object.
   */
  public void addRow(Cells cells) {
    this.rows.add(cells);
    total = rows.size();
  }

  /**
   * <p>Getter for the field <code>rows</code>.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Cells> getRows() {
    return rows;
  }

  /**
   * <p>Setter for the field <code>rows</code>.</p>
   *
   * @param rows a {@link java.util.List} object.
   */
  public void setRows(List<Cells> rows) {
    this.rows = rows;
  }

  /**
   * <p>Getter for the field <code>page</code>.</p>
   *
   * @return a int.
   */
  public int getPage() {
    return page;
  }

  /**
   * <p>Setter for the field <code>page</code>.</p>
   *
   * @param page a int.
   */
  public void setPage(int page) {
    this.page = page;
  }

  /**
   * <p>Getter for the field <code>total</code>.</p>
   *
   * @return a int.
   */
  public int getTotal() {
    return total;
  }

  /**
   * <p>Setter for the field <code>total</code>.</p>
   *
   * @param total a int.
   */
  public void setTotal(int total) {
    this.total = total;
  }

  public class Cells {
    private List<String> cell;

    public Cells(List<String> cells) {
      this.cell = cells;
    }

    public List<String> getCell() {
      return cell;
    }

    public void setCell(List<String> cell) {
      this.cell = cell;
    }
  }

  public static class ColumnModel {
    private String display;
    private String name;
    private int width;
    private boolean sortable;
    private String align;

    public ColumnModel(String display, String name, int width, boolean sortable, String align) {
      this.display = display;
      this.name = name;
      this.width = width;
      this.sortable = sortable;
      this.align = align;

    }

    public String getDisplay() {
      return display;
    }

    public void setDisplay(String display) {
      this.display = display;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getWidth() {
      return width;
    }

    public void setWidth(int width) {
      this.width = width;
    }

    public boolean isSortable() {
      return sortable;
    }

    public void setSortable(boolean sortable) {
      this.sortable = sortable;
    }

    public String getAlign() {
      return align;
    }

    public void setAlign(String align) {
      this.align = align;
    }
  }
}
