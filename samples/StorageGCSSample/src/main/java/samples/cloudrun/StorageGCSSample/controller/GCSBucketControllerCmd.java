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

package samples.cloudrun.StorageGCSSample.controller;

import samples.cloudrun.StorageGCSSample.service.GCSBucketService;
import samples.cloudrun.StorageGCSSample.model.GCSBucket;
import samples.cloudrun.StorageGCSSample.model.cmd.createBucketParams;

import samples.cloudrun.StorageGCSSample.message.ResponseMessage;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/bucket")
public class GCSBucketControllerCmd {

    private final GCSBucketService service = GCSBucketService.getInstance();
    private static final Logger LOGGER = Logger.getLogger(GCSBucketControllerCmd.class.getName());

    @GetMapping("/version")
    public String versionApp() {
        return String.format("<h2>Version 1.0</h2>");
    } 

    @GetMapping("/list")
    public Map<String,Object> listAll() {
        Map<String,Object> response = new HashMap<String, Object>();
        response.put("List of created buckets", service.getBuckets());
        return response;
    } 

    @PostMapping(path= "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> 
        createBucket(
            @RequestHeader(name = "X-COM-PERSIST", required = false) String headerPersist,
            @RequestHeader(name = "X-COM-LOCATION", defaultValue = "USA") String headerLocation,
            @RequestBody createBucketParams params) {

        String message = "";
        try {
            // Instantiates a client
            Storage storage = StorageOptions.newBuilder().setProjectId(params.getProjectId()).build().getService();
            Bucket gcsBucket = storage.create(BucketInfo.newBuilder(params.getBucketName()).build());

            GCSBucket bucket = new GCSBucket(gcsBucket.getName(),gcsBucket.getSelfLink());
            service.getBuckets().put(gcsBucket.getName(),bucket);

            message = "Bucket created successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch(Exception e) {
            message = "Bucket creation failed with error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }  
    }
}