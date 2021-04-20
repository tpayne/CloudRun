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

package samples.cloudrun.FileApp.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.nio.file.FileAlreadyExistsException;
import java.io.File;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Logger;
import java.util.logging.Level;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = Logger.getLogger(FileServiceImpl.class.getName());
    private final Path root = Paths.get("uploads");

    @Override
    public void init() {
    try {
            LOGGER.log(Level.FINE, "init(): Create directory=\"{0}\"",
                root.toString());
            if (!Files.exists(root)) {
                Files.createDirectory(root);
            }
        } catch (FileAlreadyExistsException e) {
        } catch (IOException e) {
            throw new RuntimeException("The root upload directory '"+root.toString()+"' could not be created");
        }
    }

    @Override
    public void save(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Selected file is empty or a directory");
            }
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException("The file specified already exists on the server");
        } catch (Exception e) {
            throw new RuntimeException("An error occurred saving a file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("An error occurred loading the file");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred loading the files");
        }
    }
}