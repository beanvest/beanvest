package beanvest.acceptance.returns.processingrework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ServiceFactoryRegistry {
    Map<String, Function<ServiceFactoryRegistry, Object>> factories = new HashMap<>();
    Map<String, Object> instances = new HashMap<>();
    Set<StatsStrategiesTest.Processor> processors = new HashSet<>();

    public <T extends Object> void register(Class<T> builtClass, Function<ServiceFactoryRegistry, T> xirrFactory) {
        factories.put(builtClass.getName(), (Function<ServiceFactoryRegistry, Object>) xirrFactory);
    }

    public <T> T getOrCreate(Class<T> requestedClass) {
        return getOrCreateByClassName(requestedClass.getName());
    }

    private <T> T getOrCreateByClassName(String name) {
        var instance = instances.get(name);
        if (instance != null) {
            return (T) instance;
        }
        var factory = factories.get(name);
        if (factory == null) {
            throw new RuntimeException("factory for " + name + " is not registered");
        }
        var object = (T) factory.apply(this);
        instances.put(object.getClass().getName(), object);
        if (object instanceof StatsStrategiesTest.Processor) {
            processors.add((StatsStrategiesTest.Processor) object);
        }
        return object;
    }

    public Set<StatsStrategiesTest.Processor> getProcessors() {
        return processors;
    }

    public void instantiateServices() {
        factories.keySet().forEach(this::getOrCreateByClassName);
    }
}
