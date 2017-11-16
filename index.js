import { NativeModules } from 'react-native';

const { RNDingTalkShareModule } = NativeModules;

export default RNDingTalkShareModule;

export function isSupported() {
  return RNDingTalkShareModule && RNDingTalkShareModule.isSupported();
}

export function isInstalled() {
  return RNDingTalkShareModule && RNDingTalkShareModule.isInstalled();
}

export function shareWebPage(url, thumbImage, title, content) {
  return RNDingTalkShareModule && RNDingTalkShareModule.shareWebPage(url, thumbImage, title, content);
}

export function shareImage(image) {
  return RNDingTalkShareModule && RNDingTalkShareModule.shareImage(image);
}

