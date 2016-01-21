# ZAP Utils

This module is responsible for the automatic initialization of ZAP (via local installation or Docker).

To start ZAP, simply create a `ZapInfo` instance through its builder and call `Zap.startZap(zapInfo)`:

```java
// This will make ZAP start from a local installation
ZapInfo zapInfo = ZapInfo.builder().buildToRunZap(8080, "C:\\ZAP");

// This would make ZAP start via Docker; there are many other options that can be set using ZapInfo's builder
// ZapInfo zapInfo = ZapInfo.builder().buildToRunZapWithDocker(8080);

// This starts ZAP
Zap.startZap(zapInfo);

// And this kills it
Zap.stopZap();

```

:zap:
