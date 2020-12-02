package me.thane.jailplugin.typeadapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.util.UUID;

public class WorldAdapter extends TypeAdapter<World> {
    @Override
    public void write(JsonWriter out, World value) throws IOException {
        out.value(value.getUID().toString());
    }

    @Override
    public World read(JsonReader in) throws IOException {
        return Bukkit.getServer().getWorld(UUID.fromString(in.nextString()));
    }
}
