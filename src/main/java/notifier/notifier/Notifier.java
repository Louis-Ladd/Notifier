package notifier.notifier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public final class Notifier extends JavaPlugin{
    private static Notifier instance;
    public LocalTime shutDownTime = LocalTime.of( 5, 30); //UTC to 11:30 MDT
    public LocalTime minutePrior = LocalTime.of(shutDownTime.getHour(),shutDownTime.getMinute()-1);
    boolean hourNotified = false;
    boolean minuteNotified = false;
    public boolean disabled = false;

    //Allows var check from CommandToggle.java
    public static Notifier getInstance() {return instance;}

    private void kickAllPlayers(){
        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                target.kickPlayer("Server Shut Down\n§4Th§eank §ayou §1for §dplay§5ing!\n§6ありがとう！"); //Server Shut Down Thank you for playing! ありがとう
        }
    }

    public void toggleStatus(){disabled = !disabled;}

    public void onEnable() {
        instance = this;
        this.getCommand("Ntoggle").setExecutor(new CommandToggle());
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
            if(currentTime.until(shutDownTime, ChronoUnit.MINUTES) == 10 && !minuteNotified){
                getServer().broadcastMessage("§e10 Minutes until server shutdown");
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

