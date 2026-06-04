<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://github.com/user-attachments/assets/5b7ff722-d506-4165-a081-85325be30151">
  <source media="(prefers-color-scheme: light)" srcset="https://github.com/user-attachments/assets/00c3eb05-557a-441f-9db5-916e70ac3dcb">
  <img alt="Spectrum analyzer" src="https://github.com/user-attachments/assets/00c3eb05-557a-441f-9db5-916e70ac3dcb">
</picture>

I have a self-made spectrum analyzer in my room. It's a lovely device, but it only works with a physical AUX cable plugged in, which made it useless whenever I just listened to music on my Arch setup. So I built JAnalyzer to cut the cable.

JAnalyzer captures system audio, runs it through an FFT, and broadcasts the frequency data over UDP so the hardware can visualize it wirelessly. It runs quietly in the background, or you can wire it up to start with Spotify.

Built in Java because I really like Java, and the sound API and multi-threading sounded cool to use, even though a programming language with a garbage collector is not ideal for low-latency streaming.

## How it works

Three threads, two queues:

**Capture → Process → Transmit**

- `AudioCapture` taps into the system audio line via the Java Sound API and pushes raw PCM chunks into a queue
- `AudioProcessor` picks them up, applies a **Hann window** to taper the edges (avoiding spectral leakage that would smear the FFT result), runs a **1024-point FFT** via JTransforms, maps the output into **7 logarithmically-spaced frequency buckets**, and normalizes everything against a rolling history of 1400 frames so the output stays stable and responsive
- `UdpTransmitter` packs the normalized buckets into 7 bytes and broadcasts them over UDP to the local subnet

## Running it

```bash
mvn compile exec:java -Dexec.mainClass=nl.niels.Main
```

**Getting audio in:** JAnalyzer listens to your default PipeWire output device. The trick that makes this work wirelessly is setting your Bluetooth speaker as the default output, Java picks it up as the capture source, so whatever plays through your speaker also feeds the analyzer.

Some values (broadcast address, port, bucket count) are hardcoded to match my hardware.
