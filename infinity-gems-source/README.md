# Infinity Gems (Fabric, Minecraft 26.3)

Sechs Edelsteine (Rubin, Saphir, Smaragd, Amethyst, Topas, Void-Gem), der Infinity Core und der Infinity Gem.

## .jar bauen – ohne etwas zu installieren (GitHub)

1. Auf github.com ein neues, leeres Repository anlegen.
2. Den kompletten Inhalt dieses Ordners hochladen (Add file → Upload files; der versteckte Ordner `.github` muss mit).
3. Im Repository auf **Actions** gehen. Der Build startet automatisch (sonst „Build Infinity Gems" → Run workflow).
4. Nach ca. 3–5 Minuten den Lauf öffnen und unter **Artifacts** `infinitygems-jar` herunterladen.
5. Die enthaltene `infinitygems-1.0.0.jar` in den `mods`-Ordner legen.
   Zusätzlich wird die **Fabric API 0.160.7+26.3** (oder neuer) und der **Fabric Loader 0.19.5+** benötigt (im Modrinth Launcher einfach ein Fabric-Profil für 26.3 anlegen und Fabric API dazu installieren).

## Lokal bauen

Java 25 und Gradle 9.6.0 installieren, dann: `gradle build` → `build/libs/infinitygems-1.0.0.jar`

## Rezepte (Werkbank)

Alle sechs Gems brauchen 1 Netherite-Barren in der Mitte, 2 Diamanten und je ein thematisches Material.
Infinity Core: 4 Netherite-Barren, 4 Netherstern, 1 Netherite-Block.
Infinity Gem (formlos): 6 Gems + Infinity Core + 2 Netherstern.

Texturen neu erzeugen: `python3 tools/generate_textures.py`
