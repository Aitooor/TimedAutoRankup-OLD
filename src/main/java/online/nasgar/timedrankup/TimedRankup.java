package online.nasgar.timedrankup;

import lombok.Getter;
import lombok.SneakyThrows;
import me.clip.placeholderapi.PlaceholderAPI;
import me.yushust.message.MessageHandler;
import me.yushust.message.MessageProvider;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import me.yushust.message.source.MessageSourceDecorator;
import online.nasgar.timedrankup.commands.SeeNextRankupCommand;
import online.nasgar.timedrankup.commands.SeeRankupCommand;
import online.nasgar.timedrankup.listeners.JoinListener;
import online.nasgar.timedrankup.nms.LocaleCraftBukkit;
import online.nasgar.timedrankup.nms.LocaleSpigot;
import online.nasgar.timedrankup.nms.iLocale;
import online.nasgar.timedrankup.placeholders.NyaPlaceholders;
import online.nasgar.timedrankup.rank.RankManager;
import online.nasgar.timedrankup.tasks.RankCheckerTask;
import online.nasgar.timedrankup.user.MySQL;
import online.nasgar.timedrankup.user.UserManager;
import online.nasgar.timedrankup.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Duration;
import java.util.Objects;

@Getter
public final class TimedRankup extends JavaPlugin {

    private MySQL mySQL;
    private RankManager rankManager;
    private UserManager userManager;
    private NyaPlaceholders nyaPlaceholders;
    private MessageHandler messageHandler;
    private iLocale locale;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        saveDefaultConfig();

        log("&7Loading &bTimedRankup v" + getDescription().getVersion() + " " + getDescription().getAuthors());

        mySQL = new MySQL(this);
        mySQL.setHost(getConfig().getString("MySQL.Host"));
        mySQL.setPort(getConfig().getString("MySQL.Port"));
        mySQL.setUsername(getConfig().getString("MySQL.Username"));
        mySQL.setPassword(getConfig().getString("MySQL.Password"));
        mySQL.setDatabase(getConfig().getString("MySQL.Database"));
        mySQL.connect();

        this.loadMessages();
        messageHandler = MessageHandler.of(
                MessageSourceDecorator
                        .decorate(BukkitMessageAdapt.newYamlSource(
                                this, new File(getDataFolder(), "lang")
                        ))
                        .addFallbackLanguage("en")
                        .get(),
                configurationHandle -> {
                    configurationHandle.addInterceptor(message ->
                            ChatColor.translateAlternateColorCodes(
                                    '&', message
                            ));

                    configurationHandle.specify(CommandSender.class)
                            .setLinguist(sender -> {
                                if (sender instanceof Player) {
                                    return ((Player) sender).getLocale()
                                            .split("_")[0];
                                } else {
                                    return "es";
                                }
                            })
                            .setMessageSender((sender, mode, message) -> {
                                if (mode.equals("placeholder") &&
                                        sender instanceof Player) {
                                    sender.sendMessage(PlaceholderAPI.setPlaceholders(
                                            (Player) sender,
                                            message
                                    ));
                                } else {
                                    sender.sendMessage(message);
                                }
                            });
                });

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
        Objects.requireNonNull(getCommand("rankup")).setExecutor(new SeeRankupCommand(this));
        Objects.requireNonNull(getCommand("nextrankup")).setExecutor(new SeeNextRankupCommand(this));

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

    private void loadMessages() {
        setupLocale();

        saveResource("lang/lang_en.yml", false);
        saveResource("lang/lang_es.yml", false);

        MessageProvider messageProvider = MessageProvider
                .create(
                        MessageSourceDecorator
                                .decorate(BukkitMessageAdapt.newYamlSource(this, "lang_%lang%.yml"))
                                .addFallbackLanguage("en")
                                .addFallbackLanguage("es")
                                .get(),
                        config -> {
                            config.specify(Player.class)
                                    .setLinguist(player -> {
                                        try {
                                            return locale.getLocale(player).split("_")[0];
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                                    .setMessageSender((sender, mode, message) -> {
                                        if (mode.equals("placeholder") && sender instanceof Player) {
                                            sender.sendMessage(PlaceholderAPI.setPlaceholders(
                                                    sender,
                                                    message
                                            ));
                                        } else {
                                            sender.sendMessage(message);
                                        }
                                    });
                            config.specify(CommandSender.class)
                                    .setLinguist(commandSender -> "en")
                                    .setMessageSender((sender, mode, message) -> sender.sendMessage(message));
                            config.addInterceptor(s -> ChatColor.translateAlternateColorCodes('&', s));
                        }
                );

        messageHandler = MessageHandler.of(messageProvider);
    }

    private void setupLocale() {
        double version = 0;
        try {
            version = Double.parseDouble(Bukkit.getBukkitVersion().split("-")[0].replaceFirst("1\\.", ""));
        } catch (Exception ignored) {}

        if (version > 8.8) // > 1.8.8
            locale = new LocaleSpigot();
        else
            locale = new LocaleCraftBukkit();
    }

    public void log(String s) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bTimedRankup &8> &7" + s));
    }
}
