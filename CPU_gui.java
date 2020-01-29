package com.cpuSim;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

public class CPU_gui {
    private JPanel panel1;
    private JButton calculateButton;
    private JButton addProcessesButton;
    private JTable table1;
    private JTabbedPane tabbedPane1;
    private JRadioButton firstComeFirstServedRadioButton;
    private JRadioButton shortestJobFirstSJFRadioButton;
    private JRadioButton priorityScheduelingRadioButton;
    private JTabbedPane tabbedPane2;
    private JTextField textField1;
    private JTextField textField2;
    private static int processes = 0;
    private static DefaultTableModel model = new DefaultTableModel();

    private static Object[] rows = new Object[6];

    public CPU_gui() {
        Font font = new Font("", 1, 22);
        model.setColumnIdentifiers(new String[]{"Processes","Arrival Time","Burst Time","Priority","Waiting Time","TurnAround Time"});
        table1.setModel(model);
        table1.setFont(font);
        table1.setRowHeight(30);
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] BTs = new int[processes];
                int[] ART = new int[processes];//arrival times
                if (processes == 0) {
                    JOptionPane.showMessageDialog(null, "No process is entered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for (int i = 0; i < processes; i++) {
                    BTs[i] = Integer.parseInt((String) table1.getValueAt(i+1, 2));
                }
                for (int i = 0; i < processes; i++) {
                    ART[i] = Integer.parseInt((String) table1.getValueAt(i+1, 1));
                }
                if (firstComeFirstServedRadioButton.isSelected()) {
                    int i = 1;
                    FCFS fc = new FCFS(processes, ART, BTs);
                    for (Map.Entry<String, Integer> entry : fc.ArrivalTimeMap.entrySet()) {
                        model.setValueAt(entry.getKey(), i, 0);
                        model.setValueAt(entry.getValue(), i, 1);
                        model.setValueAt(fc.BurstTimeMap.get(entry.getKey()), i, 2);
                        model.setValueAt(fc.WaitingTimeMap.get(entry.getKey()), i, 4);
                        model.setValueAt(fc.TurnAroundTimeMap.get(entry.getKey()), i, 5);
                        i++;
                    }
                    textField1.setText(String.valueOf(fc.AverageWT()));
                    textField2.setText(String.valueOf(fc.AverageTAT()));

                } else if (shortestJobFirstSJFRadioButton.isSelected()) {
                    int i = 1;
                    NONPreempSJF sjf = new NONPreempSJF(processes, ART, BTs);
                    for (Map.Entry<String, Integer> entry : sjf.ArrivalTimeMap.entrySet()) {
                        model.setValueAt(entry.getKey(), i, 0);
                        model.setValueAt(entry.getValue(), i, 1);
                        model.setValueAt(sjf.BurstTimeMap.get(entry.getKey()), i, 2);
                        model.setValueAt(sjf.WaitingTimeMap.get(entry.getKey()), i, 4);
                        model.setValueAt(sjf.TurnAroundTimeMap.get(entry.getKey()), i, 5);
                        i++;
                    }
                    textField1.setText(String.valueOf(sjf.AverageWT()));
                    textField2.setText(String.valueOf(sjf.AverageTAT()));
                } else if (priorityScheduelingRadioButton.isSelected()) {
                    int i = 1;
                    int[] Prio = new int[processes];
                    for (int k = 0; k < processes; k++) {
                        Prio[k] = Integer.parseInt((String) table1.getValueAt(k+1, 3));
                    }
                    NONPreemPriority preem = new NONPreemPriority(processes, ART, Prio, BTs);
                    for (Map.Entry<String, Integer> entry : preem.ArrivalTimeMap.entrySet()) {
                        model.setValueAt(entry.getKey(), i, 0);
                        model.setValueAt(entry.getValue(), i, 1);
                        model.setValueAt(preem.BurstTimeMap.get(entry.getKey()), i, 2);
                        model.setValueAt(preem.PriorityMap.get(entry.getKey()), i, 3);
                        model.setValueAt(preem.WaitingTimeMap.get(entry.getKey()), i, 4);
                        model.setValueAt(preem.TurnAroundTimeMap.get(entry.getKey()), i, 5);
                        i++;
                    }
                    textField1.setText(String.valueOf(preem.AverageWT()));
                    textField2.setText(String.valueOf(preem.AverageTAT()));
                }
            }

        });
        addProcessesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processes = Integer.parseInt(JOptionPane.showInputDialog("Enter number of processes"));
                rows[0] = "Processes";
                rows[1] = "Arrival Time";
                rows[2] = "Burst Time";
                rows[3] = "Priority";
                rows[4] = "Waiting Time";
                rows[5] = "TurnAround Time";
                model.addRow(rows);
                for (int i = 0; i < processes; i++) {
                    rows[0] = "P" + (i + 1);
                    rows[1] = "";
                    rows[2] = "";
                    rows[3] = "";
                    rows[4] = "";
                    rows[5] = "";
                    model.addRow(rows);
                }
            }
        });

        firstComeFirstServedRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shortestJobFirstSJFRadioButton.setSelected(false);
                priorityScheduelingRadioButton.setSelected(false);
            }
        });
        shortestJobFirstSJFRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                priorityScheduelingRadioButton.setSelected(false);
                firstComeFirstServedRadioButton.setSelected(false);
            }
        });
        priorityScheduelingRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstComeFirstServedRadioButton.setSelected(false);
                shortestJobFirstSJFRadioButton.setSelected(false);
            }
        });
    }

    public static void main(String[] args) {
        JFrame CPU = new JFrame("gui");
        CPU.setContentPane(new CPU_gui().panel1);
        CPU.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CPU.pack();
        CPU.setVisible(true);
    }
}
