package online.nasgar.timedrankup;

import java.io.File;
import lombok.Getter;
import lombok.SneakyThrows;
import online.nasgar.timedrankup.commands.SeeNextRankupCommand;
import online.nasgar.timedrankup.commands.SeeRankupCommand;
import online.nasgar.timedrankup.listeners.JoinListener;
import online.nasgar.timedrankup.placeholders.NyaPlaceholders;
import online.nasgar.timedrankup.rank.RankManager;
import online.nasgar.timedrankup.tasks.RankCheckerTask;
import online.nasgar.timedrankup.user.MySQL;
import online.nasgar.timedrankup.user.UserManager;
import online.nasgar.timedrankup.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import me.yushust.message.MessageHandler;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import me.yushust.message.source.MessageSource;
import me.yushust.message.source.MessageSourceDecorator;
import online.nasgar.timedrankup.nmessage.UserLinguist;
import online.nasgar.timedrankup.nmessage.UserMessageSender;
import online.nasgar.timedrankup.utils.MessageUtil;
import org.bukkit.entity.Player;

@Getter
public final class TimedRankup extends JavaPlugin {

    @Getter
    private static TimedRankup instance;

    private MySQL mySQL;
    private RankManager rankManager;
    private UserManager userManager;
    private NyaPlaceholders nyaPlaceholders;
    private MessageHandler messageHandler;

    @Override
    public void onEnable() {
        instance = this;

        long startTime = System.currentTimeMillis();
        saveDefaultConfig();

        log("&7Initializing languages...");
        loadLanguages();

        log("&7Loading &b" + getDescription().getName() + " v " + getDescription().getVersion() + " &7by " + getDescription().getAuthors());

        mySQL = new MySQL(this);
        mySQL.setHost(getConfig().getString("MySQL.Host"));
        mySQL.setPort(getConfig().getString("MySQL.Port"));
        mySQL.setUsername(getConfig().getString("MySQL.Username"));
        mySQL.setPassword(getConfig().getString("MySQL.Password"));
        mySQL.setDatabase(getConfig().getString("MySQL.Database"));
        mySQL.connect();

        log("&7Initializing ranks...");
        rankManager = new RankManager(this);

        log("&7Initializing user data...");
        userManager = new UserManager();

        log("&7Initializing data task...");
        new RankCheckerTask(this).runTaskTimer(this, 0L, 20L);

        log("&7Initializing listeners...");
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);

        log("&7Initializing placeholders...");
        nyaPlaceholders = new NyaPlaceholders(this);
        nyaPlaceholders.register();

        log("&7Initializing commands...");
        getCommand("rankup").setExecutor(new SeeRankupCommand(this));
        getCommand("nextrankup").setExecutor(new SeeNextRankupCommand(this));

        log("&7Plugin loaded successfully in &b" + TimeUtils.formatTimeWithMillis(Duration.ofMillis(System.currentTimeMillis() - startTime)) + "&7...");
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        log("Saving data before stopping.");
        userManager.getUsers().forEach((uuid, user) -> {
            log("Saving &b" + user.getAsPlayer().getName() + " &7data.");
            try {
                user.save();
                log("Saved &b" + user.getAsPlayer().getName() + " &7data.");
            } catch (Exception e) {
                e.printStackTrace();
                log("Data from &b" + user.getAsPlayer().getName() + " &7could not be saved.");
            }
        });
        log("Saved data.");

        mySQL.getConnection().close();

        nyaPlaceholders.unregister();
    }

    private void loadLanguages() {
        MessageSourceDecorator messageSourceDecorator = MessageSourceDecorator
                .decorate(BukkitMessageAdapt
                        .newYamlSource(this, new File(getDataFolder(), "languages")));

        MessageSource messageSource = messageSourceDecorator
                .addFallbackLanguage("en")
                .addFallbackLanguage("es").get();

        this.loadFiles("languages/lang_en.yml", "languages/lang_es.yml");

        this.messageHandler = MessageHandler.of(
                messageSource,
                config -> {
                    config.addInterceptor(MessageUtil::translate);

                    config.specify(Player.class)
                            .setMessageSender(new UserMessageSender())
                            .setLinguist(new UserLinguist());
                }
        );
    }

    private void loadFiles(String... files) {
        for (String name : files) {
            if (this.getResource(name) != null) {
                this.saveResource(name, false);
            }
        }
    }

    public void log(String s) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bTimedRankup &8> &7" + s));
    }
}
