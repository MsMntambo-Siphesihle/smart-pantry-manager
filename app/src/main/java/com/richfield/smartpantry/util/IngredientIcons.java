package com.richfield.smartpantry.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps an ingredient's normalised name to a representative emoji, so the
 * pantry list and recipe screens show a picture rather than plain text.
 *
 * The app is fully offline (Section 3.3 of the brief forbids network and
 * mapping SDKs), so emoji are used instead of downloaded images: they are
 * built into the Android font and need no extra assets or permissions.
 *
 * Matching is a simple, ordered "does the name contain this keyword" check
 * - more specific keywords are listed first so, for example, "cheddar
 * cheese" is tested against "cheese" correctly regardless of word order.
 */
public final class IngredientIcons {

    /** Shown for any ingredient that does not match a known keyword. */
    public static final String DEFAULT_ICON = "\uD83E\uDD58"; // 🥘 generic pot of food

    private static final Map<String, String> ICONS = new LinkedHashMap<>();

    static {
        ICONS.put("egg", "\uD83E\uDD5A");            // 🥚
        ICONS.put("cheese", "\uD83E\uDDC0");          // 🧀
        ICONS.put("butter", "\uD83E\uDDC8");          // 🧈
        ICONS.put("yoghurt", "\uD83E\uDD63");         // 🥣
        ICONS.put("cream", "\uD83E\uDDC8");           // 🧈 (dairy)
        ICONS.put("milk", "\uD83E\uDD5B");            // 🥛
        ICONS.put("salt", "\uD83E\uDDC2");            // 🧂
        ICONS.put("sugar", "\uD83E\uDDC1");           // 🧁
        ICONS.put("cinnamon", "\uD83E\uDDC2");         // 🧂 (spice)
        ICONS.put("paprika", "\uD83E\uDDC2");          // 🧂 (spice)
        ICONS.put("cocoa", "\uD83C\uDF6B");            // 🍫
        ICONS.put("honey", "\uD83C\uDF6F");            // 🍯
        ICONS.put("flour", "\uD83C\uDF3E");            // 🌾
        ICONS.put("maize", "\uD83C\uDF3D");            // 🌽
        ICONS.put("rice", "\uD83C\uDF5A");             // 🍚
        ICONS.put("noodle", "\uD83C\uDF5C");           // 🍜
        ICONS.put("pasta", "\uD83C\uDF5D");            // 🍝
        ICONS.put("bread", "\uD83C\uDF5E");            // 🍞
        ICONS.put("tortilla", "\uD83C\uDF2F");         // 🌯
        ICONS.put("lentil", "\uD83E\uDED8");           // 🫘
        ICONS.put("bean", "\uD83E\uDED8");             // 🫘
        ICONS.put("pea", "\uD83D\uDFE2");              // 🟢
        ICONS.put("potato", "\uD83E\uDD54");           // 🥔
        ICONS.put("tomato", "\uD83C\uDF45");           // 🍅
        ICONS.put("onion", "\uD83E\uDDC5");            // 🧅
        ICONS.put("garlic", "\uD83E\uDDC4");           // 🧄
        ICONS.put("carrot", "\uD83E\uDD55");           // 🥕
        ICONS.put("pepper", "\uD83C\uDF36");           // 🌶
        ICONS.put("mushroom", "\uD83C\uDF44");         // 🍄
        ICONS.put("coriander", "\uD83C\uDF3F");        // 🌿
        ICONS.put("apple", "\uD83C\uDF4E");            // 🍎
        ICONS.put("banana", "\uD83C\uDF4C");           // 🍌
        ICONS.put("lemon", "\uD83C\uDF4B");            // 🍋
        ICONS.put("chicken", "\uD83C\uDF57");          // 🍗
        ICONS.put("beef", "\uD83E\uDD69");             // 🥩
        ICONS.put("mince", "\uD83E\uDD69");            // 🥩
        ICONS.put("tuna", "\uD83D\uDC1F");             // 🐟
        ICONS.put("fish", "\uD83D\uDC1F");             // 🐟
        ICONS.put("mayonnaise", "\uD83E\uDD6B");        // 🥫
        ICONS.put("stock", "\uD83C\uDF72");             // 🍲
        ICONS.put("soy sauce", "\uD83C\uDF76");         // 🍶
        ICONS.put("olive oil", "\uD83E\uDED2");         // 🫒
        ICONS.put("oil", "\uD83E\uDED9");               // 🫙
    }

    private IngredientIcons() {
    }

    /**
     * Returns the best-matching emoji for a normalised ingredient name, or
     * {@link #DEFAULT_ICON} when nothing matches.
     */
    public static String getIcon(String normalisedName) {
        if (normalisedName == null || normalisedName.isEmpty()) return DEFAULT_ICON;

        for (Map.Entry<String, String> entry : ICONS.entrySet()) {
            if (normalisedName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return DEFAULT_ICON;
    }
}
