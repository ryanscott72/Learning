import { useState } from "react";
import {
  Copy,
  ExternalLink,
  BarChart3,
  Globe,
  Calendar,
  MousePointer,
} from "lucide-react";
import "./App.css";

type URLStats = {
  originalUrl: string;
  clickCount: number;
  createdAt: number;
};

function App() {
  const [url, setUrl] = useState<string>("");
  const [shortUrl, setShortUrl] = useState<string>("");
  const [shortCode, setShortCode] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  const [copied, setCopied] = useState<boolean>(false);
  const [stats, setStats] = useState<URLStats | null>();
  const [showStats, setShowStats] = useState<boolean>(false);

  const API_BASE = import.meta.env.VITE_API_URL;

  const shortenUrl = async () => {
    if (!url.trim()) {
      setError("Please enter a URL");
      return;
    }

    setLoading(true);
    setError("");
    setShortUrl("");
    setStats(null);
    setShowStats(false);

    try {
      const response = await fetch(`${API_BASE}/api/shorten`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ url: url.trim() }),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || "Failed to shorten URL");
      }

      setShortUrl(data.shortUrl);
      setShortCode(data.shortCode);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        console.warn("Unhandled error type");
        console.error(err);
      }
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = async () => {
    try {
      await navigator.clipboard.writeText(shortUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error("Failed to copy:", err);
    }
  };

  const getStats = async () => {
    if (!shortCode) return;

    try {
      const response = await fetch(`${API_BASE}/api/stats/${shortCode}`);
      const data = await response.json();

      if (response.ok) {
        setStats(data);
        setShowStats(true);
      }
    } catch (err) {
      console.error("Failed to get stats:", err);
    }
  };

  const reset = () => {
    setUrl("");
    setShortUrl("");
    setShortCode("");
    setError("");
    setStats(null);
    setShowStats(false);
    setCopied(false);
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      shortenUrl();
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <div className="max-w-2xl mx-auto pt-12">
        <div className="text-center mb-12">
          <div className="flex justify-center items-center mb-4">
            <Globe className="w-12 h-12 text-indigo-600 mr-3" />
            <h1 className="text-4xl font-bold text-gray-900">URL Shortener</h1>
          </div>
          <p className="text-gray-600 text-lg">
            Transform long URLs into short, shareable links
          </p>
        </div>

        <div className="bg-white rounded-2xl shadow-xl p-8 mb-8">
          <div className="space-y-6">
            <div>
              <label
                htmlFor="url"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                Enter your long URL
              </label>
              <input
                id="url"
                type="url"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                onKeyUp={handleKeyPress}
                placeholder="https://example.com/very-long-url-here"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors text-black"
                disabled={loading}
              />
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
                {error}
              </div>
            )}

            <div className="flex gap-3">
              <button
                type="button"
                onClick={shortenUrl}
                disabled={loading}
                className="flex-1 bg-indigo-600 text-white px-6 py-3 rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
              >
                {loading ? "Shortening..." : "Shorten URL"}
              </button>

              {shortUrl && (
                <button
                  type="button"
                  onClick={reset}
                  className="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Reset
                </button>
              )}
            </div>
          </div>

          {shortUrl && (
            <div className="mt-8 p-6 bg-green-50 border border-green-200 rounded-lg">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-green-800">
                  Your shortened URL:
                </h3>
                <div className="flex gap-2">
                  <button
                    onClick={getStats}
                    className="p-2 text-green-600 hover:text-green-800 hover:bg-green-100 rounded-lg transition-colors"
                    title="View statistics"
                  >
                    <BarChart3 className="w-5 h-5" />
                  </button>
                  <a
                    href={shortUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="p-2 text-green-600 hover:text-green-800 hover:bg-green-100 rounded-lg transition-colors"
                    title="Open in new tab"
                  >
                    <ExternalLink className="w-5 h-5" />
                  </a>
                </div>
              </div>

              <div className="flex items-center gap-3 bg-white p-3 rounded border">
                <code className="flex-1 text-green-700 font-mono">
                  {shortUrl}
                </code>
                <button
                  onClick={copyToClipboard}
                  className={`p-2 rounded transition-colors ${
                    copied
                      ? "text-green-600 bg-green-100"
                      : "text-gray-500 hover:text-green-600 hover:bg-green-100"
                  }`}
                  title={copied ? "Copied!" : "Copy to clipboard"}
                >
                  <Copy className="w-5 h-5" />
                </button>
              </div>

              {copied && (
                <p className="text-green-600 text-sm mt-2 font-medium">
                  ✓ Copied to clipboard!
                </p>
              )}
            </div>
          )}

          {showStats && stats && (
            <div className="mt-6 p-6 bg-blue-50 border border-blue-200 rounded-lg">
              <h3 className="text-lg font-semibold text-blue-800 mb-4">
                Statistics
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="flex items-center gap-3 bg-white p-3 rounded-lg">
                  <MousePointer className="w-5 h-5 text-blue-600" />
                  <div>
                    <p className="text-sm text-gray-600">Clicks</p>
                    <p className="font-semibold">{stats.clickCount}</p>
                  </div>
                </div>

                <div className="flex items-center gap-3 bg-white p-3 rounded-lg">
                  <Calendar className="w-5 h-5 text-blue-600" />
                  <div>
                    <p className="text-sm text-gray-600">Created</p>
                    <p className="font-semibold">
                      {new Date(stats.createdAt).toLocaleDateString()}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-3 bg-white p-3 rounded-lg">
                  <Globe className="w-5 h-5 text-blue-600" />
                  <div>
                    <p className="text-sm text-gray-600">Original</p>
                    <p
                      className="font-semibold text-xs truncate"
                      title={stats.originalUrl}
                    >
                      {stats.originalUrl}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        <div className="bg-white rounded-xl shadow-lg p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            How it works
          </h2>
          <div className="space-y-3 text-gray-600">
            <p>1. Paste your long URL in the input field above</p>
            <p>2. Click "Shorten URL" to generate a compact link</p>
            <p>3. Copy and share your shortened URL anywhere</p>
            <p>4. Click the stats icon to view click analytics</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
