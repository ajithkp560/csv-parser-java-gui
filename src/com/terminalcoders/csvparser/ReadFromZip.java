package com.terminalcoders.csvparser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReadFromZip implements Runnable{
  BlockingQueue<String> bq = null;
  File zFile = null;
  MainWindow mw = null;
  char csvSeparator = ',';
  String opt, tim, flt;
  int fld, tcl;
//  List<List<String>> csvList = new ArrayList<List<String>>();
  JTable records = null;
  DefaultTableModel model = null;
  JProgressBar progressBar = null;
  Map<String, Map<String, Integer>> timeData = null;
  Map<String, Integer> tData = null;
  public ReadFromZip(BlockingQueue<String> bq, File zFile, MainWindow mw, String opt, String tim, int fld, String flt, int tcl, JTable records, JProgressBar progressBar, Map<String, Map<String, Integer>> timeData, Map<String, Integer> tData) {
    this.bq = bq;
    this.zFile = zFile;
    this.mw = mw;
    this.opt = opt;
    this.tim = tim;
    this.fld = fld;
    this.flt = flt;
    this.tcl = tcl;
    this.records = records;
    this.model  = (DefaultTableModel) this.records.getModel();
    this.progressBar = progressBar;
    this.timeData = timeData;
    this.tData = tData;
  }

  public ReadFromZip() {
  }

  void readData(){
    try {
      ZipFile zf = new ZipFile(zFile);
      Enumeration<? extends ZipEntry> entries = zf.entries();
      System.out.println(zFile.getName()+" - Number of files: "+zf.size());
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        System.out.println("Parsing started: " + entry.getName());
        InputStream stream = zf.getInputStream(entry);
        BufferedReader csvReader = null;
        String csvRecord = null;
        try {
          csvReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
          String head = csvReader.readLine();
//          System.out.println(csvReader.readLine());
          while ((csvRecord = csvReader.readLine()) != null) {
            List<String> row = parseCsvRecord(csvRecord);
            try {
              if (this.opt.equals("ROWS")) {
                if (row.get(fld).equals(flt)) {
                  model.addRow(row.toArray());
                }
              } else {
                if (tim.equals("NONE")) {
                  Integer cnt = tData.getOrDefault(row.get(fld), 0);
                  tData.put(row.get(fld), cnt + 1);
                } else {
                  String key = row.get(tcl).split(" ")[0];
                  if (tim.equals("HOURLY")) {
                    key = row.get(tcl).split(":")[0];
                  }
                  Map<String, Integer> dt = timeData.getOrDefault(key, new HashMap<String, Integer>());
                  Integer dx = dt.getOrDefault(row.get(fld), 0);
                  dt.put(row.get(fld), dx + 1);
                  timeData.put(key, dt);
//                System.out.println(key+":"+row.get(fld)+":"+(dx+1));
                }
              }
            } catch (ArrayIndexOutOfBoundsException ex) {}
          }
        } catch (IOException e) {
          JOptionPane.showMessageDialog(this.mw, e.getMessage(), "Error!!!", JOptionPane.ERROR_MESSAGE);
        } finally {
          if (csvReader != null)
            try {
              csvReader.close();
            } catch (IOException e) {
              JOptionPane.showMessageDialog(this.mw, e.getMessage(), "Error!!!", JOptionPane.ERROR_MESSAGE);
            }
        }
        System.out.println("Parsing completed: " + entry.getName());
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this.mw, e.getMessage(), "Error!!!", JOptionPane.ERROR_MESSAGE);
    }
  }

  List<String> parseCsvRecord(String record) {
    boolean quoted = false;
    StringBuilder fieldBuilder = new StringBuilder();
    List<String> fields = new ArrayList<String>();
    for (int i = 0; i < record.length(); i++) {
      char c = record.charAt(i);
      fieldBuilder.append(c);

      if (c == '"') {
        quoted = !quoted; // Detect nested quotes.
      }

      if ((!quoted && c == csvSeparator) || i + 1 == record.length()) { // .. or, the end of record.
        String field = fieldBuilder.toString() // Obtain the field, ..
          .replaceAll(csvSeparator + "$", "") // .. trim ending separator, ..
          .replaceAll("^\"|\"$", "") // .. trim surrounding quotes, ..
          .replace("\"\"", "\""); // .. and un-escape quotes.
        fields.add(field.trim()); // Add field to List.
        fieldBuilder = new StringBuilder(); // Reset.
      }
    }
    return fields;
  }

  @Override
  public void run() {
    readData();
  }
}
