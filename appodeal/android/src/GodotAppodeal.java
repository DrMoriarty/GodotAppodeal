package org.godotengine.godot;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.UserSettings;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.NonSkippableVideoCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class GodotAppodeal extends Godot.SingletonBase {
    //variable
    private Activity activity = null;
    String appKey = "";
    private int instanceId = 0;
    private boolean testing = true;
    private Toast toast;

    private HashMap<String, String> callbackFunctions;

    static public Godot.SingletonBase initialize(Activity p_activity) {

        return new GodotAppodeal(p_activity);
    }

    //constructor
    public GodotAppodeal(Activity p_activity) {
        //The registration of this and its functions
        registerClass("Appodeal", new String[]{

                "init", "showBannerAd", "showInterstitialAd", "hideBannerAd",
                "hideInterstitialAd", "isBannerLoaded", "isInterstitialLoaded", "isAnyAdLoaded",
                "isRewardedVideoLoaded", "showRewardedVideoAd", "showNonSkipVideoAd", "loadNonSkipVideoAd", "loadBannerAd",
                "loadInterstitialVideoAd", "loadRewardedVideoAd", "registerCallback", "unregisterCallback",
                "userSetAge", "userSetGender"
        });

        callbackFunctions = new HashMap<String, String>();

        activity = p_activity;
    }


    // Register callbacks to GDscript
    public void registerCallback(final String callback_type, final String callback_function) {
        callbackFunctions.put(callback_type, callback_function);
    }

    // Deregister callbacks to GDscript
    public void unregisterCallback(final String callback_type) {
        callbackFunctions.remove(callback_type);
    }

    // Run a callback to GDscript
    private void runCallback(final String callback_type) {
        if (callbackFunctions.containsKey(callback_type)) {
            GodotLib.calldeferred(instanceId, callbackFunctions.get(callback_type), new Object[]{});
        }
    }

    public void userSetAge(final int age) {
        UserSettings userSettings = Appodeal.getUserSettings(activity);
        userSettings.setAge(age);
    }

    public void userSetGender(final int gender) {
        UserSettings userSettings = Appodeal.getUserSettings(activity);
        if(gender == 0)
            userSettings.setGender(UserSettings.Gender.FEMALE);
        else
            userSettings.setGender(UserSettings.Gender.MALE);
    }

    //initialization of appodeal
    public void init(final int new_instanceId, final String key, final String type, final boolean is_testing) {
        instanceId = new_instanceId;
        testing = is_testing;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                appKey = key;
                Appodeal.disableLocationPermissionCheck();
                Appodeal.disableNetwork(activity, "cheetah");
                //check string to see if it is a test or a normal initialization or a spacific initialization
                if (testing) {
                    Appodeal.setTesting(true);
                }

                //set all auto cache to false
                Appodeal.setAutoCache(Appodeal.BANNER, false);
                Appodeal.setAutoCache(Appodeal.NON_SKIPPABLE_VIDEO, false);
                Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, false);
                Appodeal.setAutoCache(Appodeal.INTERSTITIAL, false);

                if (type.equals("banner")) {
                    Appodeal.initialize(activity, appKey, Appodeal.BANNER);
                    Appodeal.cache(activity, Appodeal.BANNER);
                } else if (type.equals("banner/video")) {
                    // deprecated
                    //Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO);
                    Appodeal.initialize(activity, appKey, Appodeal.BANNER /*| Appodeal.SKIPPABLE_VIDEO*/);
                    Appodeal.cache(activity, Appodeal.BANNER);
                    //Appodeal.cache(activity, Appodeal.SKIPPABLE_VIDEO);
                } else if (type.equals("video")) {
                    // deprecated
                    //Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO);
                    //Appodeal.initialize(activity, appKey, Appodeal.SKIPPABLE_VIDEO);
                    //Appodeal.cache(activity, Appodeal.SKIPPABLE_VIDEO);
                } else if (type.equals("interstitial")) {
                    Appodeal.initialize(activity, appKey, Appodeal.INTERSTITIAL);
                    Appodeal.cache(activity, Appodeal.INTERSTITIAL);
                } else if (type.equals("interstitial/video")) {
                    // deprecated
                    //Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO);
                    Appodeal.initialize(activity, appKey, Appodeal.INTERSTITIAL /*| Appodeal.SKIPPABLE_VIDEO*/);
                    //Appodeal.cache(activity, Appodeal.SKIPPABLE_VIDEO);
                    Appodeal.cache(activity, Appodeal.INTERSTITIAL);
                } else if (type.equals("interstitial/nonSkipVideo")) {
                    Appodeal.initialize(activity, appKey, Appodeal.INTERSTITIAL | Appodeal.NON_SKIPPABLE_VIDEO);
                    Appodeal.cache(activity, Appodeal.INTERSTITIAL);
                    Appodeal.cache(activity, Appodeal.NON_SKIPPABLE_VIDEO);
                } else if (type.equals("nonSkipVideo")) {
                    Appodeal.initialize(activity, appKey, Appodeal.NON_SKIPPABLE_VIDEO);
                    Appodeal.cache(activity, Appodeal.NON_SKIPPABLE_VIDEO);
                } else if (type.equals("rewardedvideo")) {
                    Appodeal.initialize(activity, appKey, Appodeal.REWARDED_VIDEO);
                    Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
                } else if (type.equals("rewarded/interstitial")) {
                    Appodeal.initialize(activity, appKey, Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO);
                    Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
                    Appodeal.cache(activity, Appodeal.INTERSTITIAL);
                } else if (type.equals("banner/rewardedvideo")) {
                    Appodeal.initialize(activity, appKey, Appodeal.BANNER | Appodeal.REWARDED_VIDEO);
                    Appodeal.cache(activity, Appodeal.BANNER);
                    Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
                } else if (type.equals("banner/interstitial")) {
                    Appodeal.initialize(activity, appKey, Appodeal.BANNER | Appodeal.INTERSTITIAL);
                    Appodeal.cache(activity, Appodeal.BANNER);
                    Appodeal.cache(activity, Appodeal.INTERSTITIAL);
                } else {
                    Log.d("godot", "Did not find a initialization type for :" + type);
                }

                enableCallBacks();
            }

        });
    }


    public void showBannerAd(String type) {

        if (type.equals("top")) {
            activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.show(activity, Appodeal.BANNER_TOP);
                        Log.d("godot", "show banner top");
                    }
                });
        } else if (type.equals("bottom")) {
            activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.show(activity, Appodeal.BANNER_BOTTOM);
                        Log.d("godot", "show banner buttom");
                    }
                });
        } else {
            Log.d("godot", "Did not find banner of type :" + type);
        }
    }

    public void showNonSkipVideoAd() {
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.show(activity, Appodeal.NON_SKIPPABLE_VIDEO);
                        Log.d("godot", "show video");
                    }
            });
    }

    public void showRewardedVideoAd() {
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.show(activity, Appodeal.REWARDED_VIDEO);
                        Log.d("godot", "show rewarded video");
                    }
            });
    }

    public void showInterstitialAd() {
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.show(activity, Appodeal.INTERSTITIAL);
                        Log.d("godot", "show interstitial");
                    }
            });
    }

    public void hideBannerAd() {
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.hide(activity, Appodeal.BANNER);
                        Log.d("godot", "hide banner");
                    }
            });
    }

    public void hideInterstitialAd() {
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.hide(activity, Appodeal.INTERSTITIAL);
                        Log.d("godot", "hide interstitial");
                    }
            });
    }

    public void loadNonSkipVideoAd(){
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.cache(activity, Appodeal.NON_SKIPPABLE_VIDEO);
                    }
            });
    }

    public void loadBannerAd(){
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.cache(activity, Appodeal.BANNER);
                    }
            });
    }

    public void loadInterstitialVideoAd(){
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.cache(activity, Appodeal.INTERSTITIAL);
                    }
            });
    }

    public void loadRewardedVideoAd(){
        activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
                    }
            });
    }

    public boolean isBannerLoaded() {
        boolean loaded;
        loaded = Appodeal.isLoaded(Appodeal.BANNER);
        return loaded;
    }

    public boolean isRewardedVideoLoaded() {
        boolean loaded;
        loaded = Appodeal.isLoaded(Appodeal.REWARDED_VIDEO);
        return loaded;
    }

    public boolean isInterstitialLoaded() {
        boolean loaded;
        loaded = Appodeal.isLoaded(Appodeal.INTERSTITIAL);
        return loaded;
    }

    public boolean isAnyAdLoaded() {
        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            return true;
        }
        if (Appodeal.isLoaded(Appodeal.BANNER)) {
            return true;
        }
        if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
            return true;
        }

        return false;
    }


    private void enableCallBacks() {

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {

            @Override
            public void onInterstitialLoaded(boolean isPrecache) {

                showToastOnTesting("onInterstitialLoaded");
                runCallback("onInterstitialLoaded");
            }

            @Override
            public void onInterstitialFailedToLoad() {

                showToastOnTesting("onInterstitialFailedToLoad");
                runCallback("onInterstitialFailedToLoad");
            }

            @Override
            public void onInterstitialShown() {

                showToastOnTesting("onInterstitialShown");
                runCallback("onInterstitialShown");
            }

            @Override
            public void onInterstitialClicked() {

                showToastOnTesting("onInterstitialClicked");
                runCallback("onInterstitialClicked");
            }

            @Override
            public void onInterstitialClosed() {

                showToastOnTesting("onInterstitialClosed");
                runCallback("onInterstitialClosed");
            }
        });

        Appodeal.setBannerCallbacks(new BannerCallbacks() {

            @Override
            public void onBannerLoaded(int height, boolean isPrecache) {

                showToastOnTesting(String.format("onBannerLoaded, %ddp", height));
                runCallback("onBannerLoaded");
            }

            @Override
            public void onBannerFailedToLoad() {

                showToastOnTesting("onBannerFailedToLoad");
                runCallback("onBannerFailedToLoad");
            }

            @Override
            public void onBannerShown() {

                showToastOnTesting("onBannerShown");
                runCallback("onBannerShown");
            }

            @Override
            public void onBannerClicked() {

                showToastOnTesting("onBannerClicked");
                runCallback("onBannerClicked");
            }
        });

        Appodeal.setNonSkippableVideoCallbacks(new NonSkippableVideoCallbacks() {

            @Override
            public void onNonSkippableVideoLoaded() {

                showToastOnTesting("onNonSkippableVideoLoaded");
                runCallback("onNonSkippableVideoLoaded");
            }

            @Override
            public void onNonSkippableVideoFailedToLoad() {
                showToastOnTesting("onNonSkippableVideoFailedToLoad");
                runCallback("onNonSkippableVideoFailedToLoad");
            }

            @Override
            public void onNonSkippableVideoShown() {

                showToastOnTesting("onNonSkippableVideoShown");
                runCallback("onNonSkippableVideoShown");
            }

            @Override
            public void onNonSkippableVideoFinished() {

                showToastOnTesting("onNonSkippableVideoFinished");
                runCallback("onNonSkippableVideoFinished");
            }

            @Override
            public void onNonSkippableVideoClosed(boolean finished) {

                showToastOnTesting(String.format("onNonSkippableVideoClosed, finished: %s", finished));
                runCallback("onNonSkippableVideoClosed");
            }
        });

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {

            @Override
            public void onRewardedVideoLoaded() {

                showToastOnTesting("onRewardedVideoLoaded");
                runCallback("onRewardedVideoLoaded");
            }

            @Override
            public void onRewardedVideoFailedToLoad() {

                showToastOnTesting("onRewardedVideoFailedToLoad");
                runCallback("onRewardedVideoFailedToLoad");
            }

            @Override
            public void onRewardedVideoShown() {

                showToastOnTesting("onRewardedVideoShown");
                runCallback("onRewardedVideoShown");
            }

            @Override
            public void onRewardedVideoFinished(int amount, String name) {

                showToastOnTesting(String.format("onRewardedVideoFinished. Reward: %d %s", amount, name));
                runCallback("onRewardedVideoFinished");
            }

            @Override
            public void onRewardedVideoClosed(boolean finished) {

                showToastOnTesting(String.format("onRewardedVideoClosed, finished: %s", finished));
                runCallback("onRewardedVideoClosed");
            }
        });
    }

    private void showToastOnTesting(final String text) {
        if (!testing) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

}
