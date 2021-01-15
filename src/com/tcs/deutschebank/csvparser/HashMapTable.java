package com.tcs.deutschebank.csvparser;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.Map;

public class HashMapTable extends AbstractTableModel {
  Map<String, Map<String, Integer>> timeData = null;
  HashMapTable(Map<String, Map<String, Integer>> timeData){
    this.timeData = timeData;
  }

  @Override
  public int getRowCount() {
    return 0;
  }

  @Override
  public int getColumnCount() {
    return 0;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return null;
  }

  public void setRowCount(int cnt){

  }
}
