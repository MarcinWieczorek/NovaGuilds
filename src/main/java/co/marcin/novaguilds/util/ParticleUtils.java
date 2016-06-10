package co.marcin.novaguilds.util;

import com.darkblade12.particleeffect.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ParticleUtils {
	/**
	 * Creates supernova effect where an entity exists
	 *
	 * @param entity target entity
	 */
	public static void createSuperNova(Entity entity) {
		createSuperNova(entity.getLocation());
	}

	/**
	 * Gets circle vectors
	 *
	 * @param radius    radius of a circle
	 * @param precision precision (amount of vectors)
	 * @return list of vectors
	 */
	public static List<Vector> getCircleVectors(int radius, int precision) {
		List<Vector> list = new ArrayList<>();

		for(int i = 0; i < precision; i++) {
			double p1 = (i * Math.PI) / (precision / 2);
			double p2 = (((i == 0) ? precision : i - 1) * Math.PI) / (precision / 2);

			double x1 = Math.cos(p1) * radius;
			double x2 = Math.cos(p2) * radius;
			double z1 = Math.sin(p1) * radius;
			double z2 = Math.sin(p2) * radius;

			Vector vec = new Vector(x2 - x1, 0, z2 - z1);
			list.add(vec);
		}

		return list;
	}

	/**
	 * Creates supernova effect
	 *
	 * @param location target location
	 */
	public static void createSuperNova(Location location) {
		float speed = 1F;
		double range = 15D;

		location = location.clone();
		location.add(0, 0.5, 0);

		for(Vector vector : getCircleVectors(15, 100)) {
			ParticleEffect.SNOW_SHOVEL.display(vector, speed, location, range);
		}
	}
}
