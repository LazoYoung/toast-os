package toast.impl;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;
import toast.event.ToastEvent;
import toast.event.process.*;
import toast.event.processor.ProcessorDeactivateEvent;
import toast.event.processor.ProcessorRebootEvent;
import toast.persistence.domain.ProcessorRecord;
import toast.persistence.domain.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToastRecorder {

    private final Scheduler scheduler;
    private final List<Integer> loggerList = new ArrayList<>();
    private final List<Integer> watcherList = new ArrayList<>();

    public ToastRecorder(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void startRecording() {
        int w1 = ToastEvent.registerListener(ProcessRunEvent.class, (ProcessRunEvent event) -> {
            Processor processor = event.getProcessor();
            Process process = event.getProcess();
            ProcessorRecord record = Record.getInstance().getProcessorRecord(processor);
            record.setProcessAtTime(process, scheduler.getElapsedTime());
        });

        Collections.addAll(watcherList, w1);
    }

    public void startLoggingEvents() {
        int l1 = ToastEvent.registerListener(ProcessDispatchEvent.class, (ProcessDispatchEvent event) -> {
            Processor processor = event.getProcessor();
            String coreName = processor.getCore().getName();
            int coreId = processor.getId();
            int pid = event.getProcess().getId();
            System.out.printf("| Dispatch process #%d to %s #%d%n", pid, coreName, coreId);
        });

        int l2 = ToastEvent.registerListener(ProcessCompleteEvent.class, (ProcessCompleteEvent event) -> {
            System.out.printf("| Process #%d complete%n", event.getProcess().getId());
        });

        int l3 = ToastEvent.registerListener(ProcessPreemptEvent.class, (ProcessPreemptEvent event) -> {
            int from = event.getProcess().getId();
            int to = event.getNextProcess().getId();
            System.out.printf("| Preempt process #%d → #%d%n", from, to);
        });

        int l4 = ToastEvent.registerListener(ProcessReadyEvent.class, (ProcessReadyEvent event) -> {
            System.out.printf("| Process #%d is ready%n", event.getProcess().getId());
        });

        int l5 =  ToastEvent.registerListener(ProcessorDeactivateEvent.class, (ProcessorDeactivateEvent event) -> {
            Processor processor = event.getProcessor();
            String coreName = processor.getCore().getName();
            int id = processor.getId();
            String cause = switch (event.getCause()) {
                case POWER_LOSS -> "power loss";
                case POWER_SAVING -> "power saving";
            };
            System.out.printf("| %s #%d deactivated due to %s%n", coreName, id, cause);
        });

        int l6 = ToastEvent.registerListener(ProcessorRebootEvent.class, (ProcessorRebootEvent event) -> {
            Processor processor = event.getProcessor();
            String coreName = processor.getCore().getName();
            int id = processor.getId();
            System.out.printf("| Reboot %s #%d%n", coreName, id);
        });

        Collections.addAll(this.loggerList, l1, l2, l3, l4, l5, l6);
    }

    public void eraseRecords() {
        this.watcherList.forEach(ToastEvent::unregisterListener);
        this.watcherList.clear();
    }

    public void stopLoggingEvents() {
        this.loggerList.forEach(ToastEvent::unregisterListener);
        this.loggerList.clear();
    }

}
