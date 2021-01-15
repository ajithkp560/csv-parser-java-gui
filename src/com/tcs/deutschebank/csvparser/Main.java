package com.tcs.deutschebank.csvparser;

import javax.swing.*;
import java.awt.*;

public class Main  {
  public static void main(String[] args){
//    Runnable runnable = new Runnable(){
//      public void run(){
//        MainWindow mw = new MainWindow("CSV Parser for OTPS ~ OTPS SL2");
//        mw.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        mw.setVisible(true);
//        mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      }
//    };
    MainWindow mw = new MainWindow("CSV Parser");
    mw.setExtendedState(JFrame.MAXIMIZED_BOTH);
    mw.setMinimumSize(new Dimension(700, 500));
    mw.setVisible(true);
    mw.setLocationRelativeTo(null);
    mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    System.out.println("Developed By: Ajith Kp [Ajith Kavullapura]");
    System.out.println(" \u00a9 TCS 2021 \u00a9 ");
  }
}
