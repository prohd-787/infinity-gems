package dev.infinitygems;

/** Die sechs Edelsteine plus der Infinity Gem mit ihren Spielwerten. */
public enum Gem {
    //            id          Abklingzeit(s)  Dauer(s)  Tooltip-Zeilen
    RUBY("ruby", 20, 0, 3),
    SAPPHIRE("sapphire", 30, 5, 2),
    EMERALD("emerald", 40, 10, 2),
    AMETHYST("amethyst", 25, 10, 2),
    TOPAZ("topaz", 60, 15, 2),
    VOID("void_gem", 35, 10, 2),
    INFINITY("infinity_gem", 300, 30, 5);

    public final String id;
    public final int cooldownSeconds;
    public final int durationSeconds;
    public final int tooltipLines;

    Gem(String id, int cooldownSeconds, int durationSeconds, int tooltipLines) {
        this.id = id;
        this.cooldownSeconds = cooldownSeconds;
        this.durationSeconds = durationSeconds;
        this.tooltipLines = tooltipLines;
    }

    public int cooldownTicks() {
        return cooldownSeconds * 20;
    }
}
