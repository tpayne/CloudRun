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

public class DefaultValues {
    // Source image values...
    public static final String  SOURCE_IMAGE_PREFIX =
          "https://www.googleapis.com/compute/v1/projects/";
    public static final String  SOURCE_IMAGE_PATH =
          "ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20200529";
    public static final String  MACHINE_TYPE = "f1-micro";
    // Network driver values...
    public static final String  NETWORK_ACCESS_CONFIG = "External NAT";
    public static final String  NETWORK_INTERFACE_CONFIG = "ONE_TO_ONE_NAT";
}    