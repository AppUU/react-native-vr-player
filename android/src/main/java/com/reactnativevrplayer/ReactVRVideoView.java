package com.reactnativevrplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.TimedMetaData;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.model.MDPinchConfig;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.reactnativevrplayer.listener.VRModeListener;
import com.reactnativevrplayer.type.ScalableType;
import com.reactnativevrplayer.utils.APKExpansionSupport;
import com.reactnativevrplayer.utils.CustomDirectorFactory;
import com.reactnativevrplayer.utils.CustomProjectionFactory;
import com.reactnativevrplayer.utils.ScaleManager;
import com.reactnativevrplayer.utils.ZipResourceFile;
import com.reactnativevrplayer.view.ScalableGlSurfaceView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

@SuppressLint("ViewConstructor")
public class ReactVRVideoView extends ScalableGlSurfaceView implements
  IMediaPlayer.OnPreparedListener,
  IMediaPlayer.OnErrorListener,
  IMediaPlayer.OnBufferingUpdateListener,
  IMediaPlayer.OnSeekCompleteListener,
  IMediaPlayer.OnCompletionListener,
  IMediaPlayer.OnInfoListener,
  LifecycleEventListener,
  VRModeListener {

  @Override
  public void switchInteractiveMode(int mode) {
    if (mMDVRLibrary != null) mMDVRLibrary.switchInteractiveMode(getContext(), mode);
    else Log.d("vr", "vr video is not start");
  }

  @Override
  public void switchDisplayMode(int mode) {
    if (mMDVRLibrary != null) mMDVRLibrary.switchDisplayMode(getContext(), mode);
    else Log.d("vr", "vr video is not start");
  }

  @Override
  public void switchProjectionMode(int mode) {
    if (mMDVRLibrary != null) mMDVRLibrary.switchProjectionMode(getContext(), mode);
    else Log.d("vr", "vr video is not start");
  }

  @Override
  public void setAntiDistortionEnabled(boolean enabled) {
    if (mMDVRLibrary != null) {
      mMDVRLibrary.setAntiDistortionEnabled(enabled);
    } else Log.d("vr", "vr video is not start");
  }

  public enum Events {
    EVENT_LOAD_START("onVideoLoadStart"),
    EVENT_LOAD("onVideoLoad"),
    EVENT_ERROR("onVideoError"),
    EVENT_PROGRESS("onVideoProgress"),
    EVENT_TIMED_METADATA("onTimedMetadata"),
    EVENT_SEEK("onVideoSeek"),
    EVENT_END("onVideoEnd"),
    EVENT_STALLED("onPlaybackStalled"),
    EVENT_RESUME("onPlaybackResume"),
    EVENT_READY_FOR_DISPLAY("onReadyForDisplay"),
    EVENT_FULLSCREEN_WILL_PRESENT("onVideoFullscreenPlayerWillPresent"),
    EVENT_FULLSCREEN_DID_PRESENT("onVideoFullscreenPlayerDidPresent"),
    EVENT_FULLSCREEN_WILL_DISMISS("onVideoFullscreenPlayerWillDismiss"),
    EVENT_FULLSCREEN_DID_DISMISS("onVideoFullscreenPlayerDidDismiss");

    private final String mName;

    Events(final String name) {
      mName = name;
    }

    @Override
    public String toString() {
      return mName;
    }
  }

  public static final String EVENT_PROP_FAST_FORWARD = "canPlayFastForward";
  public static final String EVENT_PROP_SLOW_FORWARD = "canPlaySlowForward";
  public static final String EVENT_PROP_SLOW_REVERSE = "canPlaySlowReverse";
  public static final String EVENT_PROP_REVERSE = "canPlayReverse";
  public static final String EVENT_PROP_STEP_FORWARD = "canStepForward";
  public static final String EVENT_PROP_STEP_BACKWARD = "canStepBackward";

  public static final String EVENT_PROP_DURATION = "duration";
  public static final String EVENT_PROP_PLAYABLE_DURATION = "playableDuration";
  public static final String EVENT_PROP_SEEKABLE_DURATION = "seekableDuration";
  public static final String EVENT_PROP_CURRENT_TIME = "currentTime";
  public static final String EVENT_PROP_SEEK_TIME = "seekTime";
  public static final String EVENT_PROP_NATURALSIZE = "naturalSize";
  public static final String EVENT_PROP_WIDTH = "width";
  public static final String EVENT_PROP_HEIGHT = "height";
  public static final String EVENT_PROP_ORIENTATION = "orientation";
  public static final String EVENT_PROP_METADATA = "metadata";
  public static final String EVENT_PROP_TARGET = "target";
  public static final String EVENT_PROP_METADATA_IDENTIFIER = "identifier";
  public static final String EVENT_PROP_METADATA_VALUE = "value";

  public static final String EVENT_PROP_ERROR = "error";
  public static final String EVENT_PROP_WHAT = "what";
  public static final String EVENT_PROP_EXTRA = "extra";

  private ThemedReactContext mThemedReactContext;
  private RCTEventEmitter mEventEmitter;

  private MDVRLibrary mMDVRLibrary;

  private Handler mProgressUpdateHandler = new Handler();
  private Runnable mProgressUpdateRunnable = null;
  private TimerTask mProgressTimerTask = null;

  private String mSrcUriString = null;
  private String mSrcType = "mp4";
  private ReadableMap mRequestHeaders = null;
  private boolean mSrcIsNetwork = false;
  private boolean mSrcIsAsset = false;
  private ScalableType mResizeMode = ScalableType.LEFT_TOP;
  private boolean mRepeat = false;
  private boolean mPaused = false;
  private boolean mMuted = false;
  private boolean mPreventsDisplaySleepDuringVideoPlayback = true;
  private float mVolume = 1.0f;
  private float mStereoPan = 0.0f;
  private float mProgressUpdateInterval = 250.0f;
  private float mRate = 1.0f;
  private float mActiveRate = 1.0f;
  private long mSeekTime = 0;
  private boolean mPlayInBackground = false;
  private boolean mBackgroundPaused = false;
  private boolean mIsFullscreen = false;

  private int mMainVer = 0;
  private int mPatchVer = 0;

  private boolean mMediaPlayerValid = false; // True if mMediaPlayer is in prepared, started, paused or completed state.

  private long mVideoDuration = 0;
  private int mVideoBufferedDuration = 0;
  private boolean isCompleted = false;

  public ReactVRVideoView(ThemedReactContext themedReactContext) {
    super(themedReactContext);

    mThemedReactContext = themedReactContext;
    mEventEmitter = themedReactContext.getJSModule(RCTEventEmitter.class);
    themedReactContext.addLifecycleEventListener(this);

    initializeMediaPlayerIfNeeded();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  @SuppressLint("DrawAllocation")
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    if (!changed || !mMediaPlayerValid) {
      return;
    }

    int videoWidth = getVideoWidth();
    int videoHeight = getVideoHeight();

    if (videoWidth == 0 || videoHeight == 0) {
      return;
    }

    Size viewSize = new Size(getWidth(), getHeight());
    Size videoSize = new Size(videoWidth, videoHeight);
    ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
    Matrix matrix = scaleManager.getScaleMatrix(mScalableType);
//        if (matrix != null) {
//            setTransform(matrix);
//        }
  }

  private void initializeMediaPlayerIfNeeded() {
    if (mMediaPlayer == null) {
      mMediaPlayerValid = false;
      mMediaPlayer = new IjkMediaPlayer();
      initMDVRLibrary();
      mMediaPlayer.setOnVideoSizeChangedListener(this);
      mMediaPlayer.setOnErrorListener(this);
      mMediaPlayer.setOnPreparedListener(this);
      mMediaPlayer.setOnBufferingUpdateListener(this);
      mMediaPlayer.setOnSeekCompleteListener(this);
      mMediaPlayer.setOnCompletionListener(this);
      mMediaPlayer.setOnInfoListener(this);
    }
  }

  private void initMDVRLibrary() {
    mMDVRLibrary = MDVRLibrary.with(getContext())
      .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
      .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_TOUCH)
      .projectionMode(MDVRLibrary.PROJECTION_MODE_SPHERE)
      .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
        @Override
        public void onSurfaceReady(Surface surface) {
          mMediaPlayer.setSurface(surface);
        }
      })
      .ifNotSupport(new MDVRLibrary.INotSupportCallback() {
        @Override
        public void onNotSupport(int mode) {
        }
      })
      .pinchConfig(new MDPinchConfig().setMin(1.0f).setMax(8.0f).setDefaultValue(0.1f))
      .pinchEnabled(true)
      .directorFactory(new CustomDirectorFactory())
      .projectionFactory(new CustomProjectionFactory())
      .barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(false).setScale(0.95f))
      .build(this);
    mMDVRLibrary.setAntiDistortionEnabled(false);
  }

  private void initProgress() {
    mProgressTimerTask = new TimerTask() {
      @Override
      public void run() {
        if (mMediaPlayerValid && !isCompleted && !mPaused && !mBackgroundPaused) {
          WritableMap event = Arguments.createMap();
          event.putDouble(EVENT_PROP_CURRENT_TIME, mMediaPlayer.getCurrentPosition() / 1000.0);
          event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedDuration / 1000.0); //TODO:mBufferUpdateRunnable
          event.putDouble(EVENT_PROP_SEEKABLE_DURATION, mVideoDuration / 1000.0);
          Log.d("RNVRVideoView", "run: " + event);
          mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);
        }
      }
    };
    new Timer().schedule(mProgressTimerTask, 0, 100);

    mProgressUpdateRunnable = new Runnable() {
      @Override
      public void run() {
        Log.d("RNVRVideoView", "run: " + mMediaPlayerValid + "" + isCompleted + "" + mPaused + "" + mBackgroundPaused);
        if (mMediaPlayerValid && !isCompleted && !mPaused && !mBackgroundPaused) {
          WritableMap event = Arguments.createMap();
          event.putDouble(EVENT_PROP_CURRENT_TIME, mMediaPlayer.getCurrentPosition() / 1000.0);
          event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedDuration / 1000.0); //TODO:mBufferUpdateRunnable
          event.putDouble(EVENT_PROP_SEEKABLE_DURATION, mVideoDuration / 1000.0);
          mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);

          // Check for update after an interval
          mProgressUpdateHandler.postDelayed(mProgressUpdateRunnable, Math.round(mProgressUpdateInterval));
        }
      }
    };
  }

  public void cleanupMediaPlayerResources() {
    if (mMediaPlayer != null) {
      mMediaPlayerValid = false;
      release();
    }
    if (mIsFullscreen) {
      setFullscreen(false);
    }
    if (mThemedReactContext != null) {
      mThemedReactContext.removeLifecycleEventListener(this);
      mThemedReactContext = null;
    }
  }

  public void setSrc(final String uriString, final String type, final boolean isNetwork, final boolean isAsset, final ReadableMap requestHeaders) {
    setSrc(uriString, type, isNetwork, isAsset, requestHeaders, 0, 0);
  }

  public void setSrc(final String uriString, final String type, final boolean isNetwork, final boolean isAsset, final ReadableMap requestHeaders, final int expansionMainVersion, final int expansionPatchVersion) {

    mSrcUriString = uriString;
    mSrcType = type;
    mSrcIsNetwork = isNetwork;
    mSrcIsAsset = isAsset;
    mRequestHeaders = requestHeaders;
    mMainVer = expansionMainVersion;
    mPatchVer = expansionPatchVersion;


    mMediaPlayerValid = false;
    mVideoDuration = 0;
    mVideoBufferedDuration = 0;

    initializeMediaPlayerIfNeeded();
    mMediaPlayer.reset();

    try {
      if (isNetwork) {
        // Use the shared CookieManager to access the cookies
        // set by WebViews inside the same app
        CookieManager cookieManager = CookieManager.getInstance();

        Uri parsedUrl = Uri.parse(uriString);
        Uri.Builder builtUrl = parsedUrl.buildUpon();

        String cookie = cookieManager.getCookie(builtUrl.build().toString());

        Map<String, String> headers = new HashMap<String, String>();

        if (cookie != null) {
          headers.put("Cookie", cookie);
        }

        if (mRequestHeaders != null) {
          headers.putAll(toStringMap(mRequestHeaders));
        }
        setDataSource(mThemedReactContext, parsedUrl, headers);
      } else if (isAsset) {
        if (uriString.startsWith("content://")) {
          Uri parsedUrl = Uri.parse(uriString);
          setDataSource(mThemedReactContext, parsedUrl);
        } else {
          setDataSource(uriString);
        }
      } else {
        ZipResourceFile expansionFile = null;
        AssetFileDescriptor fd = null;
        if (mMainVer > 0) {
          try {
            expansionFile = APKExpansionSupport.getAPKExpansionZipFile(mThemedReactContext, mMainVer, mPatchVer);
            fd = expansionFile.getAssetFileDescriptor(uriString.replace(".mp4", "") + ".mp4");
          } catch (IOException e) {
            e.printStackTrace();
          } catch (NullPointerException e) {
            e.printStackTrace();
          }
        }
        if (fd == null) {
          int identifier = mThemedReactContext.getResources().getIdentifier(
            uriString,
            "drawable",
            mThemedReactContext.getPackageName()
          );
          if (identifier == 0) {
            identifier = mThemedReactContext.getResources().getIdentifier(
              uriString,
              "raw",
              mThemedReactContext.getPackageName()
            );
          }
          setRawData(identifier);
        } else {
          setDataSource(fd.getFileDescriptor());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    WritableMap src = Arguments.createMap();

    WritableMap wRequestHeaders = Arguments.createMap();
    wRequestHeaders.merge(mRequestHeaders);

    src.putString(ReactVRVideoViewManager.PROP_SRC_URI, uriString);
    src.putString(ReactVRVideoViewManager.PROP_SRC_TYPE, type);
    src.putMap(ReactVRVideoViewManager.PROP_SRC_HEADERS, wRequestHeaders);
    src.putBoolean(ReactVRVideoViewManager.PROP_SRC_IS_NETWORK, isNetwork);
    if (mMainVer > 0) {
      src.putInt(ReactVRVideoViewManager.PROP_SRC_MAINVER, mMainVer);
      if (mPatchVer > 0) {
        src.putInt(ReactVRVideoViewManager.PROP_SRC_PATCHVER, mPatchVer);
      }
    }
    WritableMap event = Arguments.createMap();
    event.putMap(ReactVRVideoViewManager.PROP_SRC, src);
    mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD_START.toString(), event);
    isCompleted = false;

    try {
      prepareAsync(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setResizeModeModifier(final ScalableType resizeMode) {
    mResizeMode = resizeMode;
    if (mMediaPlayerValid) {
      invalidate();
    }
  }

  public void setRepeatModifier(final boolean repeat) {

    mRepeat = repeat;

    if (mMediaPlayerValid) {
      setLooping(repeat);
    }
  }

  public void setPausedModifier(final boolean paused) {
    mPaused = paused;

    if (!mMediaPlayerValid) {
      return;
    }

    if (mPaused) {
      if (mMediaPlayer.isPlaying()) {
        pause();
      }
    } else {
      if (!mMediaPlayer.isPlaying()) {
        start();
        // Setting the rate unpauses, so we have to wait for an unpause
        if (mRate != mActiveRate) {
          setRateModifier(mRate);
        }

        // Also Start the Progress Update Handler
        mProgressUpdateHandler.post(mProgressUpdateRunnable);
      }
    }
    setKeepScreenOn(!mPaused && mPreventsDisplaySleepDuringVideoPlayback);
  }

  // reduces the volume based on stereoPan
  private float calulateRelativeVolume() {
    float relativeVolume = (mVolume * (1 - Math.abs(mStereoPan)));
    // only one decimal allowed
    BigDecimal roundRelativeVolume = new BigDecimal(relativeVolume).setScale(1, BigDecimal.ROUND_HALF_UP);
    return roundRelativeVolume.floatValue();
  }

  public void setPreventsDisplaySleepDuringVideoPlaybackModifier(final boolean preventsDisplaySleepDuringVideoPlayback) {
    mPreventsDisplaySleepDuringVideoPlayback = preventsDisplaySleepDuringVideoPlayback;

    if (!mMediaPlayerValid) {
      return;
    }

    mMediaPlayer.setScreenOnWhilePlaying(mPreventsDisplaySleepDuringVideoPlayback);
    setKeepScreenOn(mPreventsDisplaySleepDuringVideoPlayback);
  }

  public void setMutedModifier(final boolean muted) {
    mMuted = muted;

    if (!mMediaPlayerValid) {
      return;
    }

    if (mMuted) {
      setVolume(0, 0);
    } else if (mStereoPan < 0) {
      // louder on the left channel
      setVolume(mVolume, calulateRelativeVolume());
    } else if (mStereoPan > 0) {
      // louder on the right channel
      setVolume(calulateRelativeVolume(), mVolume);
    } else {
      // same volume on both channels
      setVolume(mVolume, mVolume);
    }
  }

  public void setVolumeModifier(final float volume) {
    mVolume = volume;
    setMutedModifier(mMuted);
  }

  public void setStereoPan(final float stereoPan) {
    mStereoPan = stereoPan;
    setMutedModifier(mMuted);
  }

  public void setProgressUpdateInterval(final float progressUpdateInterval) {
    mProgressUpdateInterval = progressUpdateInterval;
  }

  public void setRateModifier(final float rate) {
    mRate = rate;
  }

  public void setFullscreen(boolean isFullscreen) {
    if (isFullscreen == mIsFullscreen) {
      return; // Avoid generating events when nothing is changing
    }
    mIsFullscreen = isFullscreen;

    Activity activity = mThemedReactContext.getCurrentActivity();
    if (activity == null) {
      return;
    }
    Window window = activity.getWindow();
    View decorView = window.getDecorView();
    int uiOptions;
    if (mIsFullscreen) {
      if (Build.VERSION.SDK_INT >= 19) { // 4.4+
        uiOptions = SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | SYSTEM_UI_FLAG_IMMERSIVE_STICKY
          | SYSTEM_UI_FLAG_FULLSCREEN;
      } else {
        uiOptions = SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | SYSTEM_UI_FLAG_FULLSCREEN;
      }
      mEventEmitter.receiveEvent(getId(), Events.EVENT_FULLSCREEN_WILL_PRESENT.toString(), null);
      decorView.setSystemUiVisibility(uiOptions);
      mEventEmitter.receiveEvent(getId(), Events.EVENT_FULLSCREEN_DID_PRESENT.toString(), null);
    } else {
      uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
      mEventEmitter.receiveEvent(getId(), Events.EVENT_FULLSCREEN_WILL_DISMISS.toString(), null);
      decorView.setSystemUiVisibility(uiOptions);
      mEventEmitter.receiveEvent(getId(), Events.EVENT_FULLSCREEN_DID_DISMISS.toString(), null);
    }
  }

  public void applyModifiers() {
    setResizeModeModifier(mResizeMode);
    setRepeatModifier(mRepeat);
    setPausedModifier(mPaused);
    setMutedModifier(mMuted);
    setPreventsDisplaySleepDuringVideoPlaybackModifier(mPreventsDisplaySleepDuringVideoPlayback);
    setProgressUpdateInterval(mProgressUpdateInterval);
    setRateModifier(mRate);
  }

  public void setPlayInBackground(final boolean playInBackground) {
    mPlayInBackground = playInBackground;
  }

  @Override
  public void onPrepared(IMediaPlayer mp) {

    mMediaPlayerValid = true;
    mVideoDuration = mp.getDuration();

    WritableMap naturalSize = Arguments.createMap();
    naturalSize.putInt(EVENT_PROP_WIDTH, mp.getVideoWidth());
    naturalSize.putInt(EVENT_PROP_HEIGHT, mp.getVideoHeight());
    if (mp.getVideoWidth() > mp.getVideoHeight())
      naturalSize.putString(EVENT_PROP_ORIENTATION, "landscape");
    else
      naturalSize.putString(EVENT_PROP_ORIENTATION, "portrait");

    WritableMap event = Arguments.createMap();
    event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
    event.putDouble(EVENT_PROP_CURRENT_TIME, mp.getCurrentPosition() / 1000.0);
    event.putMap(EVENT_PROP_NATURALSIZE, naturalSize);
    // TODO: Actually check if you can.
    event.putBoolean(EVENT_PROP_FAST_FORWARD, true);
    event.putBoolean(EVENT_PROP_SLOW_FORWARD, true);
    event.putBoolean(EVENT_PROP_SLOW_REVERSE, true);
    event.putBoolean(EVENT_PROP_REVERSE, true);
    event.putBoolean(EVENT_PROP_FAST_FORWARD, true);
    event.putBoolean(EVENT_PROP_STEP_BACKWARD, true);
    event.putBoolean(EVENT_PROP_STEP_FORWARD, true);
    mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD.toString(), event);
    applyModifiers();
    initProgress();
  }

  @Override
  public boolean onError(IMediaPlayer mp, int what, int extra) {

    WritableMap error = Arguments.createMap();
    error.putInt(EVENT_PROP_WHAT, what);
    error.putInt(EVENT_PROP_EXTRA, extra);
    WritableMap event = Arguments.createMap();
    event.putMap(EVENT_PROP_ERROR, error);
    mEventEmitter.receiveEvent(getId(), Events.EVENT_ERROR.toString(), event);
    return true;
  }

  @Override
  public boolean onInfo(IMediaPlayer mp, int what, int extra) {
    switch (what) {
      case MediaPlayer.MEDIA_INFO_BUFFERING_START:
        mEventEmitter.receiveEvent(getId(), Events.EVENT_STALLED.toString(), Arguments.createMap());
        break;
      case MediaPlayer.MEDIA_INFO_BUFFERING_END:
        mEventEmitter.receiveEvent(getId(), Events.EVENT_RESUME.toString(), Arguments.createMap());
        break;
      case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
        mEventEmitter.receiveEvent(getId(), Events.EVENT_READY_FOR_DISPLAY.toString(), Arguments.createMap());
        break;

      default:
    }
    return false;
  }

  @Override
  public void onBufferingUpdate(IMediaPlayer mp, int percent) {
//        Log.d("RNVRVideoView", "onBufferingUpdate: " + percent);
    mVideoBufferedDuration = (int) Math.round((double) (mVideoDuration * percent) / 100.0);
  }


  @Override
  public void onSeekComplete(IMediaPlayer iMediaPlayer) {
    WritableMap event = Arguments.createMap();
    event.putDouble(EVENT_PROP_CURRENT_TIME, getCurrentPosition() / 1000.0);
    event.putDouble(EVENT_PROP_SEEK_TIME, mSeekTime / 1000.0);
    mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK.toString(), event);
    mSeekTime = 0;
  }

  @Override
  public void seekTo(int msec) {
    if (mMediaPlayerValid) {
      mSeekTime = msec;
      super.seekTo(msec);
      if (isCompleted && mVideoDuration != 0 && msec < mVideoDuration) {
        isCompleted = false;
      }
    }
  }

  @Override
  public void onCompletion(IMediaPlayer mp) {
    isCompleted = true;
    mEventEmitter.receiveEvent(getId(), Events.EVENT_END.toString(), null);
    if (!mRepeat) {
      setKeepScreenOn(false);
    }
  }

  // This is not fully tested and does not work for all forms of timed metadata
  @TargetApi(23) // 6.0
  public class TimedMetaDataAvailableListener
    implements MediaPlayer.OnTimedMetaDataAvailableListener {
    public void onTimedMetaDataAvailable(MediaPlayer mp, TimedMetaData data) {
      WritableMap event = Arguments.createMap();

      try {
        String rawMeta = new String(data.getMetaData(), "UTF-8");
        WritableMap id3 = Arguments.createMap();

        id3.putString(EVENT_PROP_METADATA_VALUE, rawMeta.substring(rawMeta.lastIndexOf("\u0003") + 1));
        id3.putString(EVENT_PROP_METADATA_IDENTIFIER, "id3/TDEN");

        WritableArray metadata = new WritableNativeArray();

        metadata.pushMap(id3);

        event.putArray(EVENT_PROP_METADATA, metadata);
        event.putDouble(EVENT_PROP_TARGET, getId());
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }

      mEventEmitter.receiveEvent(getId(), Events.EVENT_TIMED_METADATA.toString(), event);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    mMediaPlayerValid = false;
    super.onDetachedFromWindow();
    setKeepScreenOn(false);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    if (mMainVer > 0) {
      setSrc(mSrcUriString, mSrcType, mSrcIsNetwork, mSrcIsAsset, mRequestHeaders, mMainVer, mPatchVer);
    } else {
      setSrc(mSrcUriString, mSrcType, mSrcIsNetwork, mSrcIsAsset, mRequestHeaders);
    }
    setKeepScreenOn(mPreventsDisplaySleepDuringVideoPlayback);
  }

  @Override
  public void onHostPause() {
    if (mMediaPlayerValid && !mPaused && !mPlayInBackground) {
      /* Pause the video in background
       * Don't update the paused prop, developers should be able to update it on background
       *  so that when you return to the app the video is paused
       */
      mBackgroundPaused = true;
      if (mMDVRLibrary != null)
        mMDVRLibrary.onPause(getContext());
      mMediaPlayer.pause();
    }
  }

  @Override
  public void onHostResume() {
    if (mMDVRLibrary != null)
      mMDVRLibrary.onResume(getContext());
    mBackgroundPaused = false;
    if (mMediaPlayerValid && !mPlayInBackground && !mPaused) {
      new Handler().post(new Runnable() {
        @Override
        public void run() {
          // Restore original state
          setPausedModifier(false);
        }
      });
    }
  }

  @Override
  public void onHostDestroy() {
    if (mMDVRLibrary != null)
      mMDVRLibrary.onDestroy();
  }

  /**
   * toStringMap converts a {@link ReadableMap} into a HashMap.
   *
   * @param readableMap The ReadableMap to be conveted.
   * @return A HashMap containing the data that was in the ReadableMap.
   * @see 'Adapted from https://github.com/artemyarulin/react-native-eval/blob/master/android/src/main/java/com/evaluator/react/ConversionUtil.java'
   */
  public static Map<String, String> toStringMap(@Nullable ReadableMap readableMap) {
    Map<String, String> result = new HashMap<>();
    if (readableMap == null)
      return result;

    com.facebook.react.bridge.ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      result.put(key, readableMap.getString(key));
    }

    return result;
  }
}
