package event_bus;

public interface IEventBusSubscriber {
    void notify(Object event);
}
