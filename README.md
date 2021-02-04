# SCTE-35

SCTE-35 is the standard for time-based event messaging in video streams. It's most commonly used for dynamically signaling when to insert an ad break, but it could also contain information about program boundaries, blackouts, or stream switching.

## Multiplatform SCTE-35 Decoder

This is the RealEyes Kotlin Multiplatform project for SCTE-35 parsing.

### Project Setup

**NOTE** Android Studio or IntelliJ should do this step automatically.

Create a `local.properties` file with a single property `sdk.dir={Android_SDK_LOCATION}`.
Replace the placeholder with your Android SDK location, ex: `/User/{USER_NAME}/Library/Android/sdk`

### Related Projects

[Android Specific](https://github.com/realeyes-media/scte35-kotlin)

[iOS Specific](https://github.com/realeyes-media/scte35-swift)

[Javascript Specific](https://github.com/realeyes-media/scte35-js)

### How to Include on Your Platform


**Javascript**

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

### License

This project is licensed under the MIT License. See LICENSE file for details.