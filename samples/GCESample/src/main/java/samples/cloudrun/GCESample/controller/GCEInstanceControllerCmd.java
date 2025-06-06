package samples.cloudrun.GCESample.controller;

import samples.cloudrun.GCESample.service.GCEInstanceService;
import samples.cloudrun.GCESample.model.GCEInstance;
import samples.cloudrun.GCESample.model.cmd.createComputeInstanceParams;
import samples.cloudrun.GCESample.model.cmd.deleteComputeInstanceParams;
import samples.cloudrun.GCESample.cmd.GCEComputeCmd;
import samples.cloudrun.GCESample.message.ResponseMessage;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/compute")
public class GCEInstanceControllerCmd {

    private final GCEInstanceService service = GCEInstanceService.getInstance();

    private static final Logger LOGGER = Logger.getLogger(GCEInstanceControllerCmd.class.getName());
    private static GCEComputeCmd cmd = new GCEComputeCmd();

    @GetMapping("/version")
    public String versionApp() {
        return String.format("<h2>Version 1.0</h2>");
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> response = new HashMap<>();
        response.put("List of created instances", service.getComputeInstances());
        return response;
    }

    @GetMapping("/describe")
    public Map<String, Object> describeInstance(@RequestParam String projectId, @RequestParam String zone, @RequestParam String instanceName) {
        Map<String, Object> response = new HashMap<>();
        response.put("Instance:", cmd.describeInstance(projectId, zone, instanceName));
        return response;
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> createInstance(@RequestBody createComputeInstanceParams params) {
        String message;
        try {
            GCEInstance gceins = new GCEInstance(params.getInstanceName(), params.getZone(), params.getImageSource(), params.getNetworkInterface(), params.getNetworkConfig(), params.getMachineType());
            if (!cmd.createInstance(params.getProjectId(), gceins)) {
                message = "Instance creation failed";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
            LOGGER.info("createInstance() : Instance created");
            message = "Instance created successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Instance creation failed with error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @DeleteMapping("/{projectId:.+}/{zone:.+}/{instanceName:.+}")
    public ResponseEntity<ResponseMessage> deleteInstance(@PathVariable String projectId, @PathVariable String zone, @PathVariable String instanceName) {
        String message;
        try {
            GCEInstance instance = new GCEInstance(instanceName, zone);
            if (!cmd.deleteInstance(projectId, instance)) {
                message = "Instance deletion failed";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
            LOGGER.info("deleteInstance() : Instance deleted");
            message = "Instance deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Instance deletion failed with error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }
}