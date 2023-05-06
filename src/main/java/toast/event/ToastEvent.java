package toast.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ToastEvent {
    // dispatch() -> unregisterListener() chain produces concurrent situation
    private static final Map<ListenerKey, Consumer<ToastEvent>> map = new ConcurrentHashMap<>();
    private static int nextId = 0;

    private record ListenerKey(int id, String type) {
        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static int registerListener(Class<? extends ToastEvent> type, Consumer<? extends ToastEvent> listener) {
        map.put(new ListenerKey(nextId, type.getName()), (Consumer<ToastEvent>) listener);
        return nextId++;
    }

    public static void unregisterListener(int id) {
        doUnregister(id);
    }

    public static void dispatch(Class<? extends ToastEvent> type, ToastEvent event) {
        for (var entry : map.entrySet()) {
            if (entry.getKey().type.equals(type.getName())) {
                entry.getValue().accept(event);
            }
        }
    }

    private static void doUnregister(int id) {
        var iter = map.entrySet().iterator();

        while (iter.hasNext()) {
            var entry = iter.next();

            if (entry.getKey().id == id) {
                iter.remove();
                break;
            }
        }
    }
}
