package com.tcs.deutschebank.csvparser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class MainWindow extends JFrame {
  JPanel menuPanel = new JPanel();
  JPanel filPanel = new JPanel();
  JPanel mPanel = new JPanel();
  GridBagLayout menuGbl = new GridBagLayout();
  GridBagConstraints menuGbc = new GridBagConstraints();
  GridBagLayout filGbl = new GridBagLayout();
  GridBagConstraints filGbc = new GridBagConstraints();
  JTextField nameOfFiles = new JTextField();
  JTextField filter = new JTextField();
  JButton selFiles = new JButton("Browse");
  JComboBox typeOf = new JComboBox();
  JComboBox fields = new JComboBox();
  JComboBox timeint = new JComboBox();
  JComboBox timecol = new JComboBox();
  JButton generate = new JButton("Generate");
  DefaultTableModel dataModel = new DefaultTableModel();
  JTable records = new JTable(dataModel);
  JPanel  pRec = new JPanel(new GridLayout());
  JProgressBar progressBar = new JProgressBar();
  JPanel pBar = new JPanel(new BorderLayout());
  File files[];
  MenuBar mb = new MenuBar();
  Menu menu = new Menu("File");
  MenuItem opn=new MenuItem("Open Zipped CSV Files");
  MenuItem exp=new MenuItem("Export Table");
  Font nFnt = new Font("Serif", Font.PLAIN, 15);
  String head[] = null;

  MainWindow(String title){
    super(title);

    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, e.getMessage(), "Error!!!", JOptionPane.ERROR_MESSAGE);
    }

    this.setLayout(new BorderLayout());

    mPanel.setLayout(new GridLayout(2, 1));
    menuPanel.setLayout(menuGbl);

    menuGbc.insets = new Insets(10, 10, 10, 10);
    menuGbc.fill = GridBagConstraints.HORIZONTAL;
    menuGbc.gridx = 0;
    menuGbc.gridy = 0;
    menuGbc.weightx = 1;
    JLabel lbl = new JLabel("Select Files: ", SwingConstants.RIGHT);
    lbl.setFont(nFnt);
    menuPanel.add(lbl, menuGbc);

    menuGbc.fill = GridBagConstraints.HORIZONTAL;
    menuGbc.gridx = 1;
    menuGbc.gridy = 0;
    menuGbc.weightx = 20;
    nameOfFiles.setEnabled(false);
    nameOfFiles.setFont(nFnt);
    menuPanel.add(nameOfFiles, menuGbc);

    menuGbc.fill = GridBagConstraints.HORIZONTAL;
    menuGbc.gridx = 2;
    menuGbc.gridy = 0;
    menuGbc.weightx = 1;
    menuPanel.add(selFiles, menuGbc);

    mPanel.add(menuPanel);


    filPanel.setLayout(filGbl);
    filGbc.insets = new Insets(10, 10, 10, 10);

    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 0;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    lbl = new JLabel("Type: ", SwingConstants.RIGHT);
    lbl.setFont(nFnt);
    filPanel.add(lbl, filGbc);

    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 1;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    filPanel.add(typeOf, filGbc);


    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 2;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    lbl = new JLabel("Interval: ", SwingConstants.RIGHT);
    lbl.setFont(nFnt);
    filPanel.add(lbl, filGbc);

    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 3;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    filPanel.add(timeint, filGbc);

    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 4;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    lbl = new JLabel("Timestamp Column: ", SwingConstants.RIGHT);
    lbl.setFont(nFnt);
    filPanel.add(lbl, filGbc);

    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 5;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    filPanel.add(timecol, filGbc);

    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 6;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    lbl = new JLabel("Field: ", SwingConstants.RIGHT);
    lbl.setFont(nFnt);
    filPanel.add(lbl, filGbc);

    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 7;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    filPanel.add(fields, filGbc);


    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 8;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    lbl = new JLabel("Filter: ", SwingConstants.RIGHT);
    lbl.setFont(nFnt);
    filPanel.add(lbl, filGbc);

    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 9;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    filter.setColumns(15);
    filter.setFont(new Font("Serif", Font.BOLD, 15));
    filPanel.add(filter, filGbc);


    filGbc.fill = GridBagConstraints.HORIZONTAL;
    filGbc.gridx = 10;
    filGbc.gridy = 0;
    filGbc.weightx = 1;
    filPanel.add(generate, filGbc);

    mPanel.add(filPanel);

    typeOf.setEnabled(false);
    timeint.setEnabled(false);
    fields.setEnabled(false);
    filter.setEnabled(false);
    generate.setEnabled(false);
    timecol.setEnabled(false);

    this.add(mPanel, BorderLayout.NORTH);

    Border blackline = BorderFactory.createLineBorder(Color.black);
    pRec.setBorder(blackline);

    this.add(pRec, BorderLayout.CENTER);

    progressBar.setSize(50, 25);
//    progressBar.setValue(50);
    progressBar.setStringPainted(true);
    progressBar.setForeground(new Color(0, 153, 0));
    progressBar.setString("Select Files");
    progressBar.setIndeterminate(false);
    pBar.add(progressBar, BorderLayout.CENTER);
    pBar.setBorder(new EmptyBorder(10, 50, 10, 50));
    this.add(pBar, BorderLayout.SOUTH);


    selFiles.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        openFiles();
      }
    });

    generate.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
//        getHeaders(files[0]);
        String opt = typeOf.getSelectedItem().toString();
        String tim = timeint.getSelectedItem().toString();
        int fld = fields.getSelectedIndex();
        String flt = filter.getText().trim();
        int tcl = timecol.getSelectedIndex();
        if(opt.equals("ROWS") && flt.trim().equals("")){
          JOptionPane.showMessageDialog(MainWindow.this, "Please enter filter parameter", "Error!!!", JOptionPane.ERROR_MESSAGE);
          return;
        }
        dataModel.setRowCount(0);

        records.setAutoCreateRowSorter(true);
        records.setEnabled(false);

        BlockingQueue<String> bq = new ArrayBlockingQueue<String>(1024);
        ThreadCtrl ctrl = new ThreadCtrl(bq, files, MainWindow.this, opt, tim, fld, flt, tcl, records, progressBar, fields.getSelectedItem().toString(), pRec, exp);
        ctrl.execute();
//        JOptionPane.showMessageDialog(null, opt+" : "+tim+" : "+fld+" : "+flt);
      }
    });
    menu.add(opn);
    menu.add(exp);
    exp.setEnabled(false);
    mb.add(menu);
    this.setMenuBar(mb);

    opn.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        openFiles();
      }
    });
  }
  void getHeaders(File file){
    try {
      ZipFile zf = new ZipFile(file);
      Enumeration<? extends ZipEntry> entries = zf.entries();
      ZipEntry entry = entries.nextElement();
      InputStream stream = zf.getInputStream(entry);
      BufferedReader csvReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));;
      java.util.List<String> cols = new ReadFromZip().parseCsvRecord(csvReader.readLine());
      initForms(cols);
    } catch (ZipException e) {
      JOptionPane.showMessageDialog(this, e.getMessage(), "Error!!!", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, e.getMessage(), "Error!!!", JOptionPane.ERROR_MESSAGE);
    }
  }

  void openFiles(){
    JFileChooser jfc = new JFileChooser();
    jfc.setMultiSelectionEnabled(true);
    String fNames = "";
    if(jfc.showOpenDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION){
      files = jfc.getSelectedFiles();
      for(int i=0;i<files.length;i++){
        fNames+=files[i].getName()+"; ";
      }
      nameOfFiles.setText(fNames);
    }
    getHeaders(files[0]);
    progressBar.setString("Files Selected!!!");
  }

  void initForms(java.util.List<String> cols){
    typeOf.removeAllItems();
    timeint.removeAllItems();
    fields.removeAllItems();
    timecol.removeAllItems();

    typeOf.setEnabled(true);
    timeint.setEnabled(false);
    fields.setEnabled(true);
    filter.setEnabled(true);
    generate.setEnabled(true);

    typeOf.addItem("ROWS");
    typeOf.addItem("COUNT");

    timeint.addItem("DAILY");
    timeint.addItem("HOURLY");
    timeint.addItem("NONE");

    head = new String[cols.size()];
    for(int i=0;i<cols.size();i++){
      head[i] = cols.get(i);
//      records.getTableHeader().getColumnModel().getColumn(i).setHeaderValue(cols.get(i));
      fields.addItem(cols.get(i));
      timecol.addItem(cols.get(i));
    }

    dataModel=new DefaultTableModel(null, head);
    records = new JTable(dataModel);
    JTableHeader header = records.getTableHeader();
    header.setFont(new Font("Serif", Font.BOLD, 10));
    header.setBorder(new LineBorder(Color.BLACK, 1, true));
    pRec.removeAll();
    pRec.add(new JScrollPane(records), BorderLayout.CENTER );

    fields.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String s = typeOf.getSelectedItem().toString();
          setTable(s);
        } catch (NullPointerException ex) {}
      }
    });

    timeint.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String s = timeint.getSelectedItem().toString();
          if (s.equals("NONE")) {
            s = (String) typeOf.getSelectedItem();
            setTable(s);
            timecol.setEnabled(false);
          } else {
            s = (String) typeOf.getSelectedItem();
            setTable(s);
            timecol.setEnabled(true);
          }
        } catch(NullPointerException ex){}
      }
    });


    typeOf.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String s = typeOf.getSelectedItem().toString();
          setTable(s);
          if (s.equals("COUNT")) {
//          timeint.setEnabled(true);
            timeint.setEnabled(true);
            filter.setText("");
            filter.setEnabled(false);
            timecol.setEnabled(true);
          } else {
//          timeint.setEnabled(false);
            timeint.setEnabled(false);
            filter.setEnabled(true);
            timecol.setEnabled(false);
          }
        } catch(NullPointerException ex){}
      }
    });
  }
  void setTable(String opt){
    if(opt.equals("COUNT")){
      String s = timeint.getSelectedItem().toString();
      if(s.equals("NONE")){
        String head[] = new String[]{fields.getSelectedItem().toString(), "FREQUENCY"};
        dataModel = new DefaultTableModel(null, head);
//      dataModel.setRowCount(0);
        records = new JTable(dataModel);
//          records.setEnabled(false);
        JTableHeader header = records.getTableHeader();
        header.setFont(new Font("Serif", Font.BOLD, 10));
        header.setBorder(new LineBorder(Color.BLACK, 1, true));
        pRec.removeAll();
        pRec.revalidate();
        pRec.repaint();
        pRec.add(new JScrollPane(records), BorderLayout.CENTER);
      } else {
        String head[] = new String[]{"TIME", fields.getSelectedItem().toString(), "FREQUENCY"};
        dataModel = new DefaultTableModel(null, head);
//      dataModel.setRowCount(0);
        records = new JTable(dataModel);
//          records.setEnabled(false);
        JTableHeader header = records.getTableHeader();
        header.setFont(new Font("Serif", Font.BOLD, 10));
        header.setBorder(new LineBorder(Color.BLACK, 1, true));
        pRec.removeAll();
        pRec.revalidate();
        pRec.repaint();
        pRec.add(new JScrollPane(records), BorderLayout.CENTER);
      }
    } else {
      dataModel=new DefaultTableModel(null, head);
//      dataModel.setRowCount(0);
      records = new JTable(dataModel);
      JTableHeader header = records.getTableHeader();
      header.setFont(new Font("Serif", Font.BOLD, 10));
      header.setBorder(new LineBorder(Color.BLACK, 1, true));
      pRec.removeAll();
      pRec.revalidate();
      pRec.repaint();
      pRec.add(new JScrollPane(records), BorderLayout.CENTER );
    }
  }
}
