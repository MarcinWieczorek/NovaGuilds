package co.marcin.novaguilds;

import co.marcin.novaguilds.util.VersionUtils;
import org.junit.Test;

public class VersionTest {
	@Test
	public void testVersionUtils() throws Exception {

		System.out.println("Current version: " + VersionUtils.buildCurrent);
		System.out.println("Dev version: " + VersionUtils.buildDev);
		System.out.println("Stable version: " + VersionUtils.buildLatest);

		if(VersionUtils.buildDev == 0 || VersionUtils.buildLatest == 0) {
			throw new Exception("Could not access remote build numbers!");
		}

		if(VersionUtils.buildCurrent <= 0 || VersionUtils.buildDev < 0 || VersionUtils.buildLatest < 0) {
			throw new Exception("Build number cannot be smaller or equal 0!");
		}

		if(VersionUtils.buildCurrent > VersionUtils.buildDev + 1) {
			throw new Exception("Current build cannot be more than 1 bigger than latest the dev!");
		}
	}
}
