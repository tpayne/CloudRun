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

package samples.cloudrun.GCESample.model;

import samples.cloudrun.GCESample.model.DefaultValues;

import java.util.Optional;

public class GCEInstance {

    private String instanceName = "";

    private String zone = "";
    private String imageName = "";
    private String networkInterface = "";
    private String networkConfig = "";
    private String url = "";
    private String machineType = "";


    public GCEInstance(final String instanceName, final String zone) {
        this.instanceName = instanceName;
        this.zone = zone;
    }

    public GCEInstance(final String instanceName, final String zone,
                       final String imageName) {
        this.instanceName = instanceName;
        this.zone = zone;
        this.imageName = imageName;
    }

    public GCEInstance(final String instanceName, final String zone,
                       final String imageName, final String networkInterface,
                       final String networkConfig, final String machineType) {
        this.instanceName = instanceName;
        this.zone = zone;
        this.imageName = imageName;
        this.networkInterface = networkInterface;
        this.networkConfig = networkConfig;
        this.machineType = machineType;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getURL() {
        return url;
    }

    public String getZone() {
        return zone;
    }

    public String getImageName() {
        if (imageName == null || imageName.isEmpty()) {
            return DefaultValues.SOURCE_IMAGE_PATH;
        }
        return imageName;
    }

    public String getNetworkInterface() {
        if (networkInterface == null || networkInterface.isEmpty()) {
            return DefaultValues.NETWORK_INTERFACE_CONFIG;
        }
        return networkInterface;
    }

    public String getNetworkConfig() {
        if (networkConfig == null || networkConfig.isEmpty()) {
            return DefaultValues.NETWORK_ACCESS_CONFIG;
        }
        return networkConfig;        
    }

    public String getMachineType() {
        if (machineType == null || machineType.isEmpty()) {
            return DefaultValues.MACHINE_TYPE;
        }
        return machineType;        
    }

    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }

    public void setZone(final String zone) {
        this.zone = zone;
    }

    public void setImageName(final String imageName) {
        this.imageName = imageName;
    }

    public void setNetworkInterface(final String networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void setNetworkConfig(final String networkConfig) {
        this.networkConfig = networkConfig;
    }

    public void setMachineType(final String machineType) {
        this.url = url;
    }

    public void setURL(final String url) {
        this.machineType = machineType;
    }

    @Override
    public String toString() {
        return "\"GCEInstance [instanceName = \""+ getInstanceName() + 
                "\", zone = \"" + getZone() + 
                "\", imageName = \"" + getImageName() + 
                "\", machineType = \"" + getMachineType() + 
                "\", networkInterface = \"" + getNetworkInterface() + 
                "\", networkConfig = \"" + getNetworkConfig() + 
                "\", url = \"" + getURL() + 
                "\"]\"";
    }
}