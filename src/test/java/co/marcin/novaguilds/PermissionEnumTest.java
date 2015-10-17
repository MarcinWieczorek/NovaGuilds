package co.marcin.novaguilds;

import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.util.StringUtils;
import org.junit.Test;

public class PermissionEnumTest {
	@Test
	public void testPermissionEnum() throws Exception {
		boolean passed = true;
		for(Commands commands : Commands.values()) {
			Permission permission = Permission.fromPath(commands.getPermission());

			if(permission == null) {
				if(passed) {
					System.out.println("Missing enums:");
				}

				System.out.println(StringUtils.replace(commands.getPermission().toUpperCase(), ".", "_"));
				passed = false;
			}
		}

		if(!passed) {
			throw new Exception("Found missing Permission enums!");
		}
	}
}
