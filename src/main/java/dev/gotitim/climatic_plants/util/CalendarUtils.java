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
                .map(timelines -> timelines.getValue(Timelines.OVERWORLD_DAY))
                .map(timeline -> timeline.periodTicks().orElse(Integer.MAX_VALUE))
                .orElse(24000);
        int day = (int) (clockTime / singleDayTime);
        int dayTime = (int) (clockTime % singleDayTime);

        int hourDuration = singleDayTime / 24;
        int hour = (dayTime / hourDuration + 6) % 24; // minecraft day starts at 6:00am
        int minute = dayTime % hourDuration * 60 / hourDuration;

        String time = String.format(Locale.ROOT, "%02d:%02d", hour, minute);
        return Component.translatable("tooltip.climatic_plants.sealed_on", day, time);
    }
}
