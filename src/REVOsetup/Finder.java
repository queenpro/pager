/*
 * SOFTWARE BY FFS RELEASED UNDER AGPL LICENSE.
 * REFER TO WWW.FFS.IT AND INFO@FFS.IT FOR INFO.
 * Author: Franco Venezia
  
    Copyright (C) <2019>  <Franco Venezia @ ffs.it>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package REVOsetup;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
 

 
 public   class Finder
        extends SimpleFileVisitor<Path> {

        private final PathMatcher matcher;
        private int numMatches = 0;
            EVOpagerParams myParams;
Settings mySettings;

    private ErrorLogger el = new ErrorLogger(myParams, mySettings);

    public Finder(String pattern, EVOpagerParams myParams, Settings mySettings) {
        matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + pattern);

        this.myParams = myParams;
        this.mySettings = mySettings;

    }

        // Compares the glob pattern against
    // the file or directory name.
    void find(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            numMatches++;
            System.out.println(file);

            el.log("Finder", file.toString());
        }
    }

        // Prints the total number of
    // matches to standard out.
    void done() {
        System.out.println("Matched: "
                + numMatches);
        el.log("Finder", "Matched: " + numMatches);

    }

        // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attrs) {
        find(file);
        return CONTINUE;
    }

        // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attrs) {
        find(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
            IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }

    static void usage() {
        System.err.println("java Find <path>"
                + " -name \"<glob_pattern>\"");

        // System.exit(-1);
    }

    public void goFind(String[] args)
            throws IOException {
        el.log("Finder", "ARG 0:" + args[0]);
        el.log("Finder", "ARG 1:" + args[1]);
        el.log("Finder", "ARG 2:" + args[2]);

        System.out.println("ARG 0:" + args[0]);
        System.out.println("ARG 1:" + args[1]);
        System.out.println("ARG 2:" + args[2]);
        if (args.length < 3 || !args[1].equals("-name")) {
            usage();
        }

        Path startingDir = Paths.get(args[0]);
        String pattern = args[2];

        Finder finder = new Finder(pattern, myParams, mySettings);
        Files.walkFileTree(startingDir, finder);
        finder.done();
    }
}
