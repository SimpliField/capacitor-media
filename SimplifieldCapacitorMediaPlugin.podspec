require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name = 'SimplifieldCapacitorMediaPlugin'
  s.version = package['version']
  s.summary = package['description']
  s.license = package['license']
  s.homepage = package['repository']['url']
  s.authors = package['authors']
  s.source = { :git => 'https://github.com/SimpliField/capacitor-media', :tag => s.version.to_s }
  s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
  s.ios.deployment_target  = '12.0'
  s.dependency 'Capacitor'
  s.swift_version = '5.1'
end
