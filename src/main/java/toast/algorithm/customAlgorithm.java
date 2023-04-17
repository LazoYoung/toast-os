package toast.algorithm;

import toast.api.Core;
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
        // PriorityQueue<Process> PQ = new PriorityQueue<>(Collections.reverseOrder()); // 가장 큰게 먼저
        List<Processor> idleList = scheduler.getIdleProcessorList();

        if (idleList.isEmpty()) return;
        Iterator<Processor> processors = idleList.iterator();
        Iterator<Process> processes = scheduler.getReadyQueue().stream()
                .sorted(Comparator.comparingDouble(this::getMagicValue).reversed())
                .iterator();

      

        while (processors.hasNext() && processes.hasNext()) {
            Processor processor = processors.next();
            Process process = processes.next();
            String coreName = processor.getCore().getName();
            int pid = process.getId();

            scheduler.dispatch(processor, process);
            process.addCompletionListener(() -> System.out.printf("│[custom] Process #%d completed%n", pid));
            System.out.printf("│[custom] Dispatched process #%d to %s%n", pid, coreName);
        }
        // List<Processor> totalList = scheduler.getProcessorList();
        // List<Processor> idleList = scheduler.getIdleProcessorList();
        // if (idleList.isEmpty()) return; // 사용가능한 프로세서가 있는지 확인하기
        
        // Iterator<Processor> testItr = totalList.iterator(); 
        // Iterator<Processor> IdleItr = idleList.iterator(); 
        // List<Processor> JcoreList= scheduler.getIdleProcessorList(); // 임시방편, new List<>()이 안된다.
        // Processor Mcore = null; // null을 쓰는게 잘 모르겠지만 께름칙하다, 다른 방법 찾아야 할 것 같다. 

        /*
         * 코어 두 종류로 나누기 위함인데, 사용가능한 프로세서 중에서 고르는 거라서 이게 좀 문제야
         * 전체 코어 리스트를 가져와서 IdleProcessorList와 비교? 
         * 코어를 특정하고 싶은데, 어떻게 해야할까. 
         * 성능코어가 여럿이면, Mcore가 돌아가면서 설정될 수 있지 않을까? 
         * 전체와 Idle을 불러와. 코어 전체를 순회하는데 귀찮으니 첫 번째 프로세서를 Mcore로 설정하자. 
         */
        // if (testItr.hasNext()){ 
        //     Processor tmp = testItr.next();
        //     // Mcore = (tmp.getCore() == Core.PERFORMANCE) ? tmp: null;
        //     Mcore = tmp; // 임시방편 
        //     // Mcore = (tmp.getCore() == Core.EFFICIENCY) ? tmp: null;
        // }


        // JcoreList.clear();
        // int IPcount = 0;
        // while(IdleItr.hasNext()){ 
        //     Processor tmp = IdleItr.next();
        //     if(tmp.getCore() == Core.EFFICIENCY){
        //         JcoreList.add(tmp);
        //     }
        //     IPcount++;
        // }

        // Iterator<Processor> processors = idleList.iterator(); // 사용가능한 프로세서를 반복자로.
        // Iterator<Processor> JcoreItr = JcoreList.iterator(); // Jcore들을 반복자로.
        
        // Iterator<Process> processes = scheduler.getReadyQueue().stream()
        //         .sorted(Comparator.comparingDouble(this::getMagicValue).reversed())
        //         .iterator();
        
        // // 우선순위 큐에 넣기 - 계산이 중복되는 것 같지만.
        // while(processes.hasNext()){
        //     Process process = processes.next(); // 낭비적인 요소가 많지만 그래도 역할은 하니까...
        //     PQ.add(process);
        // }
    
        /*
        * 여기서 코어 할당을 생각해야 한다.
        */
        // while (processors.hasNext() && !PQ.isEmpty()) { // Jcore를 다 할당해도 Mcore가 있을 수 있으니 조건에 JcoreItr.hasNext를 넣으면 안될듯 싶다
        //     Processor processor = processors.next(); //초기화 느낌
        //     Process process = PQ.poll();
        //     boolean isMission = true;

        //     /*
        //      * isMission이 true인 경우. 
        //      * 변경 필요: 리팩토링되면 process.isMission으로 변경.(0414)
        //      * 전체 프로세서가 하나면 단 한번만 돌겠지. 그때는 있는 코어로. 위에 있는 내용.
        //      * 그러면 하나가 아닌 경우만 봐주면 된다. 
        //      */
        //     if ((scheduler.getProcessorList().size() != 1)){ 
        //         if(isMission  == true){
        //             processor = Mcore;     
        //         }else{
        //             processor = JcoreItr.next();
        //         }
        //     }

            
        //     /*
        //     * 여기에 반선점 구현
        //     * preemption 메소드 써야 할 것 같은데, 조건문을 아직 못한다.
        //     * 이유: 디스패치될 프로세스와 비교할 getRunningProcess 메소드가 뭔지 잘 모르겠다. 
        //     */
            
        //     String coreName = processor.getCore().getName();
        //     int pid = process.getId();
        //     scheduler.dispatch(processor, process);
        //     process.addCompletionListener(() -> System.out.printf("│[custom] Process #%d completed%n", pid));
        //     System.out.printf("│[custom] Dispatched process #%d to %s%n", pid, coreName);
        // }
    }

    /*
     * 마법의 가중치와 계산식을 만들자.
     */
    private double getMagicValue(Process process) { 
        int WT = process.getWaitingTime();
        int WL = process.getWorkload(); // same as BT
        int niceValue = 10; // 값이 어찌 나오는지 보고 높이거나 낮추기
        // int isMission = (process.isMission() == true) ? 1:0; // 리팩토링되면 얘로 변경.(04/14) 
        int isMission = (true) ? 1 : 0;
        return (double) (((WT + WL) / WL) + (niceValue * isMission));
    }
    
}
