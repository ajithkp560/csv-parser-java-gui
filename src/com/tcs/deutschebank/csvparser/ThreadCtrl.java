package com.tcs.deutschebank.csvparser;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ThreadCtrl extends SwingWorker<Integer, String> {
  private int status;
  BlockingQueue<String> bq = null;
  File zFile[] = null;
  MainWindow mw = null;
  String opt, tim, flt, fldName;
  int fld, tcl;
  JTable records = null;
  JProgressBar progressBar = null;
  DefaultTableModel model = null;
  JPanel  pRec = null;
  MenuItem exp = null;
  public static Map<String, Map<String, Integer>> timeData = null;
  public static Map<String, Integer> tData = null;
  public ThreadCtrl(java.util.concurrent.BlockingQueue<String> bq, File zFile[], MainWindow mw, String opt, String tim, int fld, String flt, int tcl, JTable records, JProgressBar progressBar, String fldName, JPanel  pRec, MenuItem exp) {
    this.bq = bq;
    this.zFile = zFile;
    this.mw = mw;
    this.opt = opt;
    this.tim = tim;
    this.fld = fld;
    this.flt = flt;
    this.tcl = tcl;
    this.records = records;
    this.progressBar = progressBar;
    progressBar.setString("Reading Files...");
    progressBar.setIndeterminate(true);
    this.fldName = fldName;
    this.pRec = pRec;
    this.model  = (DefaultTableModel) this.records.getModel();
    this.exp = exp;
    this.exp.setEnabled(false);
    timeData = new HashMap<String, Map<String, Integer>>();
    tData = new HashMap<String, Integer>();
    mw.selFiles.setEnabled(false);
    mw.generate.setEnabled(false);
  }
  @Override
  protected Integer doInBackground() {
    try {
      ReadFromZip rfz[] = new ReadFromZip[zFile.length];
      Thread trd[] = new Thread[zFile.length];
      for (int i = 0; i < zFile.length; i++) {
        rfz[i] = new ReadFromZip(bq, zFile[i], mw, opt, tim, fld, flt, tcl, records, progressBar, timeData, tData);
//        rfz[i].readData();
        trd[i] = new Thread(rfz[i]);
        trd[i].start();
      }
      for(int i=0;i<trd.length;i++){
        trd[i].join();
      }
    } catch (Exception e){
      JOptionPane.showMessageDialog(mw, e.getMessage(), "Error!!!", JOptionPane.ERROR_MESSAGE);
    }
    return status;
  }
  @Override
  protected void done() {
    if(this.opt.equals("COUNT")){
      if(tim.equals("NONE")){
        for (Map.Entry<String, Integer> entry : tData.entrySet()) {
          List<String> lst = new ArrayList<String>();
          lst.add(entry.getKey());
          lst.add(entry.getValue().toString());
          model.addRow(lst.toArray());
        }
      } else {
        for (Map.Entry<String, Map<String, Integer>> entry : timeData.entrySet()) {
          for (Map.Entry<String, Integer> ent : entry.getValue().entrySet()) {
            List<String> lst = new ArrayList<String>();
            lst.add(entry.getKey());
            lst.add(ent.getKey());
            lst.add(ent.getValue().toString());
            model.addRow(lst.toArray());
          }
        }
      }
      System.out.println("TOT ROWS: " + model.getRowCount());
      progressBar.setString("Completed...");
      progressBar.setIndeterminate(false);
      exp.setEnabled(true);
      exp.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          exportData(model);
        }
      });
    } else {
      progressBar.setString("Completed...");
      progressBar.setIndeterminate(false);
      exp.setEnabled(true);
      exp.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
          exportData(model);
        }
      });
    }
    mw.selFiles.setEnabled(true);
    mw.generate.setEnabled(true);
  }
  void exportData(TableModel model){
    JFileChooser jfc = new JFileChooser();
    if (jfc.showSaveDialog(mw) == JFileChooser.APPROVE_OPTION) {
      try {
        if(jfc.getSelectedFile().exists()){
          int res = JOptionPane.showConfirmDialog(mw, "Do you want to replace current file?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
          if(res != JOptionPane.YES_OPTION){
            return;
          }
        }
        FileWriter fw = new FileWriter(jfc.getSelectedFile());
        for (int i = 0; i < model.getColumnCount()-1; i++) {
          fw.write("\""+model.getColumnName(i) + "\",");
        }
        fw.write("\""+model.getColumnName(model.getColumnCount()-1) + "\"\n");
        for (int i = 0; i < model.getRowCount(); i++) {
          for (int j = 0; j < model.getColumnCount()-1; j++) {
            fw.write("\""+model.getValueAt(i, j).toString() + "\",");
          }
          fw.write("\""+model.getValueAt(i, model.getColumnCount()-1).toString() + "\"\n");
        }
        fw.close();
        JOptionPane.showMessageDialog(mw, "The result is parsed to csv file.", "Success!!!", JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(mw, e.getMessage(), "ERROR!!!", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
