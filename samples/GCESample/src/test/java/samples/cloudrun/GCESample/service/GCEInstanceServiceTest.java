import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import samples.cloudrun.GCESample.cmd.GCEComputeCmd;
import samples.cloudrun.GCESample.model.GCEInstance;
import samples.cloudrun.GCESample.service.GCEInstanceService;

import java.util.HashMap;
import java.util.Map;

public class GCEInstanceServiceTest {

    @Test
    public void testCreateInstance() {
        GCEComputeCmd mockCmd = mock(GCEComputeCmd.class);
        GCEInstanceService service = new GCEInstanceService(mockCmd);
        GCEInstance instance = new GCEInstance("test-instance", "us-central1-a");

        when(mockCmd.createInstance(anyString(), any(GCEInstance.class))).thenReturn(true);
        boolean created = service.createInstance("test-project", instance);

        assertTrue(created);
    }

    @Test
    public void testDeleteInstance() {
        GCEComputeCmd mockCmd = mock(GCEComputeCmd.class);
        GCEInstanceService service = new GCEInstanceService(mockCmd);
        GCEInstance instance = new GCEInstance("test-instance", "us-central1-a");

        when(mockCmd.deleteInstance(anyString(), any(GCEInstance.class))).thenReturn(true);
        boolean deleted = service.deleteInstance("test-project", instance);

        assertTrue(deleted);
    }

    @Test
    public void testListInstances() {
        GCEInstanceService service = new GCEInstanceService();
        service.getComputeInstances().put("test-instance", new GCEInstance("test-instance", "us-central1-a"));
        Map<String, GCEInstance> instances = service.getComputeInstances();

        assertFalse(instances.isEmpty());
        assertEquals(1, instances.size());
    }
}