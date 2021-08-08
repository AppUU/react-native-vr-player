"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "TextTrackType", {
  enumerable: true,
  get: function () {
    return _TextTrackType.default;
  }
});
Object.defineProperty(exports, "FilterType", {
  enumerable: true,
  get: function () {
    return _FilterType.default;
  }
});
Object.defineProperty(exports, "DRMType", {
  enumerable: true,
  get: function () {
    return _DRMType.default;
  }
});
Object.defineProperty(exports, "VRFilterType", {
  enumerable: true,
  get: function () {
    return _VRFilterType.default;
  }
});
exports.default = void 0;

var _react = _interopRequireWildcard(require("react"));

var _propTypes = _interopRequireDefault(require("prop-types"));

var _reactNative = require("react-native");

var _resolveAssetSource = _interopRequireDefault(require("react-native/Libraries/Image/resolveAssetSource"));

var _TextTrackType = _interopRequireDefault(require("./TextTrackType"));

var _FilterType = _interopRequireDefault(require("./FilterType"));

var _DRMType = _interopRequireDefault(require("./DRMType"));

var _VideoResizeMode = _interopRequireDefault(require("./VideoResizeMode"));

var _VRFilterType = _interopRequireDefault(require("./VRFilterType"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _getRequireWildcardCache(nodeInterop) { if (typeof WeakMap !== "function") return null; var cacheBabelInterop = new WeakMap(); var cacheNodeInterop = new WeakMap(); return (_getRequireWildcardCache = function (nodeInterop) { return nodeInterop ? cacheNodeInterop : cacheBabelInterop; })(nodeInterop); }

function _interopRequireWildcard(obj, nodeInterop) { if (!nodeInterop && obj && obj.__esModule) { return obj; } if (obj === null || typeof obj !== "object" && typeof obj !== "function") { return { default: obj }; } var cache = _getRequireWildcardCache(nodeInterop); if (cache && cache.has(obj)) { return cache.get(obj); } var newObj = {}; var hasPropertyDescriptor = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var key in obj) { if (key !== "default" && Object.prototype.hasOwnProperty.call(obj, key)) { var desc = hasPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : null; if (desc && (desc.get || desc.set)) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } newObj.default = obj; if (cache) { cache.set(obj, newObj); } return newObj; }

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

const styles = _reactNative.StyleSheet.create({
  base: {
    overflow: 'hidden'
  }
});

class VRVideo extends _react.Component {
  constructor(props) {
    super(props);

    _defineProperty(this, "seek", (time, tolerance = 100) => {
      if (isNaN(time)) {
        throw new Error('Specified time is not a number');
      }

      if (_reactNative.Platform.OS === 'ios') {
        this.setNativeProps({
          seek: {
            time,
            tolerance
          }
        });
      } else {
        this.setNativeProps({
          seek: time
        });
      }
    });

    _defineProperty(this, "presentFullscreenPlayer", () => {
      this.setNativeProps({
        fullscreen: true
      });
    });

    _defineProperty(this, "dismissFullscreenPlayer", () => {
      this.setNativeProps({
        fullscreen: false
      });
    });

    _defineProperty(this, "save", async options => {
      return await _reactNative.NativeModules.VideoManager.save(options, (0, _reactNative.findNodeHandle)(this._root));
    });

    _defineProperty(this, "restoreUserInterfaceForPictureInPictureStopCompleted", restored => {
      this.setNativeProps({
        restoreUserInterfaceForPIPStopCompletionHandler: restored
      });
    });

    _defineProperty(this, "_assignRoot", component => {
      this._root = component;
    });

    _defineProperty(this, "_hidePoster", () => {
      if (this.state.showPoster) {
        this.setState({
          showPoster: false
        });
      }
    });

    _defineProperty(this, "_onLoadStart", event => {
      if (this.props.onLoadStart) {
        this.props.onLoadStart(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onLoad", event => {
      // Need to hide poster here for windows as onReadyForDisplay is not implemented
      if (_reactNative.Platform.OS === 'windows') {
        this._hidePoster();
      }

      if (this.props.onLoad) {
        this.props.onLoad(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onError", event => {
      if (this.props.onError) {
        this.props.onError(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onProgress", event => {
      if (this.props.onProgress) {
        this.props.onProgress(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onBandwidthUpdate", event => {
      if (this.props.onBandwidthUpdate) {
        this.props.onBandwidthUpdate(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onSeek", event => {
      if (this.props.onSeek) {
        this.props.onSeek(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onEnd", event => {
      if (this.props.onEnd) {
        this.props.onEnd(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onTimedMetadata", event => {
      if (this.props.onTimedMetadata) {
        this.props.onTimedMetadata(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onFullscreenPlayerWillPresent", event => {
      if (this.props.onFullscreenPlayerWillPresent) {
        this.props.onFullscreenPlayerWillPresent(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onFullscreenPlayerDidPresent", event => {
      if (this.props.onFullscreenPlayerDidPresent) {
        this.props.onFullscreenPlayerDidPresent(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onFullscreenPlayerWillDismiss", event => {
      if (this.props.onFullscreenPlayerWillDismiss) {
        this.props.onFullscreenPlayerWillDismiss(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onFullscreenPlayerDidDismiss", event => {
      if (this.props.onFullscreenPlayerDidDismiss) {
        this.props.onFullscreenPlayerDidDismiss(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onReadyForDisplay", event => {
      if (!this.props.audioOnly) {
        this._hidePoster();
      }

      if (this.props.onReadyForDisplay) {
        this.props.onReadyForDisplay(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onPlaybackStalled", event => {
      if (this.props.onPlaybackStalled) {
        this.props.onPlaybackStalled(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onPlaybackResume", event => {
      if (this.props.onPlaybackResume) {
        this.props.onPlaybackResume(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onPlaybackRateChange", event => {
      if (this.props.onPlaybackRateChange) {
        this.props.onPlaybackRateChange(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onExternalPlaybackChange", event => {
      if (this.props.onExternalPlaybackChange) {
        this.props.onExternalPlaybackChange(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onAudioBecomingNoisy", () => {
      if (this.props.onAudioBecomingNoisy) {
        this.props.onAudioBecomingNoisy();
      }
    });

    _defineProperty(this, "_onPictureInPictureStatusChanged", event => {
      if (this.props.onPictureInPictureStatusChanged) {
        this.props.onPictureInPictureStatusChanged(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onRestoreUserInterfaceForPictureInPictureStop", event => {
      if (this.props.onRestoreUserInterfaceForPictureInPictureStop) {
        this.props.onRestoreUserInterfaceForPictureInPictureStop();
      }
    });

    _defineProperty(this, "_onAudioFocusChanged", event => {
      if (this.props.onAudioFocusChanged) {
        this.props.onAudioFocusChanged(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onBuffer", event => {
      if (this.props.onBuffer) {
        this.props.onBuffer(event.nativeEvent);
      }
    });

    _defineProperty(this, "_onGetLicense", event => {
      if (this.props.drm && this.props.drm.getLicense instanceof Function) {
        const data = event.nativeEvent;

        if (data && data.spc) {
          const getLicenseOverride = this.props.drm.getLicense(data.spc, data.contentId, data.spcBase64, this.props);
          const getLicensePromise = Promise.resolve(getLicenseOverride); // Handles both scenarios, getLicenseOverride being a promise and not.

          getLicensePromise.then(result => {
            if (result !== undefined) {
              _reactNative.NativeModules.VideoManager.setLicenseResult(result, (0, _reactNative.findNodeHandle)(this._root));
            } else {
              _reactNative.NativeModules.VideoManager.setLicenseError && _reactNative.NativeModules.VideoManager.setLicenseError('Empty license result', (0, _reactNative.findNodeHandle)(this._root));
            }
          }).catch(error => {
            _reactNative.NativeModules.VideoManager.setLicenseError && _reactNative.NativeModules.VideoManager.setLicenseError(error, (0, _reactNative.findNodeHandle)(this._root));
          });
        } else {
          _reactNative.NativeModules.VideoManager.setLicenseError && _reactNative.NativeModules.VideoManager.setLicenseError('No spc received', (0, _reactNative.findNodeHandle)(this._root));
        }
      }
    });

    _defineProperty(this, "getViewManagerConfig", viewManagerName => {
      if (!_reactNative.NativeModules.UIManager.getViewManagerConfig) {
        return _reactNative.NativeModules.UIManager[viewManagerName];
      }

      return _reactNative.NativeModules.UIManager.getViewManagerConfig(viewManagerName);
    });

    this.state = {
      showPoster: !!props.poster
    };
  }

  setNativeProps(nativeProps) {
    this._root.setNativeProps(nativeProps);
  }

  toTypeString(x) {
    switch (typeof x) {
      case 'object':
        return x instanceof Date ? x.toISOString() : JSON.stringify(x);
      // object, null

      case 'undefined':
        return '';

      default:
        // boolean, number, string
        return x.toString();
    }
  }

  stringsOnlyObject(obj) {
    const strObj = {};
    Object.keys(obj).forEach(x => {
      strObj[x] = this.toTypeString(obj[x]);
    });
    return strObj;
  }

  render() {
    const resizeMode = this.props.resizeMode;
    const source = (0, _resolveAssetSource.default)(this.props.source) || {};
    const shouldCache = !source.__packager_asset;
    let uri = source.uri || '';

    if (uri && uri.match(/^\//)) {
      uri = `file://${uri}`;
    }

    if (!uri) {
      console.warn('Trying to load empty source.');
    }

    const isNetwork = !!(uri && uri.match(/^https?:/));
    const isAsset = !!(uri && uri.match(/^(assets-library|ipod-library|file|content|ms-appx|ms-appdata):/));
    let nativeResizeMode;
    const RCTVideoInstance = this.getViewManagerConfig('RCTVRVideo');

    if (resizeMode === _VideoResizeMode.default.stretch) {
      nativeResizeMode = RCTVideoInstance.Constants.ScaleToFill;
    } else if (resizeMode === _VideoResizeMode.default.contain) {
      nativeResizeMode = RCTVideoInstance.Constants.ScaleAspectFit;
    } else if (resizeMode === _VideoResizeMode.default.cover) {
      nativeResizeMode = RCTVideoInstance.Constants.ScaleAspectFill;
    } else {
      nativeResizeMode = RCTVideoInstance.Constants.ScaleNone;
    }

    const nativeProps = Object.assign({}, this.props);
    Object.assign(nativeProps, {
      style: [styles.base, nativeProps.style],
      resizeMode: nativeResizeMode,
      src: {
        uri,
        isNetwork,
        isAsset,
        shouldCache,
        type: source.type || '',
        mainVer: source.mainVer || 0,
        patchVer: source.patchVer || 0,
        requestHeaders: source.headers ? this.stringsOnlyObject(source.headers) : {}
      },
      onVideoLoadStart: this._onLoadStart,
      onVideoLoad: this._onLoad,
      onVideoError: this._onError,
      onVideoProgress: this._onProgress,
      onVideoSeek: this._onSeek,
      onVideoEnd: this._onEnd,
      onVideoBuffer: this._onBuffer,
      onVideoBandwidthUpdate: this._onBandwidthUpdate,
      onTimedMetadata: this._onTimedMetadata,
      onVideoAudioBecomingNoisy: this._onAudioBecomingNoisy,
      onVideoExternalPlaybackChange: this._onExternalPlaybackChange,
      onVideoFullscreenPlayerWillPresent: this._onFullscreenPlayerWillPresent,
      onVideoFullscreenPlayerDidPresent: this._onFullscreenPlayerDidPresent,
      onVideoFullscreenPlayerWillDismiss: this._onFullscreenPlayerWillDismiss,
      onVideoFullscreenPlayerDidDismiss: this._onFullscreenPlayerDidDismiss,
      onReadyForDisplay: this._onReadyForDisplay,
      onPlaybackStalled: this._onPlaybackStalled,
      onPlaybackResume: this._onPlaybackResume,
      onPlaybackRateChange: this._onPlaybackRateChange,
      onAudioFocusChanged: this._onAudioFocusChanged,
      onAudioBecomingNoisy: this._onAudioBecomingNoisy,
      onGetLicense: nativeProps.drm && nativeProps.drm.getLicense && this._onGetLicense,
      onPictureInPictureStatusChanged: this._onPictureInPictureStatusChanged,
      onRestoreUserInterfaceForPictureInPictureStop: this._onRestoreUserInterfaceForPictureInPictureStop
    });
    const posterStyle = { ..._reactNative.StyleSheet.absoluteFillObject,
      resizeMode: this.props.posterResizeMode || 'contain'
    };
    return /*#__PURE__*/_react.default.createElement(_reactNative.View, {
      style: nativeProps.style
    }, /*#__PURE__*/_react.default.createElement(RCTVRVideo, _extends({
      ref: this._assignRoot
    }, nativeProps, {
      style: _reactNative.StyleSheet.absoluteFill
    })), this.state.showPoster && /*#__PURE__*/_react.default.createElement(_reactNative.Image, {
      style: posterStyle,
      source: {
        uri: this.props.poster
      }
    }));
  }

}

exports.default = VRVideo;
VRVideo.propTypes = {
  /**android vr only */
  switchInteractiveMode: _propTypes.default.oneOf([_VRFilterType.default.INTERACTIVE_MODE_CARDBORAD_MOTION, _VRFilterType.default.INTERACTIVE_MODE_CARDBORAD_MOTION_WITH_TOUCH, _VRFilterType.default.INTERACTIVE_MODE_TOUCH, _VRFilterType.default.INTERACTIVE_MODE_MOTION, _VRFilterType.default.INTERACTIVE_MODE_MOTION_WITH_TOUCH]),
  switchDisplayMode: _propTypes.default.oneOf([_VRFilterType.default.DISPLAY_MODE_GLASS, _VRFilterType.default.DISPLAY_MODE_NORMAL]),
  switchProjectionMode: _propTypes.default.oneOf([_VRFilterType.default.PROJECTION_MODE_SPHERE, _VRFilterType.default.PROJECTION_MODE_CUBE]),
  setAntiDistortionEnabled: _propTypes.default.bool,
  filter: _propTypes.default.oneOf([_FilterType.default.NONE, _FilterType.default.INVERT, _FilterType.default.MONOCHROME, _FilterType.default.POSTERIZE, _FilterType.default.FALSE, _FilterType.default.MAXIMUMCOMPONENT, _FilterType.default.MINIMUMCOMPONENT, _FilterType.default.CHROME, _FilterType.default.FADE, _FilterType.default.INSTANT, _FilterType.default.MONO, _FilterType.default.NOIR, _FilterType.default.PROCESS, _FilterType.default.TONAL, _FilterType.default.TRANSFER, _FilterType.default.SEPIA]),
  filterEnabled: _propTypes.default.bool,

  /**android vr only */

  /**ios vr only */
  //TODO

  /**ios vr only */

  /* Native only */
  src: _propTypes.default.object,
  seek: _propTypes.default.oneOfType([_propTypes.default.number, _propTypes.default.object]),
  fullscreen: _propTypes.default.bool,
  onVideoLoadStart: _propTypes.default.func,
  onVideoLoad: _propTypes.default.func,
  onVideoBuffer: _propTypes.default.func,
  onVideoError: _propTypes.default.func,
  onVideoProgress: _propTypes.default.func,
  onVideoBandwidthUpdate: _propTypes.default.func,
  onVideoSeek: _propTypes.default.func,
  onVideoEnd: _propTypes.default.func,
  onTimedMetadata: _propTypes.default.func,
  onVideoAudioBecomingNoisy: _propTypes.default.func,
  onVideoExternalPlaybackChange: _propTypes.default.func,
  onVideoFullscreenPlayerWillPresent: _propTypes.default.func,
  onVideoFullscreenPlayerDidPresent: _propTypes.default.func,
  onVideoFullscreenPlayerWillDismiss: _propTypes.default.func,
  onVideoFullscreenPlayerDidDismiss: _propTypes.default.func,

  /* Wrapper component */
  source: _propTypes.default.oneOfType([_propTypes.default.shape({
    uri: _propTypes.default.string
  }), // Opaque type returned by require('./video.mp4')
  _propTypes.default.number]),
  drm: _propTypes.default.shape({
    type: _propTypes.default.oneOf([_DRMType.default.CLEARKEY, _DRMType.default.FAIRPLAY, _DRMType.default.WIDEVINE, _DRMType.default.PLAYREADY]),
    licenseServer: _propTypes.default.string,
    headers: _propTypes.default.shape({}),
    base64Certificate: _propTypes.default.bool,
    certificateUrl: _propTypes.default.string,
    getLicense: _propTypes.default.func
  }),
  minLoadRetryCount: _propTypes.default.number,
  maxBitRate: _propTypes.default.number,
  resizeMode: _propTypes.default.string,
  poster: _propTypes.default.string,
  posterResizeMode: _reactNative.Image.propTypes.resizeMode,
  repeat: _propTypes.default.bool,
  automaticallyWaitsToMinimizeStalling: _propTypes.default.bool,
  allowsExternalPlayback: _propTypes.default.bool,
  selectedAudioTrack: _propTypes.default.shape({
    type: _propTypes.default.string.isRequired,
    value: _propTypes.default.oneOfType([_propTypes.default.string, _propTypes.default.number])
  }),
  selectedVideoTrack: _propTypes.default.shape({
    type: _propTypes.default.string.isRequired,
    value: _propTypes.default.oneOfType([_propTypes.default.string, _propTypes.default.number])
  }),
  selectedTextTrack: _propTypes.default.shape({
    type: _propTypes.default.string.isRequired,
    value: _propTypes.default.oneOfType([_propTypes.default.string, _propTypes.default.number])
  }),
  textTracks: _propTypes.default.arrayOf(_propTypes.default.shape({
    title: _propTypes.default.string,
    uri: _propTypes.default.string.isRequired,
    type: _propTypes.default.oneOf([_TextTrackType.default.SRT, _TextTrackType.default.TTML, _TextTrackType.default.VTT]),
    language: _propTypes.default.string.isRequired
  })),
  paused: _propTypes.default.bool,
  muted: _propTypes.default.bool,
  volume: _propTypes.default.number,
  bufferConfig: _propTypes.default.shape({
    minBufferMs: _propTypes.default.number,
    maxBufferMs: _propTypes.default.number,
    bufferForPlaybackMs: _propTypes.default.number,
    bufferForPlaybackAfterRebufferMs: _propTypes.default.number
  }),
  stereoPan: _propTypes.default.number,
  rate: _propTypes.default.number,
  pictureInPicture: _propTypes.default.bool,
  playInBackground: _propTypes.default.bool,
  preferredForwardBufferDuration: _propTypes.default.number,
  playWhenInactive: _propTypes.default.bool,
  ignoreSilentSwitch: _propTypes.default.oneOf(['ignore', 'obey']),
  reportBandwidth: _propTypes.default.bool,
  disableFocus: _propTypes.default.bool,
  controls: _propTypes.default.bool,
  audioOnly: _propTypes.default.bool,
  currentTime: _propTypes.default.number,
  fullscreenAutorotate: _propTypes.default.bool,
  fullscreenOrientation: _propTypes.default.oneOf(['all', 'landscape', 'portrait']),
  progressUpdateInterval: _propTypes.default.number,
  useTextureView: _propTypes.default.bool,
  hideShutterView: _propTypes.default.bool,
  onLoadStart: _propTypes.default.func,
  onLoad: _propTypes.default.func,
  onBuffer: _propTypes.default.func,
  onError: _propTypes.default.func,
  onProgress: _propTypes.default.func,
  onBandwidthUpdate: _propTypes.default.func,
  onSeek: _propTypes.default.func,
  onEnd: _propTypes.default.func,
  onFullscreenPlayerWillPresent: _propTypes.default.func,
  onFullscreenPlayerDidPresent: _propTypes.default.func,
  onFullscreenPlayerWillDismiss: _propTypes.default.func,
  onFullscreenPlayerDidDismiss: _propTypes.default.func,
  onReadyForDisplay: _propTypes.default.func,
  onPlaybackStalled: _propTypes.default.func,
  onPlaybackResume: _propTypes.default.func,
  onPlaybackRateChange: _propTypes.default.func,
  onAudioFocusChanged: _propTypes.default.func,
  onAudioBecomingNoisy: _propTypes.default.func,
  onPictureInPictureStatusChanged: _propTypes.default.func,
  needsToRestoreUserInterfaceForPictureInPictureStop: _propTypes.default.func,
  onExternalPlaybackChange: _propTypes.default.func,

  /* Required by react-native */
  scaleX: _propTypes.default.number,
  scaleY: _propTypes.default.number,
  translateX: _propTypes.default.number,
  translateY: _propTypes.default.number,
  rotation: _propTypes.default.number,
  ..._reactNative.ViewPropTypes
};
const RCTVRVideo = (0, _reactNative.requireNativeComponent)('RCTVRVideo', VRVideo, {
  nativeOnly: {
    src: true,
    seek: true,
    fullscreen: true
  }
});
//# sourceMappingURL=VRVideo.js.map