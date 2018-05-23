package com.cbmwebdevelopment.utterfare.main;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by Connor Meehan on 5/9/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public interface GlobalVariables {
    public static final String USER_LINK = "https://www.utterfare.com/includes/mobile/users/Users.php";
    public static final String USER_ITEMS_URL = "https://www.utterfare.com/includes/mobile/items/UserItems.php";
    public final String ENCODING = "UTF-8";
    public final Animation FADE_IN = new AlphaAnimation(0.0f, 1.0f);
    public final Animation FADE_OUT = new AlphaAnimation(1.0f, 0.0f);
}
