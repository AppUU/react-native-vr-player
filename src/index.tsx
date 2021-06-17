import { requireNativeComponent, ViewStyle } from 'react-native';

type VrPlayerProps = {
  color: string;
  style: ViewStyle;
};


export const VrPlayerViewManager = requireNativeComponent<VrPlayerProps>(
  'VrPlayerView'
);

export default VrPlayerViewManager;
