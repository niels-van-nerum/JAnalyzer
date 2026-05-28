# Spectrum Analyzer

Reads audio playing on your computer (e.g. Spotify), figures out which frequencies are loud at any given moment, and sends those results over the network.

## What it does

Your music is made up of many sine waves at different frequencies playing at the same time. This app listens to that audio 50 times per second, figures out how loud each frequency is, and broadcasts the results over UDP — so something else (like a visualizer or LED strip) can react to it in real time.

## How it works

1. **AudioCapture** — listens to your system audio and slices it into 20ms chunks of raw bytes.
2. **AudioProcessor** — converts those bytes into audio samples, runs an FFT to find out which frequencies are present and how loud they are, and produces a list of magnitudes — one number per frequency bucket.
3. **UdpTransmitter** *(not yet built)* — takes those magnitudes and broadcasts them over UDP.

Each step runs on its own thread. They hand work to each other through a queue.

## Design decisions

**Three threads, two queues** — Capture, processing, and transmitting run independently. If one is slow, it doesn't stall the others.

**Bounded queue with drop-on-full** — If the processor can't keep up, old chunks are dropped rather than queued up forever. For live audio this is the right call: a chunk that is a few seconds old is useless.

**Arrays.copyOf before queuing** — The audio buffer gets reused on every read. Copying into a fresh array means the processor always sees stable data.

**20ms chunks** — Small enough to feel real-time, large enough to give the FFT useful frequency resolution.

**FFT size of 1024** — Each chunk produces 960 mono samples. The FFT works fastest on powers of 2, so samples are zero-padded to 1024 before processing. Zeros mean silence — no fake frequencies are introduced.
