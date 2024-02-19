package org.mossmc.mosscg.MoBoxCore.Game;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.mossmc.mosscg.MoBoxCore.Info.InfoCountDown;
import org.mossmc.mosscg.MoBoxCore.Main;

@SuppressWarnings("unused")
public class GameWait {
    public static int remainSecond = 30;

    @SuppressWarnings({"deprecation", "unused"})
    public static void startWait() {
        remainSecond = GameBasicInfo.getGame.waitTime();
        final boolean[] isReduced = {false};
        new BukkitRunnable() {
            @SuppressWarnings("CodeBlock2Expr")
            @Override
            public void run() {
                GameStatus.gameStatus status = GameBasicInfo.gameStatus;
                if (!status.equals(GameStatus.gameStatus.Waiting)) {
                    cancel();
                    return;
                }
                if (remainSecond <= 0) {
                    try {
                        GameBasicInfo.startMethod.invoke(Main.instance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                int playerNow = Main.instance.getServer().getOnlinePlayers().size();
                if (playerNow < GameBasicInfo.getGame.minPlayer()) {
                    remainSecond = GameBasicInfo.getGame.waitTime();
                    TextComponent textComponent = new TextComponent(ChatColor.GREEN+"正在等待更多玩家加入游戏...");
                    Main.instance.getServer().getOnlinePlayers().forEach(player -> {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
                    });
                    isReduced[0] = false;
                } else {
                    if (playerNow >= GameBasicInfo.getGame.maxPlayer() && !isReduced[0]) {
                        Bukkit.broadcastMessage(ChatColor.GREEN+"玩家到齐，倒计时缩短！");
                        remainSecond = GameBasicInfo.getGame.reduceTime();
                        isReduced[0] = true;
                    }
                    TextComponent textComponent = new TextComponent(InfoCountDown.getRemainSecondString(remainSecond));
                    Sound sound = Sound.valueOf(Main.getConfig.getString("countdownSound"));
                    Main.instance.getServer().getOnlinePlayers().forEach(player -> {
                        if(remainSecond <= 10 || remainSecond % 10 == 0){
                         player.sendMessage(ChatColor.YELLOW +"游戏将在"+InfoCountDown.getRemainSecondString(remainSecond)+ChatColor.YELLOW+"秒后开始！");
                         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
                         player.playSound(player.getLocation(),sound,1.0f,1.0f);
                    }
                    });
                    remainSecond--;
                }
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }
}
