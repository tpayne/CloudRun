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

package samples.cloudrun.GCESample.model.cmd;

public class deleteComputeInstanceParams {

    private String projectId;
    private String instanceName;
    private String zone;

    public String getProjectId() {
        return this.projectId;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public String getZone() {
        return this.zone;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }

    public void setZone(final String zone) {
        this.zone = zone;
    }
}