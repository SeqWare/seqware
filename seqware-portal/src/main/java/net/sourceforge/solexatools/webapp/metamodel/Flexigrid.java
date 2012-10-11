package net.sourceforge.solexatools.webapp.metamodel;

import java.util.ArrayList;
import java.util.List;

public class Flexigrid {

  private int total;
  private int page;
  private List<Cells> rows;

  public Flexigrid(int total, int page) {
    this.total = total;
    this.page = page;
    rows = new ArrayList<Flexigrid.Cells>();
  }

  public Flexigrid(int total) {
    this.total = total;
    rows = new ArrayList<Flexigrid.Cells>();
  }

  public Flexigrid() {
    rows = new ArrayList<Flexigrid.Cells>();
  }

  public void addRow(Cells cells) {
    this.rows.add(cells);
    total = rows.size();
  }

  public List<Cells> getRows() {
    return rows;
  }

  public void setRows(List<Cells> rows) {
    this.rows = rows;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getTotal() {
    return total;
  }

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
