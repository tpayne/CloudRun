package samples.cloudrun.GCESample.cmd;

import samples.cloudrun.GCESample.model.DefaultValues;
import samples.cloudrun.GCESample.model.GCEInstance;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.ComputeScopes;
import com.google.api.services.compute.model.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class GCEComputeCmd {

    private static final Logger LOGGER = Logger.getLogger(GCEComputeCmd.class.getName());

    private static HttpTransport httpTransport = null;
    private static GsonFactory jsonFactoryInstance = null;
    private static Compute compute = null;

    private static final String APPLICATION_NAME = "";
    private static final long OPERATION_TIMEOUT_MILLIS = 60 * 1000;

    public GCEComputeCmd() {
        try {
            init();
        } catch (Exception e) {
            LOGGER.severe("Initialization failed: " + e.getMessage());
        }
    }

    private void init() throws IOException {
        try {
            if (httpTransport != null && jsonFactoryInstance != null && compute != null) {
                return;
            }

            if (jsonFactoryInstance == null) {
                jsonFactoryInstance = GsonFactory.getDefaultInstance();
            }
            if (httpTransport == null) {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            }

            if (compute == null) {
                GoogleCredentials credential = GoogleCredentials.getApplicationDefault();
                if (credential.createScopedRequired()) {
                    List<String> scopes = Arrays.asList(ComputeScopes.DEVSTORAGE_FULL_CONTROL,
                    ComputeScopes.COMPUTE);
                    credential = credential.createScoped(scopes);
                }

                HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credential);
                compute = new Compute.Builder(httpTransport, jsonFactoryInstance, requestInitializer)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize Compute Engine client: " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    private static Operation.Error blockUntilComplete(final String projectId,
            Compute compute, Operation operation, long timeout) throws Exception {
        long start = System.currentTimeMillis();
        final long pollInterval = 5 * 1000;
        String zone = operation.getZone();
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
            operation = (zone != null) ? 
                compute.zoneOperations().get(projectId, zone, opId).execute() :
                compute.globalOperations().get(projectId, opId).execute();
            if (operation != null) {
                status = operation.getStatus();
            }
        }
        return operation == null ? null : operation.getError();
    }

    private static Operation create(final String projectId, GCEInstance gceInstance) throws RuntimeException {
        try {
            Instance instance = new Instance();
            instance.setName(gceInstance.getInstanceName());
            instance.setMachineType(
                    String.format("https://www.googleapis.com/compute/v1/projects/%s/zones/%s/machineTypes/%s",
                            projectId, gceInstance.getZone(), gceInstance.getMachineType()));
            NetworkInterface ifc = new NetworkInterface();
            ifc.setNetwork(
                    String.format("https://www.googleapis.com/compute/v1/projects/%s/global/networks/default",
                            projectId));

            List<AccessConfig> configs = new ArrayList<>();
            AccessConfig config = new AccessConfig();
            config.setType(gceInstance.getNetworkInterface());
            config.setName(gceInstance.getNetworkConfig());
            configs.add(config);
            ifc.setAccessConfigs(configs);
            instance.setNetworkInterfaces(Collections.singletonList(ifc));

            AttachedDisk disk = new AttachedDisk();
            disk.setBoot(true);
            disk.setAutoDelete(true);
            disk.setType("PERSISTENT");
            AttachedDiskInitializeParams params = new AttachedDiskInitializeParams();
            params.setDiskName(gceInstance.getInstanceName());
            params.setSourceImage(DefaultValues.SOURCE_IMAGE_PREFIX + gceInstance.getImageName());
            params.setDiskType(
                    String.format("https://www.googleapis.com/compute/v1/projects/%s/zones/%s/diskTypes/pd-standard",
                            projectId, gceInstance.getZone()));
            disk.setInitializeParams(params);
            instance.setDisks(Collections.singletonList(disk));

            ServiceAccount account = new ServiceAccount();
            account.setEmail("default");
            List<String> scopes = Arrays.asList(
                    "https://www.googleapis.com/auth/devstorage.full_control",
                    "https://www.googleapis.com/auth/compute");
            account.setScopes(scopes);
            instance.setServiceAccounts(Collections.singletonList(account));

            return compute.instances().insert(projectId, gceInstance.getZone(), instance).execute();

        } catch (Exception e) {
            LOGGER.severe("Error during instance creation: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Operation delete(final String projectId, GCEInstance gceInstance) throws RuntimeException {
        try {
            return compute.instances().delete(projectId, gceInstance.getZone(), gceInstance.getInstanceName()).execute();
        } catch (Exception e) {
            LOGGER.severe("Error during instance deletion: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private Instance getInstance(final String projectId, final String region, final String instanceName) {
        try {
            InstanceList list = compute.instances().list(projectId, region).execute();
            for (Instance instance : list.getItems()) {
                if (instance.getName().equals(instanceName)) {
                    return instance;
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.severe("Error fetching instance: " + e.getMessage());
            return null;
        }
    }

    public List<Object> listInstances(final String projectId, final String region) {
        return (list(projectId, region));
    }

    public boolean createInstance(final String projectId, GCEInstance gceInstance) throws RuntimeException {
        try {
            init();
            Operation op = create(projectId, gceInstance);
            Operation.Error error = blockUntilComplete(projectId, compute, op, OPERATION_TIMEOUT_MILLIS);
            if (error != null) {
                throw new RuntimeException(error.toPrettyString());
            }
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error during instance creation: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean deleteInstance(final String projectId, GCEInstance gceInstance) throws RuntimeException {
        try {
            init();
            Operation op = delete(projectId, gceInstance);
            Operation.Error error = blockUntilComplete(projectId, compute, op, OPERATION_TIMEOUT_MILLIS);
            return error == null;
        } catch (Exception e) {
            LOGGER.severe("Error during instance deletion: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}