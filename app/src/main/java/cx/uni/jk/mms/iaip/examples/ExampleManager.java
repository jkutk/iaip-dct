/**
 *    Copyright (C) 2015  Peter Plaimer <dct-tool@tk.jku.at>
 *
 *    This file is part of the program
 *    InterActive Image Processing / Discrete Cosine Transformation (DCT) 
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cx.uni.jk.mms.iaip.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * Provider for a list of {@link Path} to the example images 
 */
public class ExampleManager {
	private static final String RESOURCES_PATH = "resources";

	private static final Object staticLock = new Object();

	private static Logger logger = Logger.getGlobal();

	private static ExampleManager instance;

	public static ExampleManager getInstance() {
		/**
		 * TODO: for some reason this is not synchronizing correctly.
		 * practically harmless.
		 */
		synchronized (staticLock) {
			if (ExampleManager.instance == null) {
				ExampleManager.instance = new ExampleManager();
			}
			return ExampleManager.instance;
		}
	}

	private List<Path> paths = new ArrayList<>();

	/**
	 * holds a list of all example images.
	 */
	private ExampleManager() {
		CodeSource src = this.getClass().getProtectionDomain().getCodeSource();

		if (src == null) {
			/** no examples available */
			logger.fine("I am running in an unknown file system, no examples available.");
		} else {
			final URL location = src.getLocation();
			logger.fine("example class location: " + location.toString());

			try {
				FileSystem fs;
				Path resourcePath;
				if (location.toString().toLowerCase().endsWith(".jar")) {
					logger.fine("I am in a jar.");

					fs = FileSystems.newFileSystem(Paths.get(location.toURI()), this.getClass().getClassLoader());
					resourcePath = fs.getPath(RESOURCES_PATH);
				} else {
					logger.fine("I am running in a file system.");
					fs = FileSystems.getDefault();
					resourcePath = fs.getPath(src.getLocation().getPath(), RESOURCES_PATH);
				}

				final Path xiDir = fs.getPath(resourcePath.toString(), "examples");
				logger.fine("searching examples in " + xiDir.toString());
				Files.walkFileTree(xiDir, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, this.visitor);
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Collections.sort(this.paths);
		}
	}

	public List<Path> getPaths() {
		return this.paths;
	}

	private FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

		@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
			logger.fine(String.format("visitFile: %s ", path.toUri()));
			ExampleManager.this.paths.add(path);
			return FileVisitResult.CONTINUE;
		}

	};
}