/*
 * Copyright (C) 2022 Franco Venezia @ www.ffs.it
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package REVOsetup;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class OSenv {

    String OSbasePath;
    String OSslash;

    public OSenv() {
        String slash = "/";
        this.OSbasePath = System.getProperty("user.dir");
//        System.out.println("Working Directory = " + systemBasepath);
        if (this.OSbasePath.contains("\\")) {
            slash = "\\";
        } else {
            slash = "/";
        }
        System.out.println("slash = " + slash);
        this.OSslash = slash;
    }

    public String normalizePath(String dirtyPath) {
        String cleanPath = dirtyPath;
        if (this.OSbasePath.contains("\\")) {
            cleanPath = cleanPath.replace("/", "\\");
        } else {
            cleanPath = cleanPath.replace("\\", "/");
        }

        return cleanPath;
    }

    public String getOSbasePath() {
        return OSbasePath;
    }

    public String getOSslash() {
        return OSslash;
    }

}
