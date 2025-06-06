package samples.cloudrun.GCESample.service;

import samples.cloudrun.GCESample.model.GCEInstance;

import java.util.HashMap;
import java.util.Map;

public class GCEInstanceService {

    private Map<String, GCEInstance> computeVMs = new HashMap<>();

    public static GCEInstanceService getInstance() {
        return new GCEInstanceService();
    }

    public Map<String, GCEInstance> getComputeInstances() {
        return computeVMs;
    }
}