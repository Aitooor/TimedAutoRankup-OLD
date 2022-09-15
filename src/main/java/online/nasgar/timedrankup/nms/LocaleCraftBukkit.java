package online.nasgar.timedrankup.nms;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Locale;

public class LocaleCraftBukkit implements iLocale {

    private Method playerLocaleMethod;

    @Override
    public String getLocale(Player p) throws Exception {
        if (playerLocaleMethod == null)
            playerLocaleMethod = p.getClass().getDeclaredMethod("getLocale");

        return ((Locale) playerLocaleMethod.invoke(p)).getLanguage();
    }
}
