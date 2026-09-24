# Smart Pantry Manager

An Android application, written entirely in Java, that helps a household cut food waste by
tracking the ingredients it actually has at home and suggesting **only** the recipes that can
be cooked right now - with no shopping trip required.

Module: Mobile App Development 700 | Faculty of Information Technology | Richfield Graduate
Institute of Technology

---

## 1. What the app does

The user records what is in their pantry (name, quantity, unit and an optional expiry date).
The app holds a pre-loaded collection of **20 recipes**. Every time the Suggestions tab is
opened, the app tests each recipe against the pantry and lists only those where **every single
required ingredient is present in at least the required quantity**.

If a recipe needs five ingredients and only four are in the pantry, that recipe does not
appear. Recipes missing exactly one ingredient are shown in a clearly separated "Almost There"
section, which can be switched off in Settings.

## 2. Screens

| Screen | Class | Purpose |
|---|---|---|
| Pantry List | `ui/PantryFragment` | Lists all ingredients in a RecyclerView; delete from here |
| Add / Edit Ingredient | `AddEditIngredientActivity` | Create and update pantry records, with validation |
| Suggested Recipes | `ui/SuggestedRecipesFragment` | Runs the strict-matching rule |
| Recipe Detail | `RecipeDetailActivity` | Full ingredient list and method, with have/missing ticks |
| Settings | `ui/SettingsFragment` | Expiry alerts, "Almost There" toggle, unit preference |

Navigation between the three tabs uses a **BottomNavigationView** in `MainActivity`; the two
child screens are opened with **explicit Intents** carrying the record id as an extra.

## 3. Database choice: SQLite (SQLiteOpenHelper)

**SQLite was chosen, and here is why:**

1. **The data is private and single-user.** A pantry belongs to one person on one phone.
   There is nothing to sync and nobody to share with, so the account management, network
   layer and security rules that Firebase or a hosted PostgreSQL instance would require would
   add cost and complexity without adding value.
2. **The app must work offline.** People check what they can cook while standing in the
   kitchen, which is often exactly where the signal is worst. A local database means the app
   never shows a spinner or an error because the network is down.
3. **The matching logic is read-heavy and latency-sensitive.** Every time the Suggestions tab
   opens, the app reads 20 recipes and roughly 100 ingredient rows. On-device SQLite returns
   that in milliseconds; a REST round-trip to PostgreSQL would not.
4. **`SQLiteOpenHelper` is the approach covered in the module's persistent data chapter**, so
   the implementation is consistent with what was taught.
5. **No cost, no vendor account, no API keys in the repository.**

The trade-off accepted is that the pantry does not sync across devices. For this use case that
is an acceptable limitation rather than a missing feature.

### Tables

```
pantry_items                       recipes                  recipe_ingredients
------------                       -------                  ------------------
_id              INTEGER PK        _id       INTEGER PK     _id             INTEGER PK
name             TEXT              name      TEXT           recipe_id       INTEGER FK -> recipes(_id)
normalised_name  TEXT              category  TEXT           name            TEXT
quantity         REAL              steps     TEXT           normalised_name TEXT
unit             TEXT                                       quantity        REAL
expiry_date      TEXT (nullable)                            unit            TEXT
```

`recipes` and `recipe_ingredients` are seeded once, inside a single transaction, the first
time the database is created (`data/RecipeSeeder.java`). `pantry_items` carries full CRUD.

## 4. How the strict-matching rule works

All of it lives in `logic/RecipeMatcher.java`, which deliberately contains no Android classes
so the rule can be read and tested on its own.

1. **Normalise both sides.** `logic/IngredientNormaliser` lower-cases the name, strips
   punctuation and descriptive words ("fresh", "large", "chopped"), then singularises each
   remaining word, so `"3 Large Ripe Tomatoes!"` and `"tomato"` both become `tomato`. This
   key is calculated on write and stored in `normalised_name`, so the matcher never re-parses
   text while a list is scrolling.
2. **Index the pantry.** Entries with the same key are summed, so two separate "Pasta" rows
   of 120 g count as 240 g.
3. **Convert units.** `logic/UnitConverter` maps every unit into one of three families -
   MASS (base gram), VOLUME (base millilitre) or COUNT (base piece) - so 1 kg of flour
   satisfies a recipe asking for 200 g.
4. **Test each recipe.** An ingredient counts as available only when the name matches *and*
   the pantry holds at least the required amount. Every shortfall is recorded.
5. **Only a recipe with zero shortfalls is suggested.** A recipe short of exactly one
   ingredient goes to the separate "Almost There" list instead.

If an exact key is not found, a whole-word loose match is tried, so `"chicken breast"` in the
pantry satisfies a recipe asking for `"chicken"` - but `"corn"` never matches `"cornflour"`.

## 5. Ingredient and recipe icons

Every pantry row, every recipe in the Suggestions list, and the Recipe Detail header show a
small emoji icon rather than plain text. This is implemented with `util/IngredientIcons.java`,
which maps an ingredient's normalised name to a representative emoji (`egg` -> 🥚, `cheese` ->
🧀, and so on) using an ordered keyword match, with a generic 🥘 fallback for anything
unrecognised. Recipes carry their own hand-picked icon (e.g. Cheese Omelette -> 🍳), stored in
a new `icon` column on the `recipes` table and seeded alongside each recipe in
`data/RecipeSeeder.java`.

Emoji were chosen over downloaded images because the app is required to work fully offline
(Section 3.3 of the brief) - they are built into the Android font and need no extra assets,
network access or storage permissions.

## 6. App icon

The launcher icon uses the illustrated pantry design supplied for the project (jars, fresh
vegetables and a recipe checklist inside a rounded green badge). It is provided as a full
Android icon set rather than a single image, so it displays correctly on every device and
launcher shape:

- **Legacy icons** (`mipmap-*/ic_launcher.png`, `ic_launcher_round.png`) for API 24-25 devices,
  generated at all five density buckets (mdpi through xxxhdpi).
- **An adaptive icon** (`mipmap-anydpi-v26/ic_launcher.xml`) for API 26+, built from a
  foreground layer (`ic_launcher_foreground.png`, the artwork scaled to sit inside Android's
  safe zone) over a solid background colour sampled from the artwork itself
  (`#0A4940`), so the icon looks correct however a given phone's launcher masks it - circular,
  squircle, or rounded square.

## 7. Setup and run instructions

**Requirements:** Android Studio (Koala or newer), JDK 17, Android SDK Platform 34, and an
emulator or physical device running Android 7.0 (API 24) or higher. The project ships with its
own Gradle wrapper (Gradle 8.7, Android Gradle Plugin 8.5.2), which is compatible with modern
JDKs including JDK 21 — you do not need to install Gradle separately.

1. Clone the repository:
   ```bash
   git clone <YOUR-REPOSITORY-URL>
   ```
2. Open Android Studio, choose **File > Open**, and select the cloned `SmartPantryManager`
   folder (the one containing `settings.gradle`).
3. Wait for Gradle to sync. Android Studio downloads the Gradle wrapper and dependencies
   automatically on first sync; accept any SDK component it offers to install.
4. Select a device in the toolbar and press **Run** (Shift + F10).
5. On first launch the database is created and the 20 recipes are seeded automatically. The
   pantry starts empty - tap the green **+** button to add your first ingredient.

**To see the strict-matching rule in action quickly:** add Eggs (3 piece), Cheddar Cheese
(40 g), Butter (10 g) and Salt (1 tsp), then open the Recipes tab. *Cheese Omelette* appears.
Delete the butter and it disappears again.

**No permissions are required.** The app requests no internet, location or mapping
permissions; it runs entirely offline against its local database, as required by the brief.

## 8. Project structure

```
app/src/main/java/com/richfield/smartpantry/
├── MainActivity.java                 host Activity + bottom navigation
├── AddEditIngredientActivity.java    create/update form + validation
├── RecipeDetailActivity.java         ingredients and method
├── data/
│   ├── DatabaseHelper.java           SQLiteOpenHelper, all CRUD
│   └── RecipeSeeder.java             the 20 pre-loaded recipes
├── logic/
│   ├── RecipeMatcher.java            THE STRICT-MATCHING RULE
│   ├── IngredientNormaliser.java     name normalisation
│   └── UnitConverter.java            g/kg/ml/l/tsp/tbsp/cup conversion
├── model/                            PantryItem, Recipe, RecipeIngredient, MatchResult
├── ui/                               the three fragments + two custom adapters
└── util/                             Prefs (SharedPreferences), DateUtils
```

## 9. Out of scope

In line with Section 3.3 of the brief, this app contains no Google Maps or any other mapping
SDK, no GPS or location services, no payment processing, and is not published to the Play
Store.
