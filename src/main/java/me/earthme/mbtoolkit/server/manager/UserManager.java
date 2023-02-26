package me.earthme.mbtoolkit.server.manager;

import me.earthme.mbtoolkit.server.data.UserDataEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserManager {
    private static final Logger logger = LogManager.getLogger();
    private static final Set<UserDataEntry> userData = ConcurrentHashMap.newKeySet();
    private static final File userDataFolder = new File("user_datas");

    static {
        userDataFolder.mkdirs();
    }

    public static void readAllFromFile(){
        final File[] files = userDataFolder.listFiles();
        if (files != null && files.length > 0){
            logger.info("Loading user data");
            userData.addAll(Arrays.stream(files).parallel().map(UserDataEntry::readFromFile).collect(Collectors.toList()));
        }
        logger.info("Loaded {} user data",userData.size());
    }

    public static void saveAll(){
        userData.parallelStream().filter(UserDataEntry::isDirty).forEach(userDataEntry -> userDataEntry.writeToFile(userDataFolder,userDataEntry.getUserUUID().toString()+".json"));
    }

    public static void addUser(@NotNull UserDataEntry entry){
        entry.setDirty(true);
        userData.add(entry);
    }

    public static @NotNull UserDataEntry addUser(){
        final UserDataEntry userDataEntry = new UserDataEntry(UUID.randomUUID(),false);
        userDataEntry.setDirty(true);
        userData.add(userDataEntry);
        return userDataEntry;
    }
}
