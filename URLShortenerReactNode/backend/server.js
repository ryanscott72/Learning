require('dotenv').config();
const express = require('express');
const cors = require('cors');
const sqlite3 = require('sqlite3').verbose();
const crypto = require('crypto');
const path = require('path');
const app = express();

const PORT = process.env.PORT;
const REDIRECT_PORT = process.env.REDIRECT_PORT;
const URL_FOR_SHORTENER = process.env.URL_FOR_SHORTENER;
console.debug("PORT: ", process.env.PORT);
console.debug("REDIRECT_PORT: ", process.env.REDIRECT_PORT);
console.debug("URL_FOR_SHORTENER: ", process.env.URL_FOR_SHORTENER);

// Middleware
app.use(cors());
app.use(express.json());

// Initialize SQLite database
const dbPath = process.env.DB_PATH;
const db = new sqlite3.Database(dbPath);

// Create table if it doesn't exist
db.serialize(() => {
  db.run(`CREATE TABLE IF NOT EXISTS urls (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    original_url TEXT NOT NULL,
    short_code TEXT UNIQUE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    click_count INTEGER DEFAULT 0
  )`);
});

// Generate short code
const generateShortCode = () => {
  return crypto.randomBytes(4).toString('hex');
};

// Validate URL format
const isValidUrl = (string) => {
  try {
    new URL(string);
    return true;
  } catch (_) {
    return false;
  }
};

// Routes

// Create short URL
app.post('/api/shorten', (req, res) => {
  const { url } = req.body;

  if (!url) {
    return res.status(400).json({ error: 'URL is required' });
  }

  if (!isValidUrl(url)) {
    return res.status(400).json({ error: 'Invalid URL format' });
  }

  // Check if URL already exists
  db.get('SELECT short_code FROM urls WHERE original_url = ?', [url], (err, row) => {
    if (err) {
      console.error(err);
      return res.status(500).json({ error: 'Database error' });
    }

    if (row) {
      console.debug("URL " + url + " already exists, returning existing short code");
      // URL already exists, return existing short code
      return res.json({
        shortUrl: `http://${URL_FOR_SHORTENER}:${REDIRECT_PORT}/${row.short_code}`,
        shortCode: row.short_code
      });
    }

    console.debug("URL received: ", url);
    // Generate new short code
    const shortCode = generateShortCode();

    // Insert new URL
    db.run(
      'INSERT INTO urls (original_url, short_code) VALUES (?, ?)',
      [url, shortCode],
      function(err) {
        if (err) {
          return res.status(500).json({ error: 'Failed to create short URL' });
        }

        console.debug("Returning shortened URL: ", `http://${URL_FOR_SHORTENER}:${REDIRECT_PORT}/${shortCode}`);
        res.json({
          shortUrl: `http://${URL_FOR_SHORTENER}:${REDIRECT_PORT}/${shortCode}`,
          shortCode: shortCode
        });
      }
    );
  });
});

// Redirect to original URL
app.get('/:shortCode', (req, res) => {
  const { shortCode } = req.params;
  console.debug("Redirect shortcode " + shortCode + " to longcode");

  db.get(
    'SELECT original_url FROM urls WHERE short_code = ?',
    [shortCode],
    (err, row) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }

      if (!row) {
        return res.status(404).json({ error: 'Short URL not found' });
      }

      // Increment click count
      db.run('UPDATE urls SET click_count = click_count + 1 WHERE short_code = ?', [shortCode]);

      console.debug("Redirecting to URL: ", row.original_url);
      // Redirect to original URL
      res.redirect(row.original_url);
    }
  );
});

// Get URL stats
app.get('/api/stats/:shortCode', (req, res) => {
  const { shortCode } = req.params;
  console.debug("Getting stats for shortcode: ", shortCode);

  db.get(
    'SELECT original_url, click_count, created_at FROM urls WHERE short_code = ?',
    [shortCode],
    (err, row) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }

      if (!row) {
        return res.status(404).json({ error: 'Short URL not found' });
      }

      res.json({
        originalUrl: row.original_url,
        clickCount: row.click_count,
        createdAt: row.created_at
      });
    }
  );
});

// Get all URLs (for admin/debugging)
app.get('/api/urls', (req, res) => {
  console.debug("Getting all URLs");
  db.all(
    'SELECT short_code, original_url, click_count, created_at FROM urls ORDER BY created_at DESC',
    [],
    (err, rows) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }

      res.json(rows);
    }
  );
});

// Health check
app.get('/api/health', (req, res) => {
  console.debug('Health Check');
  res.json({ status: 'OK', message: 'URL Shortener API is running' });
});

// Start server
app.listen(PORT, () => {
  console.log(`Server running on http://0.0.0.0:${PORT}`);
  console.log(`Database: ${dbPath}`);
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\nShutting down server...');
  db.close((err) => {
    if (err) {
      console.error('Error closing database:', err);
    } else {
      console.log('Database connection closed.');
    }
    process.exit(0);
  });
});