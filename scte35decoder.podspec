Pod::Spec.new do |spec|
    spec.name                     = 'scte35decoder'
    spec.version                  = '0.0.1'
    spec.homepage                 = 'https://github.com/realeyes-media/scte35-decoder-multiplatform'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'CocoaPods stce35-decoder library'
    spec.vendored_frameworks      = 'build/cocoapods/framework/scte35decoder.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '15'
                
                
    if !Dir.exist?('build/cocoapods/framework/scte35decoder.framework') || Dir.empty?('build/cocoapods/framework/scte35decoder.framework')
        raise "

        Kotlin framework 'scte35decoder' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => '',
        'PRODUCT_MODULE_NAME' => 'scte35decoder',
    }
                
    spec.script_phases = [
        {
            :name => 'Build scte35decoder',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end