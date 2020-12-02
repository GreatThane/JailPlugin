package me.thane.jailplugin.typeadapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public class LocationAdapter extends TypeAdapter<Location> {
    private Gson gson;

    public LocationAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        out.beginObject();
        out.name("world");
        gson.getAdapter(World.class).write(out, value.getWorld());
        out.name("x").value(value.getX());
        out.name("y").value(value.getY());
        out.name("z").value(value.getZ());
        out.name("pitch").value(value.getPitch());
        out.name("yaw").value(value.getYaw());
        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        World world = null;
        Double x = null;
        Double y = null;
        Double z = null;
        Float pitch = null;
        Float yaw = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NAME) {
                switch (in.nextName()) {
                    case "world":
                        world = gson.getAdapter(World.class).read(in);
                        break;
                    case "x":
                        x = in.nextDouble();
                        break;
                    case "y":
                        y = in.nextDouble();
                        break;
                    case "z":
                        z = in.nextDouble();
                        break;
                    case "pitch":
                        pitch = (float) in.nextDouble();
                        break;
                    case "yaw":
                        yaw = (float) in.nextDouble();
                        break;
                }
            }
        }
        in.endObject();
        if (world == null || x == null || y == null || z == null || pitch == null || yaw == null) return null;
        return new Location(world, x, y, z, pitch, yaw);
    }
}
