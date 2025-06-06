import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import samples.cloudrun.GCESample.cmd.GCEComputeCmd;
import samples.cloudrun.GCESample.model.GCEInstance;
import samples.cloudrun.GCESample.controller.GCEInstanceControllerCmd;

public class GCEInstanceControllerCmdTest {

    private GCEInstanceControllerCmd controller;
    private GCEComputeCmd mockCmd;

    @BeforeEach
    public void setUp() {
        mockCmd = mock(GCEComputeCmd.class);
        controller = new GCEInstanceControllerCmd(mockCmd);
    }

    @Test
    public void testVersionApp() {
        String version = controller.versionApp();
        assertEquals("<h2>Version 1.0</h2>", version);
    }

    @Test
    public void testCreateInstance() {
        GCEInstance instance = new GCEInstance("test-instance", "us-central1-a");
        when(mockCmd.createInstance(anyString(), any(GCEInstance.class))).thenReturn(true);

        ResponseEntity<ResponseMessage> response = controller.createInstance(instance);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Instance created successfully", response.getBody().getMessage());
    }

    @Test
    public void testDeleteInstance() {
        GCEInstance instance = new GCEInstance("test-instance", "us-central1-a");
        when(mockCmd.deleteInstance(anyString(), any(GCEInstance.class))).thenReturn(true);

        ResponseEntity<ResponseMessage> response = controller.deleteInstance("test-project", "us-central1-a", "test-instance");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Instance deleted successfully", response.getBody().getMessage());
    }
}