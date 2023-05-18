package net.jadedmc.turfwars.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtils {

    public static Vector getTrajectory2d(Location from, Location to) {
        return getTrajectory2d(from.toVector(), to.toVector());
    }
    public static Vector getTrajectory2d(Vector from, Vector to) {
        return to.clone().subtract(from).setY(0).normalize();
    }

}