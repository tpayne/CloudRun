/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samples.cloudrun.GCESample.controller;

import samples.cloudrun.GCESample.service.GCEInstanceService;
import samples.cloudrun.GCESample.model.GCEInstance;
import samples.cloudrun.GCESample.model.cmd.createComputeInstanceParams;
import samples.cloudrun.GCESample.model.cmd.deleteComputeInstanceParams;
import samples.cloudrun.GCESample.cmd.GCEComputeCmd;
import samples.cloudrun.GCESample.message.ResponseMessage;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.util.logging.Logger;
import java.util.logging.Level;

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
    public Map<String,Object> list() {
        Map<String,Object> response = new HashMap<String, Object>();
        response.put("List of created instances", service.getComputeInstances());
        return response;
    } 

    @GetMapping("/describe/{projectId:.+}/{zone:.+}/{instanceName:.+}")
    public Map<String,Object> describeInstanceURL(@PathVariable String projectId, 
                                               @PathVariable String zone,
                                               @PathVariable String instanceName) {    
        Map<String,Object> response = new HashMap<String, Object>();
        response.put("Instance:", cmd.describeInstance(projectId,zone,instanceName));
        return response;
    } 

    @GetMapping("/describe")
    public Map<String,Object> describeInstance(@RequestParam String projectId, 
                                               @RequestParam String zone,
                                               @RequestParam String instanceName) {
        Map<String,Object> response = new HashMap<String, Object>();
        response.put("Instance:", cmd.describeInstance(projectId,zone,instanceName));
        return response;
    } 

    @GetMapping("/listAll/{projectId:.+}/{zone:.+}")
    public Map<String,List<Object>> listAllURL(@PathVariable String projectId, 
                                               @PathVariable String zone) {
        Map<String,List<Object>> response = new HashMap<String, List<Object>>();
        response.put("List of all instances", cmd.listInstances(projectId,zone));
        return response;
    }

    @GetMapping("/listAll/{projectId:.+}")
    public Map<String,Map<String,List<Object>>> listAllURL(@PathVariable String projectId) {
        Map<String,Map<String,List<Object>>> response = new HashMap<String, Map<String,List<Object>>>();
        response.put("List of all instances", cmd.listInstances(projectId));
        return response;
    }

    @GetMapping("/listAll")
    public Map<String,List<Object>> listAll(@RequestParam String projectId, 
                                            @RequestParam String zone) {
        Map<String,List<Object>> response = new HashMap<String, List<Object>>();
        response.put("List of all instances", cmd.listInstances(projectId,zone));
        return response;
    } 

    @DeleteMapping("/{projectId:.+}/{zone:.+}/{instanceName:.+}")
    public ResponseEntity<ResponseMessage>
        deleteInstance(@PathVariable String projectId, 
                       @PathVariable String zone,
                       @PathVariable String instanceName) {

        String message = "";
        try {
            // Instantiates a client
            GCEInstance instance = new GCEInstance(instanceName,
                                                   zone);
            
            if (!cmd.deleteInstance(projectId,instance)) {
                message = "Instance deletion failed";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));                
            }

            service.getComputeInstances().remove(instance.getInstanceName());
            LOGGER.info("deleteInstance() : Instance deleted");
            message = "Instance deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch(Exception e) {
            LOGGER.severe("deleteInstance() :"+e.getMessage());
            message = "Instance deleted failed with error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }  
    }            
    
    @PostMapping(path= "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> 
        deleteInstance(
            @RequestHeader(name = "X-COM-PERSIST", required = false) String headerPersist,
            @RequestHeader(name = "X-COM-LOCATION", defaultValue = "USA") String headerLocation,
            @RequestBody deleteComputeInstanceParams params) {

        String message = "";
        try {
            // Instantiates a client
            GCEInstance instance = new GCEInstance(params.getInstanceName(),
                                                   params.getZone());
            
            if (!cmd.deleteInstance(params.getProjectId(),instance)) {
                message = "Instance deletion failed";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));                
            }

            service.getComputeInstances().remove(instance.getInstanceName());
            LOGGER.info("deleteInstance() : Instance deleted");
            message = "Instance deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch(Exception e) {
            LOGGER.severe("deleteInstance() :"+e.getMessage());
            message = "Instance deleted failed with error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }  
    }

    @PostMapping(path= "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> 
        createInstance(
            @RequestHeader(name = "X-COM-PERSIST", required = false) String headerPersist,
            @RequestHeader(name = "X-COM-LOCATION", defaultValue = "USA") String headerLocation,
            @RequestBody createComputeInstanceParams params) {

        String message = "";
        try {
            GCEInstance gceins = new GCEInstance(params.getInstanceName(),
                                                   params.getZone(),
                                                   params.getImageSource(),
                                                   params.getNetworkInterface(),
                                                   params.getNetworkConfig(),
                                                   params.getMachineType());
            LOGGER.info("createInstance() :"+params.getProjectId()+" "+gceins.toString());
            if (!cmd.createInstance(params.getProjectId(),gceins)) {
                message = "Instance creation failed";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));                
            }

            service.getComputeInstances().put(gceins.getInstanceName(),gceins);
            LOGGER.info("createInstance() : Instance created");
            message = "Instance created successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch(Exception e) {
            LOGGER.severe("createInstance() :"+e.getMessage());
            message = "Instance creation failed with error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }  
    }
}