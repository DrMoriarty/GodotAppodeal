package org.godotengine.godot;

import android.content.Context;
import android.util.Log;
import android.app.Activity;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import java.util.HashMap;

import com.my.target.ads.MyTargetView;
import com.my.target.ads.InterstitialAd;

public class MyTargetPlugin extends Godot.SingletonBase {
    private final String TAG = "MyTarget";
    private FrameLayout layout = null;
    private MyTargetView bannerView = null;
    private InterstitialAd fullscreenAd = null;
    private Activity activity;
    private int instanceId = 0;
    private boolean inited = false;
    private HashMap<String, String> callbackFunctions;

    static public Godot.SingletonBase initialize(Activity p_activity) {

        return new MyTargetPlugin(p_activity);
    }

    //constructor
    public MyTargetPlugin(Activity p_activity) {
        //The registration of this and its functions
        registerClass("MyTarget", new String[]{
                "init",
                "registerCallback", "unregisterCallback",
                "loadBanner", "showBanner", "removeBanner",
                "loadFullScreen", "showFullScreen"
        });
        callbackFunctions = new HashMap<String, String>();

        activity = p_activity;
        Log.i(TAG, "MyTarget module inited");
    }

    @Override protected void onMainPause() {
        Log.i(TAG, "Paused");
        if(bannerView != null) bannerView.pause();
    }

    @Override protected void onMainResume() {
        Log.i(TAG, "Resume");
        if(bannerView != null) bannerView.resume();
    }
    
    @Override protected void onMainDestroy() {
        Log.i(TAG, "Destroy");
        if(bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
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
    private void runCallback(final String callback_type, final Object argument) {
        if (callbackFunctions.containsKey(callback_type)) {
            GodotLib.calldeferred(instanceId, callbackFunctions.get(callback_type), new Object[]{ argument });
        }
    }

	public boolean init(final int new_instanceId, final boolean debugMode) {
        if(!inited) {
            Log.i(TAG, "MyTarget initialize");
            instanceId = new_instanceId;
            layout = (FrameLayout)activity.getWindow().getDecorView().getRootView();
            /*
              if(layout instanceof AbsoluteLayout) Log.i(TAG, "AbsoluteLayout!!!");
              if(layout instanceof DrawerLayout) Log.i(TAG, "DrawerLayout!!!");
              if(layout instanceof FrameLayout) Log.i(TAG, "FrameLayout!!!");
              if(layout instanceof GridLayout) Log.i(TAG, "GridLayout!!!");
              if(layout instanceof LinearLayout) Log.i(TAG, "LinearLayout!!!");
              if(layout instanceof RelativeLayout) Log.i(TAG, "RelativeLayout!!!");
              if(layout instanceof SlidingPaneLayout) Log.i(TAG, "SlidingPaneLayout!!!");
            */
            Log.i(TAG, "Find root object with type "+layout.getClass().toString());
            MyTargetView.setDebugMode(debugMode);
            InterstitialAd.setDebugMode(debugMode);
            inited = true;
            return true;
        }
        return false;
    }

    public boolean loadBanner(final int slot) {
        if(bannerView != null) {
            Log.e(TAG, "Banner view already created");
            runCallback("error_callback", "Banner view already created");
            return false;
        }
        activity.runOnUiThread(new Runnable() {
                public void run() {
                    bannerView = new MyTargetView(activity);
                    bannerView.init(slot);

                    // Устанавливаем слушатель событий
                    bannerView.setListener(new MyTargetView.MyTargetViewListener() {
                            @Override
                            public void onLoad(MyTargetView myTargetView) {
                                // Данные успешно загружены, запускаем показ объявлений
                                Log.i(TAG, "Banner has been loaded");
                                runCallback("banner_loaded", slot);
                            }

                            @Override
                            public void onNoAd(String reason, MyTargetView myTargetView) {
                                Log.e(TAG, "No ads for banner");
                                runCallback("error_callback", "No ads for slot "+slot);
                            }

                            @Override
                            public void onClick(MyTargetView myTargetView) {
                                Log.i(TAG, "Banner clicked");
                                runCallback("banner_clicked", slot);
                            }
                        });

                    // Запускаем загрузку данных
                    bannerView.load();
                    Log.i(TAG, "Start banner loading");
                }
            });
        return true;
    }

    public boolean showBanner() {
        if(bannerView == null) {
            Log.e(TAG, "Have no banner to show");
            runCallback("error_callback", "You should load banner before call showBanner");
            return false;
        } 
        // Устанавливаем слушатель событий
        bannerView.setListener(new MyTargetView.MyTargetViewListener() {
                @Override
                public void onLoad(MyTargetView myTargetView) {
                    Log.e(TAG, "Banner has been loaded twice");
                }
                @Override
                public void onNoAd(String reason, MyTargetView myTargetView) {
                    Log.e(TAG, "No ads for loaded banner!");
                    runCallback("error_callback", "No ads for slot");
                }
                @Override
                public void onClick(MyTargetView myTargetView) {
                    Log.i(TAG, "Banner clicked");
                    runCallback("banner_clicked", 0);
                }
            });
        // Добавляем экземпляр в лэйаут главной активности
        final FrameLayout.LayoutParams adViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0x50);
        layout.post(new Runnable() {
                public void run() {
                    Log.i(TAG, "Show new banner");
                    layout.addView(bannerView, adViewLayoutParams);
                    bannerView.start();
                }
            });
        return true;
    }

    public boolean removeBanner() {
        if(bannerView != null) {
            activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(TAG, "Remove banner");
                        layout.removeView(bannerView);
                        bannerView.destroy();
                        bannerView = null;
                        runCallback("banner_removed", 0);
                    }
                });
            return true;
        } else {
            runCallback("error_callback", "No banner view");
            return false;
        }
    }

    public boolean loadFullScreen(final int slot) {
        activity.runOnUiThread(new Runnable() {
                public void run() {
                    fullscreenAd = new InterstitialAd(slot, activity);
                    fullscreenAd.setListener(new InterstitialAd.InterstitialAdListener() {
                            @Override
                            public void onLoad(InterstitialAd ad) {
                                Log.i(TAG, "Fullscreen ad was loaded. Slot "+slot);
                                runCallback("fullscreen_loaded", slot);
                            }

                            @Override
                            public void onNoAd(String reason, InterstitialAd ad) {
                                Log.e(TAG, "No available fullscreen ad for slot "+slot);
                                runCallback("error_callback", "No ads for slot "+slot);
                            }

                            @Override
                            public void onClick(InterstitialAd ad) {
                                Log.i(TAG, "Click on fullscreen ad. Slot "+slot);
                                runCallback("fullscreen_clicked", slot);
                            }

                            @Override
                            public void onDisplay(InterstitialAd ad) {
                                Log.i(TAG, "Display fullscreen ad. Slot "+slot);
                                runCallback("fullscreen_shown", slot);
                            }

                            @Override
                            public void onDismiss(InterstitialAd ad) {
                                Log.i(TAG, "Fullscreen ad dismiss. Slot "+slot);
                                runCallback("fullscreen_hidden", slot);
                            }

                            @Override
                            public void onVideoCompleted(InterstitialAd ad) {
                                Log.i(TAG, "Fullscreen video completed. Slot "+slot);
                                runCallback("fullscreen_complete", slot);
                            }
                        });

                    // Запускаем загрузку данных
                    fullscreenAd.load();
                    Log.i(TAG, "Start fullscreen loading");
                }
            });
        return true;
    }

    public boolean showFullScreen() {
        if(fullscreenAd == null) {
            Log.e(TAG, "Have no fullscreenAd to show");
            runCallback("error_callback", "You should call loadFullScreen before showFullScreen");
            return false;
        } 
        fullscreenAd.setListener(new InterstitialAd.InterstitialAdListener() {
                @Override
                public void onLoad(InterstitialAd ad) {
                    Log.e(TAG, "Fullscreen ad was loaded twice.");
                    runCallback("fullscreen_loaded", 0);
                }

                @Override
                public void onNoAd(String reason, InterstitialAd ad) {
                    Log.e(TAG, "No available ad for loaded fullscreen Ad");
                    runCallback("error_callback", "No ads for fullscreen");
                }

                @Override
                public void onClick(InterstitialAd ad) {
                    Log.i(TAG, "Click on fullscreen ad");
                    runCallback("fullscreen_clicked", 0);
                }

                @Override
                public void onDisplay(InterstitialAd ad) {
                    Log.i(TAG, "Display fullscreen ad");
                    runCallback("fullscreen_shown", 0);
                }

                @Override
                public void onDismiss(InterstitialAd ad) {
                    Log.i(TAG, "Fullscreen ad dismiss");
                    runCallback("fullscreen_hidden", 0);
                }

                @Override
                public void onVideoCompleted(InterstitialAd ad) {
                    Log.i(TAG, "Fullscreen video completed");
                    runCallback("fullscreen_complete", 0);
                }
            });
        fullscreenAd.show();
        Log.i(TAG, "Show fullscreen");
        return true;
    }
};
