package notifier.notifier;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class Notifier extends JavaPlugin{
    private static Notifier instance;
    boolean hourNotified = false;
    boolean tenMinutesNotified = false;
    boolean minuteNotified = false;
    public boolean disabled = false;

    public LocalTime shutDownTime;
    public LocalTime minutePrior;

    public void onEnable() {
        FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();
        int userHour = config.getInt("time.hour");
        int userMinute = config.getInt("time.minute");
        if (config.getInt("time.hour") > 23 || config.getInt("time.minute") > 59){ //Make sure time is actually valid. If the user puts a negative time...
            shutDownTime = LocalTime.of( 5, 30); //UTC to 11:30 MDT
        }
        else{ //is valid
            shutDownTime = LocalTime.of(userHour, userMinute);
        }
        minutePrior = LocalTime.of(shutDownTime.getHour(),shutDownTime.getMinute()-1);
        mainPlugin();
    }
    //Allows var check from CommandToggle.java
    public static Notifier getInstance() {return instance;}

    private void kickAllPlayers(){
        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                target.kickPlayer("Server Shut Down\n§4Th§eank §ayou §1for §dplay§5ing!\n§6ありがとう！"); //Server Shut Down Thank you for playing! ありがとう
        }
    }

    public void toggleStatus(){disabled = !disabled;}

    public void mainPlugin() {
        instance = this;
        Objects.requireNonNull(this.getCommand("Ntoggle")).setExecutor(new CommandToggle());

        BukkitScheduler scheduler = getServer().getScheduler();

        scheduler.scheduleSyncRepeatingTask(this, () -> {
            if(disabled){return;}

            LocalTime currentTime = LocalTime.now();
            long timeDifference = currentTime.until(shutDownTime, ChronoUnit.MINUTES);

            //Hour Warning
            if(timeDifference == 59 && !hourNotified){
                getServer().broadcastMessage("§a1 Hour until server shutdown");
                hourNotified = true;
            }
            //10 Minute Warning
            if(currentTime.until(shutDownTime, ChronoUnit.MINUTES) == 10 && !tenMinutesNotified){
                getServer().broadcastMessage("§e10 Minutes until server shutdown");
                tenMinutesNotified = true;
            }
            //1 Minute Warning
            if(currentTime.until(shutDownTime, ChronoUnit.MINUTES) == 1 && !minuteNotified){
                getServer().broadcastMessage("§e1 Minute until server shutdown");
                minuteNotified = true;
            }
            //Final warning
            if(currentTime.getHour() == shutDownTime.getHour() && currentTime.getMinute() == minutePrior.getMinute() && currentTime.getSecond() >=49){
                getServer().broadcastMessage("§l§cSHUTDOWN IN " + currentTime.until(shutDownTime, ChronoUnit.SECONDS));
            }
            //We want to kick one second before the shutdown time due to how "Thank you for playing, server shutdown" looks better than "disconnected".
            if(currentTime.getHour() == shutDownTime.getHour() && currentTime.getMinute() == minutePrior.getMinute() && currentTime.getSecond() == 59){
                kickAllPlayers();
            }
        }, 0L, 20L);
    }
}

