package event_bus;

import java.util.*;

public class EventBus {
    // LinkedHashSet을 써야하는 이유)
    //  그냥 HashSet은 순회 순서가 보장되질 않아서 롤백이나 리플레이시 의사 난수 호출 순서가 달라지므로 결과가 바뀜
    Map<Class, LinkedHashSet<IEventBusSubscriber>> subscribers = new HashMap<>();
    Map<Class, LinkedHashSet<IEventBusSubscriber>> forRegister = new HashMap<>();
    Map<Class, LinkedHashSet<IEventBusSubscriber>> forUnregister = new HashMap<>();

    public EventBus() {
        //Kryo 직렬화를 위한 매개변수 없는 생성자
    }

    public void register(Class eventClass, IEventBusSubscriber subscriber){
        if(!forRegister.containsKey(eventClass)) forRegister.put(eventClass, new LinkedHashSet<>());

        forRegister.get(eventClass).add(subscriber);
    }

    public void unregister(Class eventClass, IEventBusSubscriber subscriber){
        if(!forUnregister.containsKey(eventClass)) forUnregister.put(eventClass, new LinkedHashSet<>());

        forUnregister.get(eventClass).add(subscriber);
    }

    public void publish(Object event){
        // ConcurrentModification을 피하기 위하여 실제 register/unregister는 여기서 처리
        processRegistrations();

        // publish처리
        if(!subscribers.containsKey(event.getClass())) return;
        if(subscribers.get(event.getClass()).isEmpty()) return;

        for(IEventBusSubscriber subscriber : subscribers.get(event.getClass())){
            subscriber.notify(event);
        }
    }
    private void processRegistrations() {
        // 메인 메서드는 단순히 흐름만 제어합니다 (복잡도 1)
        applyNewRegistrations();
        applyUnregistrations();
    }

// --- 추출된 메서드들 ---

    private void applyNewRegistrations() {
        // entrySet을 사용하여 불필요한 map.get() 호출 제거
        for (Map.Entry<Class, LinkedHashSet<IEventBusSubscriber>> entry : forRegister.entrySet()) {
            Class eventClass = entry.getKey();
            Set<IEventBusSubscriber> newSubscribers = entry.getValue();

            // 1. computeIfAbsent: 키가 없으면 LinkedHashSet 생성 후 반환 (if 제거)
            // 2. addAll: 루프 없이 한 번에 추가 (내부 for 제거)
            subscribers.computeIfAbsent(eventClass, k -> new LinkedHashSet<>())
                    .addAll(newSubscribers);
        }
    }

    private void applyUnregistrations() {
        for (Map.Entry<Class, LinkedHashSet<IEventBusSubscriber>> entry : forUnregister.entrySet()) {
            Class eventClass = entry.getKey();
            Set<IEventBusSubscriber> targetsToRemove = entry.getValue();

            Set<IEventBusSubscriber> currentSubscribers = subscribers.get(eventClass);

            // 해당 이벤트 클래스에 대한 구독자가 존재할 때만 제거 수행
            if (currentSubscribers != null) {
                // removeAll: 루프 없이 한 번에 제거 (내부 for 제거)
                // isEmpty() 체크 불필요 (비어있으면 아무 일도 안 일어남)
                currentSubscribers.removeAll(targetsToRemove);
            }
        }
    }
}
