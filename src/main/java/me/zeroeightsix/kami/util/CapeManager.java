package me.zeroeightsix.kami.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CapeManager {
    private static final String users = "https://pastebin.com/raw/VWQai3Zb";
    private static HashMap<String, Boolean> capeUsers;

    public CapeManager() {
        capeUsers = new HashMap();
    }

    public void initializeCapes() {
        this.getFromPastebin(users).forEach(uuid -> capeUsers.put((String)uuid, true));
    }

    private List<String> getFromPastebin(String urlString) {
        URL url;
        BufferedReader bufferedReader;
        try {
            url = new URL(urlString);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
        ArrayList<String> uuidList = new ArrayList<String>();
        do {
            String line;
            try {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<String>();
            }
            uuidList.add(line);
        } while (true);
        try {
            bufferedReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
        return uuidList;
    }

    public static boolean hasCape(UUID uuid) {
        return capeUsers.containsKey(uuid.toString());
    }

    public static boolean isOg(UUID uuid) {
        if (capeUsers.containsKey(uuid.toString())) {
            return capeUsers.get(uuid.toString());
        }
        return false;
    }
}

