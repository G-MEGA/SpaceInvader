package event_bus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventBus {
    Map<Class, Set<IEventBusSubscriber>> subscribers = new HashMap<>();
    Map<Class, Set<IEventBusSubscriber>> forRegister = new HashMap<>();
    Map<Class, Set<IEventBusSubscriber>> forUnregister = new HashMap<>();

    //Kryo 직렬화를 위한 매개변수 없는 생성자
    public EventBus() {}

    public void register(Class eventClass, IEventBusSubscriber subscriber){
        if(!forRegister.containsKey(eventClass)) forRegister.put(eventClass, new HashSet<>());

        forRegister.get(eventClass).add(subscriber);
    }

    public void unregister(Class eventClass, IEventBusSubscriber subscriber){
        if(!forUnregister.containsKey(eventClass)) forUnregister.put(eventClass, new HashSet<>());

        forUnregister.get(eventClass).add(subscriber);
    }

    public void publish(Object event){
        // ConcurrentModification을 피하기 위하여 실제 register/unregister는 여기서 처리
        for(Class eventClass:forRegister.keySet()){
            Set<IEventBusSubscriber> set = forRegister.get(eventClass);
            for(IEventBusSubscriber subscriber:set){
                if(!subscribers.containsKey(eventClass)) subscribers.put(eventClass, new HashSet<>());

                subscribers.get(eventClass).add(subscriber);
            }
        }
        for(Class eventClass:forUnregister.keySet()){
            Set<IEventBusSubscriber> set = forUnregister.get(eventClass);
            for(IEventBusSubscriber subscriber:set){
                if(!subscribers.containsKey(eventClass)) continue;
                if(subscribers.get(eventClass).isEmpty()) continue;

                subscribers.get(eventClass).remove(subscriber);
            }
        }

        // publish처리
        if(!subscribers.containsKey(event.getClass())) return;
        if(subscribers.get(event.getClass()).isEmpty()) return;

        for(IEventBusSubscriber subscriber : subscribers.get(event.getClass())){
            subscriber.notify(event);
        }
    }
}
