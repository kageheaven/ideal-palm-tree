# Phantom Lancer Roguelike

Мобильная roguelike-бродилка в стиле Dota 2 (Aghanim's Labyrinth). Играешь за Фантом Лансера: бей крипов, собирай амулеты и крафть рандомный мусор.

## Стек
- Kotlin + Jetpack Compose (Canvas-рендер)
- Gradle (AGP 8.2.0, Kotlin 1.9.20, minSdk 26)

## Геймплей
- 10 волн крипов, босс каждые 5 волн
- Скиллы PL: Spirit Lance, Doppelganger, Phantom Strike, Juxtapose
- Токены крафта за пройденную волну
- Крафт с шансом «тролля» — вместо нормального предмета выпадает мусор

## Управление
- Свайп — движение
- Тап — автоатака
- Даблтап — крафт

## Сборка
```bash
./gradlew assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`