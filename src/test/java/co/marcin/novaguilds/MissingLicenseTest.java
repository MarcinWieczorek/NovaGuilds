/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2018 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds;

import co.marcin.novaguilds.util.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MissingLicenseTest {
    @Test
    public void testMissingLicense() throws IOException {
        List<File> files = IOUtils.listFilesRecursively(new File("./src"));
        boolean missing = false;

        for(File file : files) {
            if(!file.getName().endsWith(".java")) {
                continue;
            }

            String content = IOUtils.read(file);

            if(!content.startsWith("/*")) {
                missing = true;
                System.out.println(file.getAbsolutePath());
            }
        }

        if(missing) {
            throw new IllegalArgumentException("There are missing license headers!");
        }
    }
}
