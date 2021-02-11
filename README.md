# SCTE-35

SCTE-35 is the standard for time-based event messaging in video streams. It's most commonly used for dynamically signaling when to insert an ad break, but it could also contain information about program boundaries, blackouts, or stream switching.

## Multiplatform SCTE-35 Decoder

This is the RealEyes Kotlin Multiplatform project for SCTE-35 parsing.

### Project Setup

**NOTE** Android Studio or IntelliJ should do this step automatically.

Create a `local.properties` file with a single property `sdk.dir={Android_SDK_LOCATION}`.
Replace the placeholder with your Android SDK location, ex: `/User/{USER_NAME}/Library/Android/sdk`

### Reference Projects

[Android Specific](https://github.com/realeyes-media/scte35-android)

[iOS Specific](https://github.com/realeyes-media/scte35-swift)

[iOS Test Harness](https://github.com/realeyes-media/scte35-decoder-multiplatform-iOS-harness)

[Javascript Specific](https://github.com/realeyes-media/scte35-js)

### How to Include on Your Platform


**Javascript**


After running the build command, the js package can be found here: `build/js/packages/scte35decoder`
```TypeScript
const { Scte35DecoderFactory } = require('./packages/scte35decoder').scte35decoder;

const B64_STRING = '/DA1AAAAAAAAAP/wBQb/SMG+pgAfAh1DVUVJAAAAAX+/AQ5FUDAzMjU2ODEyMDAyNwEBATMCzNc=';
const HEX_STRING = '0xFC303500000000000000FFF00506FF48C1BEA6001F021D43554549000000017FBF010E45503033323536383132303032370101013302CCD7';

/**
 * Parse Scte Message From Base64 String
 */
const scteMessageFromBase64String = (base64String) => {
    const b64Decoder = Scte35DecoderFactory.createB64Decoder();
    const scteDecoder = Scte35DecoderFactory.createScteDecoder();
    return scteDecoder.decodeFromB64(base64String, b64Decoder);
}

/**
 * Parse Scte Message From Hex String
 */
const scteMessageFromHexString = (hexString) => {
    const scteDecoder = Scte35DecoderFactory.createScteDecoder();
    return scteDecoder.decodeFromHex(hexString);
}

console.log('from Base64 String: ', scteMessageFromBase64String(B64_STRING));
console.log('from Hex String: ', scteMessageFromHexString(HEX_STRING));
```

**Android**

**iOS**

After running the build command, the js package can be found here: `build/cocoapods/framework/scte35decoder.framework
_When installing the framework, be sure to chose "copy if needed" with adding the framework to the project._

```Swift
import scte35decoder

let b64String = "/DA1AAAAAAAAAP/wBQb/SMG+pgAfAh1DVUVJAAAAAX+/AQ5FUDAzMjU2ODEyMDAyNwEBATMCzNc="
let hexString = "0xFC303500000000000000FFF00506FF48C1BEA6001F021D43554549000000017FBF010E45503033323536383132303032370101013302CCD7"

class SCTEDecoder {
    
    let b64Decoder = Scte35DecoderFactory().createB64Decoder()
    let scteDecoder = Scte35DecoderFactory().createScteDecoder()
    
    /**
    * Parse Scte Message From Base64 String
    */
    
    func decodeFromB64(b64String: String) -> SpliceInfoSection {
        return scteDecoder.decodeFromB64(b64String: b64String, b64Decoder: b64Decoder)
    }
    
    /**
    * Parse Scte Message From Hex String
    */
    
    func decodeFromHex(hexString: String) -> SpliceInfoSection {
        return scteDecoder.decodeFromHex(hexString: hexString)
    }
}
```

-------

## Usage

- In build.gradle under dependencies add the following
```
 implementation 'com.realeyes:scte35decoder:0.100.2'
 
```
- Add environmental variables for your Github token and username credentials
### Step 1 : Generate a Personal Access Token for GitHub
- Inside you GitHub account:
	- Settings -> Developer Settings -> Personal Access Tokens -> Generate new token
	- Make sure you select the following scopes (" write:packages", " read:packages") and Generate a token
	- After Generating make sure to copy your new personal access token. You won’t be able to see it again!

### Step 2: Add your GitHub - Personal Access Token details to environment variables
	
- You add the **GPR_USER** and **GPR_API_KEY** values to your environment variables on you local machine or build server to avoid creating a github properties file

### Step 3: Configure Github pacakges

```
repositories {
        maven {
            name = "GitHubPackages"

            url = uri("https://maven.pkg.github.com/UserID/REPOSITORY")

            credentials {
                username = githubProperties['gpr.usr'] ?: System.getenv("GPR_USER")
                password = githubProperties['gpr.key'] ?: System.getenv("GPR_API_KEY")
            }
        }
    }
 ```

 - Then under the test package you can find ExampleUnitTest which uses the library methods and it will run successfully

Parse From Base64 String

```
   @Test
    fun correctly_parses_ContentIdentification_scte_message_from_base64_string() {
        val b64Decoder = Scte35DecoderFactory.createB64Decoder()
        val scteDecoder = Scte35DecoderFactory.createScteDecoder()
        val info = scteDecoder.decodeFromB64("/DA1AAAAAAAAAP/wBQb/SMG+pgAfAh1DVUVJAAAAAX+/AQ5FUDAzMjU2ODEyMDAyNwEBATMCzNc=", b64Decoder)

        assertEquals(0xFC, info.tableId)
    }
```

Parse from Hex String

```
  @Test
    fun correctly_parses_ContentIdentification_scte_message_from_hex_string() {
        val scteDecoder = Scte35DecoderFactory.createScteDecoder()
        val info = scteDecoder.decodeFromHex("0xFC303500000000000000FFF00506FF48C1BEA6001F021D43554549000000017FBF010E45503033323536383132303032370101013302CCD7")

        assertEquals(0xFC, info.tableId)
    }
```
### License

This project is licensed under the MIT License. See LICENSE file for details.
