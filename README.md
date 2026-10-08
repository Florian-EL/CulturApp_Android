# CulturApp Android

Application Android de consultation d’une bibliothèque CulturApp. L’interface est construite avec Jetpack Compose et les données sont lues avec Room (SQLite). L’application propose des écrans pour les films, séries, romans, mangas, webtoons et histoires Wattpad.

## Sommaire

- [Fonctionnalités](#fonctionnalités)
- [Technologies et versions](#technologies-et-versions)
- [Prérequis](#prérequis)
- [Ouvrir et compiler le projet](#ouvrir-et-compiler-le-projet)
- [Base de données et import](#base-de-données-et-import)
- [Architecture](#architecture)
- [Limites actuelles](#limites-actuelles)

## Fonctionnalités

- Navigation entre six catégories de contenus depuis un tiroir de navigation.
- Affichage des bibliothèques de films, séries, romans, mangas, webtoons et Wattpad.
- Sélection d’un dossier avec le sélecteur Android de documents pour charger une base SQLite existante.
- Copie de la base sélectionnée dans le stockage privé de l’application, avec prise en charge de ses éventuels fichiers `-wal` et `-shm` ainsi que des images présentes dans le dossier.
- Accès aux données via des DAO Room et un dépôt (`LibraryRepository`).

L’application est actuellement conçue principalement pour consulter les données. Les DAO n'exploitent que des requêtes de lecture.

## Technologies et versions

- Kotlin avec cible JVM 17.
- Android Gradle Plugin : 8.7.3.
- Kotlin : 2.0.21.
- Jetpack Compose / Material 3, Compose BOM 2025.12.00.
- Room 2.6.1, avec génération de code KSP.
- `minSdk` 26, `targetSdk` et `compileSdk` 35.

Les versions réellement utilisées sont déclarées dans `build.gradle.kts` et `app/build.gradle.kts`. `gradle/libs.versions.toml` contient aussi un catalogue de versions, mais le module app référence directement plusieurs dépendances avec leurs versions dans son fichier Gradle.

## Prérequis

- Android Studio compatible avec le wrapper Gradle et Android Gradle Plugin du projet.
- JDK 17.
- Android SDK Platform 35 et les outils de compilation correspondants.
- Un appareil ou émulateur Android API 26 ou supérieur pour exécuter l’application.

## Ouvrir et compiler le projet

Ouvrez le dossier du projet dans Android Studio et laissez Gradle synchroniser les dépendances. Depuis un terminal à la racine :

```bash
./gradlew assembleDebug
```

L’APK debug est généré sous `app/build/outputs/apk/debug/`. Pour installer sur un appareil connecté avec ADB :

```bash
./gradlew installDebug
```

Sous Windows, utilisez `gradlew.bat` à la place de `./gradlew`.

## Base de données et import

Au premier démarrage, Room utilise une base locale nommée `library.db` dans le stockage privé de l’application. Elle est initialement vide. Pour consulter une base existante :

1. Ouvrez le menu de navigation.
2. Touchez **Sélectionner un dossier de base de données**.
3. Choisissez le dossier contenant un fichier `.db` ou `.sqlite`.
4. L’application sélectionne le premier fichier de base trouvé dans ce dossier et le copie dans son stockage privé.

La base doit contenir des tables compatibles avec les entités Room du projet : `film`, `serie`, `roman`, `manga`, `webtoon` et `wattpad`. La structure de schéma Android est versionnée dans `app/schemas/`. Une migration automatique de la version 2 à la version 3 est déclarée. En cas de schéma incompatible, le code utilise actuellement `fallbackToDestructiveMigration()` après le chargement, ce qui peut remplacer des données incompatibles : conservez toujours une copie de la base source.

Les images `.jpg`, `.jpeg`, `.png` et `.webp` situées dans le dossier sélectionné sont également copiées à côté de la base dans le répertoire privé. Le dossier choisi doit donc contenir les images utilisées par les fiches si celles-ci en dépendent.

## Architecture

```text
app/src/main/java/com/example/culturapp_android/
├── MainActivity.kt             # Navigation Compose et sélection de dossier
├── data/
│   ├── AppDatabase.kt          # Base Room, entités et version de schéma
│   ├── Daos.kt                 # Requêtes de lecture
│   ├── *Class.kt               # Entités des types de contenus
│   └── LibraryRepository.kt    # Accès aux DAO
├── ui/theme/                   # Écrans par type et composants partagés
└── viewmodel/
    └── TypeViewModel.kt        # Chargement des données et état de l’interface
```

Les schémas exportés par Room sont conservés dans `app/schemas/com.example.culturapp_android.data.AppDatabase/`.

## Limites actuelles

- Aucun mécanisme de synchronisation réseau n’est présent dans le code décrit ici ; l’import se fait depuis un dossier accessible via le sélecteur Android.
- L’application lit les catégories définies mais les fonctions d’édition des contenus ne sont pas exposées dans les DAO actuels.
- L’identifiant d’application et le namespace sont `com.example.culturapp_android` ; adaptez-les avant une publication sous une identité définitive.
