const TOKEN = '6A5AA1D4EAFF4E9FB37E23D68491D6F4';
const ENDPOINT = `wss://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1?TrustedClientToken=${TOKEN}`;

function escXml(s) {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

async function synthesize(text) {
  const reqId = crypto.randomUUID().replace(/-/g, '');
  const resp = await fetch(ENDPOINT.replace('wss://', 'https://'), {
    headers: { Upgrade: 'websocket' },
  });
  const ws = resp.webSocket;
  if (!ws) throw new Error('WebSocket upgrade failed');
  ws.accept();

  return new Promise((resolve, reject) => {
    const chunks = [];
    ws.addEventListener('message', (ev) => {
      if (typeof ev.data === 'string') {
        if (ev.data.includes('Path:turn.end')) {
          ws.close();
          const total = chunks.reduce((a, b) => a + b.byteLength, 0);
          const out = new Uint8Array(total);
          let off = 0;
          for (const c of chunks) { out.set(c, off); off += c.byteLength; }
          resolve(out);
        }
      } else if (ev.data instanceof ArrayBuffer) {
        const view = new DataView(ev.data);
        const hdrLen = view.getUint16(0);
        if (ev.data.byteLength > hdrLen + 2) {
          chunks.push(new Uint8Array(ev.data, hdrLen + 2));
        }
      }
    });
    ws.addEventListener('error', (e) => reject(e));

    const ts = new Date().toISOString();
    ws.send(
      `X-Timestamp:${ts}\r\nContent-Type:application/json; charset=utf-8\r\nPath:speech.config\r\n\r\n` +
      `{"context":{"synthesis":{"audio":{"metadataoptions":{"sentenceBoundaryEnabled":"false","wordBoundaryEnabled":"false"},"outputFormat":"audio-24khz-48kbitrate-mono-mp3"}}}}`
    );
    const ssml = `<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='ne-NP'><voice name='ne-NP-HemkalaNeural'><prosody rate='-8%' pitch='-2Hz'>${escXml(text)}</prosody></voice></speak>`;
    ws.send(
      `X-RequestId:${reqId}\r\nContent-Type:application/ssml+xml\r\nX-Timestamp:${ts}\r\nPath:ssml\r\n\r\n${ssml}`
    );
  });
}

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    const text = url.searchParams.get('text');
    if (!text) return new Response('Missing text', { status: 400 });

    const cache = caches.default;
    const cached = await cache.match(request);
    if (cached) return cached;

    try {
      const mp3 = await synthesize(text.slice(0, 1000));
      const res = new Response(mp3, {
        headers: {
          'Content-Type': 'audio/mpeg',
          'Cache-Control': 'public, max-age=86400',
          'Access-Control-Allow-Origin': '*',
        },
      });
      ctx.waitUntil(cache.put(request, res.clone()));
      return res;
    } catch (e) {
      return new Response('TTS error', { status: 502 });
    }
  },
};
