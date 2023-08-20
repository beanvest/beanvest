package beanvest.processor.processingv2;

import beanvest.processor.processingv2.validator.Validator;

import java.util.*;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class ServiceRegistry {
    private final Map<String, Function<ServiceRegistry, Object>> factories = new HashMap<>();
    private final Map<String, Object> instances = new HashMap<>();
    private final Set<ProcessorV2> processors = new HashSet<>();

    public <T> void register(Class<T> builtClass, Function<ServiceRegistry, T> factory) {
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
        if (object instanceof ProcessorV2) {
            processors.add((ProcessorV2) object);
        }
        return object;
    }

    public Set<ProcessorV2> getProcessors() {
        return processors;
    }

    public void initialize(Collection<Class<?>> services) {
        for (Class<?> service : services) {
            getOrCreateByClassName(service.getName());
        }
    }
    public List<Validator> instantiateValidators(Collection<Class<? extends Validator>> validators) {
        var validatorList = new ArrayList<Validator>();
        for (var v : validators) {
            validatorList.add(getOrCreateByClassName(v.getName()));
        }
        return validatorList;
    }
}
