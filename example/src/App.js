import * as React from 'react';

import { StyleSheet, Button, View, Dimensions } from 'react-native';
import { VRVideo } from '../..';

export default function App() {
  const videoRef = React.useRef();
  const [] = React.useState(false);

  return (
    <View style={styles.container}>
      {/* <TestView /> */}
      <VRVideo
        ref={videoRef}
        style={styles.box}
        source={{ uri: 'http://allabc.jinshunkj.com/course/gkk/VR0136G.mp4' }}
        onLoadStart={(e) => console.log(`onLoadStart=>${JSON.stringify(e)}`)}
        onLoad={(e) => console.log(`onLoad=>${JSON.stringify(e)}`)}
        onBuffer={(e) => console.log(`onBuffer=>${JSON.stringify(e)}`)}
        onError={(e) => console.log(`onError=>${JSON.stringify(e)}`)}
        onProgress={(e) => console.log(`onProgress=>${JSON.stringify(e)}`)}
        onBandwidthUpdate={(e) =>
          console.log(`onBandwidthUpdate=>${JSON.stringify(e)}`)
        }
        onEnd={(e) => console.log(`onEnd=>${JSON.stringify(e)}`)}
        onFullscreenPlayerWillPresent={(e) =>
          console.log(`onFullscreenPlayerWillPresent=>${JSON.stringify(e)}`)
        }
        onFullscreenPlayerDidPresent={(e) =>
          console.log(`onFullscreenPlayerDidPresent=>${JSON.stringify(e)}`)
        }
        onFullscreenPlayerWillDismiss={(e) =>
          console.log(`onFullscreenPlayerWillDismiss=>${JSON.stringify(e)}`)
        }
        onFullscreenPlayerDidDismiss={(e) =>
          console.log(`onFullscreenPlayerDidDismiss=>${JSON.stringify(e)}`)
        }
        onReadyForDisplay={(e) =>
          console.log(`onReadyForDisplay=>${JSON.stringify(e)}`)
        }
        onPlaybackStalled={(e) =>
          console.log(`onPlaybackStalled=>${JSON.stringify(e)}`)
        }
        onPlaybackResume={(e) =>
          console.log(`onPlaybackResume=>${JSON.stringify(e)}`)
        }
        onPlaybackRateChange={(e) =>
          console.log(`onPlaybackRateChange=>${JSON.stringify(e)}`)
        }
        onPictureInPictureStatusChanged={(e) =>
          console.log(`onPictureInPictureStatusChanged=>${JSON.stringify(e)}`)
        }
        onExternalPlaybackChange={(e) =>
          console.log(`onExternalPlaybackChange=>${JSON.stringify(e)}`)
        }
      />
      <Button
        title={'全屏点击'}
        onPress={() => {
          videoRef?.current?.presentFullscreenPlayer();
          console.log(videoRef);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: Dimensions.get('screen').width,
    height: 250,
    marginVertical: 20,
  },
});
