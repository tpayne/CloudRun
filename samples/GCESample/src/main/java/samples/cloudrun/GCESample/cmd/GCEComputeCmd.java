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

package samples.cloudrun.GCESample.cmd;

import samples.cloudrun.GCESample.model.DefaultValues;
import samples.cloudrun.GCESample.model.GCEInstance;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.ComputeScopes;
import com.google.api.services.compute.model.AccessConfig;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.AttachedDiskInitializeParams;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.Metadata;
import com.google.api.services.compute.model.NetworkInterface;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.ServiceAccount;
import com.google.api.services.compute.model.Zone;
import com.google.api.services.compute.model.ZoneList;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.lang.RuntimeException;

import java.util.logging.Logger;
import java.util.logging.Level;

public class GCEComputeCmd {

    private static final Logger LOGGER = Logger.getLogger(GCEComputeCmd.class.getName());

    private static HttpTransport httpTransport = null;
    private static JsonFactory jsonFactoryInstance = null;
    private static Compute compute = null;
    
    private static final String APPLICATION_NAME = "";
    private static final long OPERATION_TIMEOUT_MILLIS = 60 * 1000;

    public GCEComputeCmd() {
        try {
            init();
        } catch(Exception e) {
        }
    }

    // Method to initialise the class
    private void init() 
        throws IOException {
        try {
            // Initialise the class...
            if (httpTransport != null && jsonFactoryInstance != null) {
                return;
            }

            jsonFactoryInstance = JacksonFactory.getDefaultInstance();
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // Authenticate using Google Application Default Credentials.
            GoogleCredentials credential = GoogleCredentials.getApplicationDefault();
            if (credential.createScopedRequired()) {
                List<String> scopes = new ArrayList<>();
                // Set Google Cloud Storage scope to Full Control.
                scopes.add(ComputeScopes.DEVSTORAGE_FULL_CONTROL);
                // Set Google Compute Engine scope to Read-write.
                scopes.add(ComputeScopes.COMPUTE);
                credential = credential.createScoped(scopes);
            }

            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credential);

            // Create Compute Engine object for listing instances.
            compute = new Compute.Builder(httpTransport, jsonFactoryInstance, requestInitializer)
                          .setApplicationName(APPLICATION_NAME)
                          .build(); 
        } catch(Exception e) {
            LOGGER.severe("init() :"+e.getMessage());
            httpTransport = null;
            jsonFactoryInstance = null;
            compute = null;
            throw new IOException(e.getMessage());
        }
        return;       
    }

    private static Operation.Error blockUntilComplete(
        final String projectId,
        Compute compute, 
        Operation operation, 
        long timeout) 
        throws Exception {

        long start = System.currentTimeMillis();
        final long pollInterval = 5 * 1000;
        String zone = operation.getZone(); // null for global/regional operations
        if (zone != null) {
            String[] bits = zone.split("/");
            zone = bits[bits.length - 1];
        }
        String status = operation.getStatus();
        String opId = operation.getName();
        while (operation != null && !status.equals("DONE")) {
            Thread.sleep(pollInterval);
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed >= timeout) {
                throw new InterruptedException("Timed out waiting for operation to complete");
            }
            if (zone != null) {
                Compute.ZoneOperations.Get get = compute.zoneOperations().get(projectId, zone, opId);
                operation = get.execute();
            } else {
                Compute.GlobalOperations.Get get = compute.globalOperations().get(projectId, opId);
                operation = get.execute();
            }
            if (operation != null) {
                status = operation.getStatus();
            }
        }
        return operation == null ? null : operation.getError();
    }

    // Create an instance implementation
    private static Operation create(final String projectId, GCEInstance gceInstance) 
        throws RuntimeException {

        try {
            Instance instance = new Instance();
            instance.setName(gceInstance.getInstanceName());
            instance.setMachineType(
                String.format(
                    "https://www.googleapis.com/compute/v1/projects/%s/zones/%s/machineTypes/%s",
                    projectId, gceInstance.getZone(), gceInstance.getMachineType()));
            // Add Network Interface to be used by VM Instance.
            LOGGER.info("create() "+instance.getMachineType());
            NetworkInterface ifc = new NetworkInterface();
            ifc.setNetwork(
                String.format(
                    "https://www.googleapis.com/compute/v1/projects/%s/global/networks/default",
                    projectId));

            List<AccessConfig> configs = new ArrayList<>();
            AccessConfig config = new AccessConfig();
            config.setType(gceInstance.getNetworkInterface());
            config.setName(gceInstance.getNetworkConfig());
            configs.add(config);
            ifc.setAccessConfigs(configs);
            instance.setNetworkInterfaces(Collections.singletonList(ifc));

            // Add attached Persistent Disk to be used by VM Instance.
            AttachedDisk disk = new AttachedDisk();
            disk.setBoot(true);
            disk.setAutoDelete(true);
            disk.setType("PERSISTENT");
            AttachedDiskInitializeParams params = new AttachedDiskInitializeParams();
            // Assign the Persistent Disk the same name as the VM Instance.
            params.setDiskName(gceInstance.getInstanceName());
            // Specify the source operating system machine image to be used by the VM Instance.
            params.setSourceImage(DefaultValues.SOURCE_IMAGE_PREFIX + gceInstance.getImageName());
            // Specify the disk type as Standard Persistent Disk
            params.setDiskType(
                String.format(
                    "https://www.googleapis.com/compute/v1/projects/%s/zones/%s/diskTypes/pd-standard",
                    projectId, gceInstance.getZone()));
            disk.setInitializeParams(params);
            instance.setDisks(Collections.singletonList(disk));

            // Initialize the service account to be used by the VM Instance and set the API access scopes.
            ServiceAccount account = new ServiceAccount();
            account.setEmail("default");
            List<String> scopes = new ArrayList<>();
            scopes.add("https://www.googleapis.com/auth/devstorage.full_control");
            scopes.add("https://www.googleapis.com/auth/compute");
            account.setScopes(scopes);
            instance.setServiceAccounts(Collections.singletonList(account));

            // Optional - Add a startup script to be used by the VM Instance.
            // Metadata meta = new Metadata();
            // Metadata.Items item = new Metadata.Items();
            // item.setKey("startup-script-url");
            // If you put a script called "vm-startup.sh" in this Google Cloud Storage
            // bucket, it will execute on VM startup.  This assumes you've created a
            // bucket named the same as your PROJECT_ID.
            // For info on creating buckets see:
            // https://cloud.google.com/storage/docs/cloud-console#_creatingbuckets
            // item.setValue(String.format("gs://%s/vm-startup.sh", PROJECT_ID));
            // meta.setItems(Collections.singletonList(item));
            // instance.setMetadata(meta);

            Compute.Instances.Insert insert = compute.instances().insert(projectId, 
                                                                         gceInstance.getZone(), 
                                                                         instance);

            return insert.execute();

        } catch(Exception e) {
            LOGGER.severe("create() :"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // Delete an instance implementation
    private static Operation delete(final String projectId, GCEInstance gceInstance) 
        throws RuntimeException {

        try {
            Compute.Instances.Delete delete =
                compute.instances().delete(projectId, gceInstance.getZone(), 
                                           gceInstance.getInstanceName());
            return delete.execute();
        } catch(Exception e) {
            LOGGER.severe("delete() :"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // Get instance
    private Instance getInstance(final String projectId, final String region, final String instanceName) {
        try {
            Compute.Instances.List instances = compute.instances().list(projectId, region);
            InstanceList list = instances.execute();
            if (list.getItems() == null) {
                return null;
            } else {
                for (Instance instance : list.getItems()) {
                    if (instance.getName().equals(instanceName)) {
                        return instance;
                    }
                }
            }
            return null;
        } catch(Exception e) {
            LOGGER.severe("getInstance() :"+e.getMessage());
            return null;
        }
    }

    // Get instance
    private Map<String,List<Object>> list(final String projectId) {
        try {
            Compute.Zones.List zones = compute.zones().list(projectId);
            ZoneList list = zones.execute();
            if (list.getItems() == null) {
                return null;
            } else {
                Map<String,List<Object>> mmap = new HashMap<String,List<Object>>();
                for (Zone zone : list.getItems()) {
                    List<Object> insList = list(projectId,zone.getName());
                    if (insList != null) {
                        mmap.put(zone.getName(),insList);
                    }
                }   
                return mmap;
            }
        } catch(Exception e) {
            LOGGER.severe("list() :"+e.getMessage());
            return null;
        }
    }

    // Get instance
    private List<Object> list(final String projectId, final String region) {
        try {
            Compute.Instances.List instances = compute.instances().list(projectId, region);
            InstanceList list = instances.execute();
            if (list.getItems() == null) {
                return null;
            } else {
                List<Object> listInst = new ArrayList<Object>(list.getItems());
                return listInst;
            }
        } catch(Exception e) {
            LOGGER.severe("list() :"+e.getMessage());
            return null;
        }
    }

    // List all instances
    public List<Object> listInstances(final String projectId, final String region) {
        return(list(projectId,region));
    }

    // List all instances
    public Map<String,List<Object>> listInstances(final String projectId) {
        return(list(projectId));
    }

    // Describe instance
    public Instance describeInstance(final String projectId, final String region, final String instanceName) {
        return(getInstance(projectId,region,instanceName));
    }

    // Create an instance
    public boolean createInstance(final String projectId, GCEInstance gceInstance)
        throws RuntimeException {

        try {
            init();
            Operation op = create(projectId,gceInstance);
            Operation.Error error = blockUntilComplete(projectId, compute, 
                                                       op, OPERATION_TIMEOUT_MILLIS);
            if (error != null) {
                throw new RuntimeException(error.toPrettyString());
            }
            Instance gce = getInstance(projectId,gceInstance.getZone(),gceInstance.getInstanceName());
            if (gce == null) {
                return false;
            }
            LOGGER.info("createInstance() :"+gce.toPrettyString());
            return true;
        } catch(Exception e) {
            LOGGER.severe("createInstance() :"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // Delete an instance
    public boolean deleteInstance(final String projectId, GCEInstance gceInstance)
        throws RuntimeException {

        try {
            init();
            Operation op = delete(projectId,gceInstance);
            Operation.Error error = blockUntilComplete(projectId, compute, 
                                                       op, OPERATION_TIMEOUT_MILLIS);
            return (error == null);
        } catch(Exception e) {
            LOGGER.severe("deleteInstance() :"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
