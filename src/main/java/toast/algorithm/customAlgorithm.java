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

/*
* 04월 18일 
 * idle List 프로세서 사용할 방법? 
 * 1. Jcore 프로세서 첫 번째에만 몰릴 가능성.
 * 2. 반선점 올려보기.
 * 3. 함수 나누기 
 * 4. 작동여부 확인하기
 */

public class customAlgorithm implements Algorithm {
    @Override 
    
    public void run(Scheduler scheduler) {
        // PriorityQueue<Process> PQ = new PriorityQueue<>(Collections.reverseOrder()); // 가장 큰게 먼저
        PriorityQueue<Process> processPQ = new PriorityQueue<>(new ProcessComparator().reversed());
        processPQ.addAll(scheduler.getReadyQueue());
        
        List<Processor> allList = scheduler.getProcessorList(); // 모든 프로세서 get
        List<Processor> JcoreList = scheduler.getProcessorList(); // 미션아닌 프로세스 위한 코어 목록, 그냥 생성이 안되는 것 같아서 우선 채우고 시작.
        List<Processor> idleList = scheduler.getIdleProcessorList();
        if (allList.isEmpty()) return;

        Iterator<Processor> processors = allList.iterator();
        Iterator<Processor> testItr = allList.iterator();
        Iterator<Processor> JcoreItr = JcoreList.iterator();
        Iterator<Processor> idleItr = idleList.iterator();
        Processor Mcore = null;

        /*
         * <Mcore, JcoreList 만들기> 
         * 전체 프로세서 돌면서 Mcore 1개, 나머지는 Jcore목록에 넣기
         * 만약 Mcore 선정조건을 변경한다면 여기를 바꾸면 됨 (성능코어를 우선한다던지)
         */
        JcoreList.clear();
        while(testItr.hasNext()){
            Processor tmpCore = testItr.next();
            if(tmpCore.getId() == 0){
                Mcore = tmpCore;
            }else{
                JcoreList.add(tmpCore);
            }
        }
        
        /*
         * <프로세스 나누고 할당하는 부분>
         * 
         */
        while (canExecute(processPQ, processors) ) {

            Process process = processPQ.poll();
            Processor processor = processors.next();

            boolean isItMission = process.getId() == 1; // 첫 번째 코어 들고오기(임시) 
            boolean isItMcore = process.getId() == 0; // getId() -> isMission() (04/18)
            
            //사용가능 프로세서의 ID가 Mcore와 같다면 = 미션 전용 프로서세를 들고 온다면

            /*
             * "미션코어이고 미션프로세스라면"
             * 프로세서를 Mcore로 지정
             * 뽑은 프로세스를 바로 프로세서에 할당. 
             * 
             * + 만약 미션코어 놀리지 않겠다고 미션아닌 프로세스가 들어가도록 한다면 여기에 반선점 구현(아래 경우랑 같이 진행해야)
             */
            if (isItMcore && isItMission){ 
                processor = Mcore;// 의미 없음. 이미 미션 코어, 프로세스이기 때문에
                process = process;
            }
            /*
             * "미션코어인데 미션 아닌 프로세스라면" 
             * -> Jcore로 보내기 or +로 설명된 방법
             * 프로세서: HRRN 방법으로 Jcore리스트에서 뽑기 -> Jcore에서 첫 번째 요소에만 몰릴 가능성. 
             * -> ?
             * 
             * + 미션 코어 놀리지 않겠다면 비었는지 확인하고 진행
             */
            else if(isItMcore && !isItMission){ 
                // processor = JcoreItr.next(); // 미션 코어 놀리는 것 상관없으면 이것 사용해도 무관
                // if (!processor.isIdle()){ // Jcore목록에서 뽑았는데 사용불가하다면...? 근데 어차피 프로세스는 누적될건데 괜찮지 않나? 
                //     processor = idleItr.next(); 
                // }

                boolean isProcessorEmpty = processor.getRunningProcess().isEmpty();
                if (isProcessorEmpty){
                    
                }
            }
            /*
             * "미션코어가 아닌데 미션 프로세스라면"
             * 프로세서 변경
             * 프로세서 목록에서 뽑아낸 프로세서가 Mcore가 아닌데 미션이기 때문에 프로세서를 Mcore로 변경
             * -> 현재 
             */
            else if(!isItMcore && isItMission){
                processor = Mcore;
            }
            /*
             * "미션코어도 아니고 미션 아닌 프로세스라면"
             * processors에서 뽑았는데 Mcore가 아닌거니까 JcoreList에 속하는 것.
             * 프로세스는 별도로 처리할 것 없으니까 그냥 진행.
             */
            else if(!isItMcore && !isItMission){ 
                // 아무것도 없어도 될듯
            }

            /*
             * 마지막 과정은 동일.
             * 위에서 프로세서, 프로세스 결정한 후 dispatch한다.
             */
            scheduler.dispatch(processor, process);
            String coreName = processor.getCore().getName();
            int pid = process.getId();

            process.addCompletionListener(() -> System.out.printf("│[custom] Process #%d completed%n", pid));
            System.out.printf("│[custom] Dispatched process #%d to %s%n", pid, coreName);
        }
        
    }

    // 우선 하나의 함수로 해놓고 나서 함수들을 나누는 것도 좋을듯?
    // private static void runWith(Scheduler scheduler, PriorityQueue<Process> processPQ,
    // Iterator<Processor> processors){
    //     while(canExecute(processPQ, processors) && ){
    //         if 
    //     }
    // }

    // private static void runWith(Scheduler scheduler, PriorityQueue<Process> processorPQ) {
    //     Iterator<Processor> idleProcessorIterator = scheduler.getIdleProcessorList().iterator();

    //     while(canExecute(processorPQ, idleProcessorIterator)) {
    //         Processor idleProcessor = idleProcessorIterator.next();
    //         Process nextProcess = processorPQ.poll();

    //         dispatch(scheduler, idleProcessor, nextProcess);
    //     }
    // }

    // private static void action4M(Scheduler scheduler, PriorityQueue<Process> processPQ, Processor currProcessor){
    // }

    /*
     * 마법의 가중치와 계산식을 만들자.
     */
    private int getMagicValue(Process process) { 
        int WT = process.getWaitingTime();
        int WL = process.getWorkload(); // same as BT
        int niceValue = 10; // 값이 어찌 나오는지 보고 높이거나 낮추기
        // int isMission = (process.isMission() == true) ? 1:0; // 리팩토링되면 얘로 변경.(04/14) 
        int isMission = (true) ? 1 : 0;
        return (int) (((WT + WL) / WL) + (niceValue * isMission));
    }

    private static boolean canExecute(PriorityQueue<Process> processorPQ, Iterator<Processor> idleProcessorIterator) {
        return !processorPQ.isEmpty() && idleProcessorIterator.hasNext();
    }

    class ProcessComparator implements Comparator<Process> {

        @Override
        public int compare(Process e1, Process e2) {
            return Integer.compare(getMagicValue(e1), getMagicValue(e2));
        }
    }
    
}
