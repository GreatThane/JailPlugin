package me.thane.jailplugin.typeadapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class JailTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> clazz = (Class<T>) type.getRawType();

        if (World.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new WorldAdapter();
        } else if (OfflinePlayer.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new PlayerAdapter();
        } else if (Location.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new LocationAdapter(gson);
        } return null;
    }
}
