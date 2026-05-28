# Spectrum Analyzer

Captures system audio, runs an FFT on it, and sends the frequency magnitudes over UDP. The idea is that something else — a visualizer, LED strip, whatever — picks up those UDP packets and does something with them.

## Pipeline

Audio capture → FFT processing → UDP transmit. Each step runs on its own thread and hands chunks to the next via a LinkedBlockingQueue.

The queue is bounded and uses `offer()` to drop chunks when full. Old audio is useless, so dropping is the right call.

`Arrays.copyOf` before enqueuing — the read buffer gets reused, so you need a fresh copy or the processor sees garbage.

## FFT

Chunks are 20ms of 48kHz stereo 16-bit audio (3840 bytes). After converting to mono doubles that's 960 samples. Zero-padded to 1024 (next power of 2) before the FFT. Zeros are silence, they don't add fake frequencies.

The FFT gives back a complex number per frequency bin — real and imaginary. We only care about the amplitude, so we take `sqrt(re² + im²)` for each bin and group the results into buckets.
