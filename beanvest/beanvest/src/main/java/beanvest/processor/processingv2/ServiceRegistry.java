package beanvest.processor.processingv2;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("unchecked") // maybe Class<?> instead of Objects and Strings would get rid of this warning?
public class ServiceRegistry {
    private final Map<String, Function<ServiceRegistry, Object>> factories = new HashMap<>();
    private final Map<String, Object> instances = new HashMap<>();
    private final Set<Processor> processors = new HashSet<>();

    public <T> void registerFactory(Class<T> builtClass, Function<ServiceRegistry, T> factory) {
        factories.put(builtClass.getName(), (Function<ServiceRegistry, Object>) factory);
    }
    public Calculator getCollector(Class<?> requestedClass) {
        return getOrCreateByClassName(requestedClass.getName());
    }

    public <T> T get(Class<T> requestedClass) {
        return getOrCreateByClassName(requestedClass.getName());
    }

    private <T> T getOrCreateByClassName(String name) {
        var instance = instances.get(name);
        if (instance != null) {
            return (T) instance;
        }
        var factory = factories.get(name);
        if (factory == null) {
            throw new RuntimeException("Factory of class " + name + " is not registered");
        }
        var object = (T) factory.apply(this);
        instances.put(object.getClass().getName(), object);
        if (object instanceof Processor) {
            processors.add((Processor) object);
        }
        return object;
    }

    public Set<Processor> getProcessors() {
        return processors;
    }

    public void instantiateServices(Collection<Class<?>> services) {
        for (Class<?> service : services) {
            getOrCreateByClassName(service.getName());
        }
    }
}
