package greenlink.statistic;

import lombok.Getter;

@Getter
public enum StatisticPeriod {
    ALL_TIME("Всё время", "all_time"),
    MONTH("Месяц", "month"),
    WEEK("Неделя", "week"),
    DAY("День", "day");

    private final String displayName;
    private final String period;

    StatisticPeriod(String displayName, String period) {
        this.displayName = displayName;
        this.period = period;
    }

    public StatisticPeriod next() {
        StatisticPeriod[] periods = values();
        int nextIndex = (this.ordinal() + 1) % periods.length;
        return periods[nextIndex];
    }
}