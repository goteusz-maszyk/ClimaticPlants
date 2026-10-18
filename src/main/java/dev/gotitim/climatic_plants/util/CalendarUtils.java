package dev.gotitim.climatic_plants.util;

import java.util.Locale;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timelines;

public class CalendarUtils {
    public static Component sealDate(long clockTime, Level level) {
        int singleDayTime = level.registryAccess()
                .lookup(Registries.TIMELINE)
                .get()
                .getValue(Timelines.OVERWORLD_DAY)
                .periodTicks()
                .orElse(Integer.MAX_VALUE);
        int day = (int) (clockTime / singleDayTime);
        int dayTime = (int) (clockTime % singleDayTime);

        int hourDuration = singleDayTime / 24;
        int hour = (dayTime / hourDuration + 6) % 24; // minecraft day starts at 6:00am
        int minute = dayTime % hourDuration * 60 / hourDuration;

        String time = String.format(Locale.ROOT, "%02d:%02d", hour, minute);
        return Component.translatable("tooltip.climatic_plants.sealed_on", day, time);
    }
}
