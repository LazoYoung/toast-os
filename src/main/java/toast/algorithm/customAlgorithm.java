package toast.algorithm;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class customAlgorithm implements Algorithm {
    @Override    

    public void run(Scheduler scheduler) {
        List<Processor> idleList = scheduler.getIdleProcessorList();
        PriorityQueue<Process> PQ = new PriorityQueue<>(Collections.reverseOrder());

        if (idleList.isEmpty()) return; // 사용가능한 프로세서가 있는지 확인하기
        Iterator<Processor> processors = idleList.iterator(); // 사용가능한 프로세서를 그대로 리스트로.
         //레디큐를 받아와서 계산 & 정렬. 여기를 우선순위 큐로 대체해야. 
        Iterator<Process> processes = scheduler.getReadyQueue().stream()
                .sorted(Comparator.comparingDouble(this::getMagicValue).reversed())
                .iterator();        

        
        /*
        * 여기서 코어 할당을 생각해야 한다.
        */
        // 이미 할당한 프로세서 & 프로세스는 제치고 다음 것을 넣기. 
        while (processors.hasNext() && processes.hasNext()) {
            Processor processor = processors.next();
            Process process = processes.next();
            String coreName = processor.getCore().getName();
            int pid = process.getId();

            scheduler.dispatch(processor, process);
            process.addCompletionListener(() -> System.out.printf("│[HRRN] Process #%d completed%n", pid));
            System.out.printf("│[HRRN] Dispatched process #%d to %s%n", pid, coreName);
        }
    }

    /*
     * 마법의 가중치와 계산식을 만들자.
     */
    private double getMagicValue(Process process) { 
        int WT = process.getWaitingTime();
        int WL = process.getWorkload(); // same as BT
        return (double) (WT + WL) / WL;
    }
    
}
