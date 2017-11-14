import { NativeModules } from 'react-native';

const { RNDingTalkShare } = NativeModules;

export default RNDingTalkShare;

export function shareWebPage(url, thumbImage, title, content) {
  return RNDingTalkShare && RNDingTalkShare.shareWebPage(text, shareScene);
}
