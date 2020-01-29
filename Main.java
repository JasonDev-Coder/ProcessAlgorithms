package com.cpuSim;


import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


class MapSort {
    static HashMap<String, Integer> sorting(HashMap<String, Integer> map) {
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
        list.sort(((o1, o2) -> o1.getValue().compareTo(o2.getValue())));
        HashMap<String, Integer> map2 = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            map2.put(entry.getKey(), entry.getValue());
        }
        return map2;
    }

    static Queue<Map.Entry<String, Integer>> sorting(Queue<Map.Entry<String, Integer>> q) {
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(q);
        list.sort(((o1, o2) -> o1.getValue().compareTo(o2.getValue())));
        Queue<Map.Entry<String, Integer>> map2 = new LinkedList<Map.Entry<String, Integer>>();
        for (Map.Entry<String, Integer> entry : list) {
            map2.add(entry);
        }
        return map2;
    }
}

abstract class Algorithms {
    int numProcesses;//number of processes
    double AWT;//average waiting time
    double ATAT;//average turnaround time
    HashMap<String, Integer> ArrivalTimeMap = new HashMap<String, Integer>();
    HashMap<String, Integer> BurstTimeMap = new HashMap<>();
    HashMap<String, Integer> WaitingTimeMap = new HashMap<>();
    HashMap<String, Integer> TurnAroundTimeMap = new HashMap<>();

    Algorithms() {
        numProcesses = (int) (AWT = ATAT = 0);
    }

    Algorithms(int processes,int []arrivalTimes, int...BurstTimes) {
        numProcesses = processes;
        AWT = 0;
        ATAT = 0;
        for (int i = 0; i < numProcesses; i++)
            ArrivalTimeMap.put("P" + (i+1), arrivalTimes[i]);//input arrival times and p0,p1,p2.... in map
        ArrivalTimeMap = MapSort.sorting(ArrivalTimeMap);//sort arrival map in way that we get 1st process id with lowest value ex: p2:0,p1:3,p0:5
        BurstTimeMap.putAll(ArrivalTimeMap);
        BurstTimeMap = MapSort.sorting(BurstTimeMap);//sort it so we can get same as arrival sorted least arr time process 1st
        for (int i = 0; i < BurstTimes.length; i++) {
            BurstTimeMap.replace((String) "P" + (i + 1), BurstTimes[i]);//exchange each process arrival time with its burst time
        }
        WaitingTimeMap.putAll(ArrivalTimeMap);//put arr time map in waiting time map
        WaitingTimeMap = MapSort.sorting(WaitingTimeMap);//sort map so we can get least arr time process 1st
        TurnAroundTimeMap.putAll(ArrivalTimeMap);
        TurnAroundTimeMap = MapSort.sorting(TurnAroundTimeMap);
    }

    double AverageWT() {
        for (Map.Entry<String, Integer> entry : WaitingTimeMap.entrySet()) {
            AWT += entry.getValue();
        }
        return AWT / numProcesses;
    }

    double AverageTAT() {
        for (Map.Entry<String, Integer> entry : TurnAroundTimeMap.entrySet()) {
            ATAT += entry.getValue();
        }
        return ATAT / numProcesses;
    }

    void calcTAT() {
        ArrayList<Integer> BT = new ArrayList(Arrays.asList(BurstTimeMap.values().toArray()));
        ArrayList WT = new ArrayList(Arrays.asList(WaitingTimeMap.values().toArray()));
        for (int i = 0; i < numProcesses; i++) {

            TurnAroundTimeMap.replace("P" + (i + 1), WaitingTimeMap.get("P"+(i+1))+BurstTimeMap.get("P"+(i+1)));
        }
    }

    void getWTs() {
        System.out.println(WaitingTimeMap.toString());
    }

    void getBTs() {
        System.out.println(BurstTimeMap.toString());
    }

    void getATs() {
        System.out.println(ArrivalTimeMap.toString());
    }

    void getTAT() {
        System.out.println(TurnAroundTimeMap.toString());
    }

    int getTotalBurst() {
        int s = 0;
        for (Map.Entry<String, Integer> entry : BurstTimeMap.entrySet()) {
            s += entry.getValue();
        }
        return s;
    }
}

class FCFS extends Algorithms {

    FCFS(int processes,int []arrTimes, int... BurstTimes) {
        super(processes,arrTimes, BurstTimes);
        calcWT();
        calcTAT();
    }

    private void calcWT() {
        ArrayList<Integer> BT = new ArrayList(Arrays.asList(BurstTimeMap.values().toArray()));
        ArrayList AT = new ArrayList(Arrays.asList(ArrivalTimeMap.values().toArray()));
        Integer[] WT = new Integer[numProcesses];
        WT[0] = 0;
        for (int i = 1; i < numProcesses; i++) {
            WT[i] = BT.get(i - 1) + WT[i - 1] + (int) AT.get(i - 1) - (int) AT.get(i);
        }
        int i = 0;
        for (Map.Entry<String, Integer> entry : WaitingTimeMap.entrySet()) {
            WaitingTimeMap.replace(entry.getKey(), WT[i]);//put waiting time of each process in map
            i++;
        }
    }
}


class NONPreempSJF extends Algorithms {
    private Queue<Map.Entry<String, Integer>> ArrivalQueue = new LinkedList<>();

    NONPreempSJF(int processes,int []arrTimes ,int... Bursts) {
        super(processes,arrTimes, Bursts);
        CalcWT();
        calcTAT();
    }

    private void CalcWT() {
        int time = 0;                           //total time of bursts
        boolean found;                  //boolean for if not finding an element and queue is empty we pass time unitl we find a new element whom has arrived
        Map<String, Integer> temp = new ConcurrentHashMap<String, Integer>(ArrivalTimeMap);                //temporary map of arrival time bcz we need to remove from it
        while (time < getTotalBurst()) {        //stop loop when time has reached or passed(probably reached) total bursts
            found = false;                         //at begining no element found to put in queue
            for (Map.Entry<String, Integer> entry : temp.entrySet()) {                                  //we loop through every set in temp
                if (entry.getValue() <= time) {                                      //if arrival time of a process is less than total time passed then we can add its burst time to queue
                    found = true;                                                       //found becomes true bcz an element has been found to put in queue
                    for (Map.Entry<String, Integer> entry2 : BurstTimeMap.entrySet()) {                    //created this loop so just i can iterate over first element in map of bursts and add bursts
                        if (entry2.getKey().compareTo(entry.getKey()) == 0) {                           //each time we iterate if we wanted take example  burst of third process we iterate ro 3rd entry by comparing 3rd entry process name
                            ArrivalQueue.add(entry2);                                                   //we add entry of burst times to entry queue
                            temp.remove(entry.getKey());                                                //we remove entry of arrival time of added process to queue
                            break;
                        }
                    }
                }
            }
            if (!found && ArrivalQueue.isEmpty()) {
                ++time;
                continue;
            }
            ArrivalQueue = MapSort.sorting(ArrivalQueue);               //sort queue to put shortest burst at head
            Iterator<Map.Entry<String, Integer>> iterator = ArrivalQueue.iterator();

            Map.Entry<String, Integer> m = iterator.next();
            WaitingTimeMap.replace(m.getKey(), time - ArrivalTimeMap.get(m.getKey()));      //we put waiting time in the respected key in waiting map
            time += m.getValue();                                       //we add burst of process to time passed
            iterator.remove();                                          //we remove the smalles burst from queue

        }
    }
}

class NONPreemPriority extends Algorithms {
    private Queue<Map.Entry<String, Integer>> ArrivalQueue = new LinkedList<>();
    HashMap<String, Integer> PriorityMap = new HashMap<>(ArrivalTimeMap);

    NONPreemPriority(int processes,int []arrTimes,int []Priorities, int... Bursts) {
        super(processes,arrTimes, Bursts);
        PriorityMap = MapSort.sorting(PriorityMap);
        for (int i = 0; i < numProcesses; i++) {
            PriorityMap.replace("P" + (i + 1), Priorities[i]);
        }
        calcWT();
        calcTAT();
    }


    private void calcWT() {
        int time = 0;                           //total time of bursts
        boolean found;                  //boolean for if not finding an element and queue is empty we pass time unitl we find a new element whom has arrived
        Map<String, Integer> temp = new ConcurrentHashMap<String, Integer>(ArrivalTimeMap);                //temporary map of arrival time bcz we need to remove from it
        while (time < getTotalBurst()) {        //stop loop when time has reached or passed(probably reached) total bursts
            found = false;                         //at begining no element found to put in queue
            for (Map.Entry<String, Integer> entry : temp.entrySet()) {                                  //we loop through every set in temp
                if (entry.getValue() <= time) {                                      //if arrival time of a process is less than total time passed then we can add its burst time to queue
                    found = true;                                                       //found becomes true bcz an element has been found to put in queue
                    for (Map.Entry<String, Integer> entry2 : PriorityMap.entrySet()) {
                        if (entry2.getKey().compareTo(entry.getKey()) == 0) {                           //each time we iterate if we wanted take example  burst of third process we iterate ro 3rd entry by comparing 3rd entry process name
                            ArrivalQueue.add(entry2);                                                   //we add entry of priority to entry queue
                            temp.remove(entry.getKey());                                                //we remove entry of arrival time from map of added process to queue
                            break;
                        }
                    }
                }
            }
            if (!found && ArrivalQueue.isEmpty()) {
                ++time;
                continue;
            }
            ArrivalQueue = MapSort.sorting(ArrivalQueue);               //sort queue to put shortest burst at head
            Iterator<Map.Entry<String, Integer>> iterator = ArrivalQueue.iterator();

            Map.Entry<String, Integer> m = iterator.next();
            WaitingTimeMap.replace(m.getKey(), time - ArrivalTimeMap.get(m.getKey()));      //we put waiting time in the respected key in waiting map
            time += BurstTimeMap.get(m.getKey());                                       //we add burst of process to time passed
            iterator.remove();
        }
    }

    void getPriorities() {
        System.out.println(PriorityMap.toString());
    }
}

public class Main {

    public static void main(String[] args) {
        int []arr ={2,3,0,1};
NONPreempSJF f = new NONPreempSJF(4,arr,2,3,1,2);
f.getATs();
f.getBTs();
f.getWTs();
f.getTAT();
    }
}
