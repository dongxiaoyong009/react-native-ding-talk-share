/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  NativeModules
} from 'react-native';

const { RNDingTalkShareModule } = NativeModules;

export default class App extends Component<{}> {

  _share = async () => {
    console.log('App', RNDingTalkShareModule.share);
    const result = await RNDingTalkShareModule.share('https://shimo.im/',
      null,
      'https://pbs.twimg.com/profile_images/781793252995325956/CP2cCxe-_400x400.jpg',
      'Test Title',
      'Test Content');
  };

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>
          Ding Talk Share Demo
        </Text>
        <TouchableOpacity style={styles.share}>
          <Text style={styles.shareText}
                onPress={this._share}>
            Share To Ding Talk
          </Text>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  title: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  share: {
    margin: 10,
  },
  shareText: {
    textAlign: 'center',
    color: '#333333',
  }
});
