package br.net.rankup.altar.misc;

import org.bukkit.*;

public class ProgressBar
{
    public static String progressBar(final double current, final double needed, final String character) {
        final StringBuilder stringBuilder = new StringBuilder();
        final int doneBars = (int)(current / needed * 25.0);
        if (doneBars > 25) {
            stringBuilder.append("§a" + repeat(10, character));
        }
        else {
            final double percent = current / needed * 100.0;
            final int leftBars = 25 - doneBars;
            final ChatColor color = ChatColor.GREEN;
            stringBuilder.append(color).append(repeat(doneBars, character)).append(ChatColor.GRAY).append(repeat(leftBars, character)).append("");
        }
        return stringBuilder.toString();
    }
    
    private static String repeat(final int times, final String character) {
        return StringUtils.repeat(String.valueOf(character), times);
    }
}
