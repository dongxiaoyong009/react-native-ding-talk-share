Pod::Spec.new do |s|
  s.name             = 'RNDingTalkShare'
  s.version          = '0.1.1'
  s.summary          = 'react-native-ding-talk-share'

  s.description      = <<-DESC
TODO: Add long description of the pod here.
                       DESC

  s.homepage     = "https://github.com/shimohq/react-native-ding-talk-share"
  s.license      = { :type => 'MIT', :file => 'LICENSE' }
  s.author       = { 'lisong' => 'lisong@shimo.im' }
  s.source       = { :git => "https://github.com/shimohq/react-native-ding-talk-share.git", :tag => s.version.to_s }

  s.ios.deployment_target = '8.0'
  
  s.source_files  = "ios/*.{h,m,mm}"
  
  s.dependency 'React'
  s.dependency 'DTShareKit'

end
