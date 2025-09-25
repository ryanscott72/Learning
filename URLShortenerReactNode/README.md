# URL Shortener Full-Stack Application

A complete URL shortener application built with React frontend and Node.js backend, similar to TinyURL.

## Features

- ✅ Shorten long URLs into compact, shareable links
- ✅ Automatic URL validation
- ✅ Click tracking and analytics
- ✅ Duplicate URL detection (reuses existing short codes)
- ✅ Copy to clipboard functionality
- ✅ Responsive design with modern UI
- ✅ SQLite database for persistence
- ✅ RESTful API endpoints

## Tech Stack

**Frontend:**
- React with Hooks
- Tailwind CSS for styling
- Lucide React for icons

**Backend:**
- Node.js with Express
- SQLite database
- CORS enabled

## Quick Start

### Backend Setup

1. Create a new directory for the backend:
```bash
mkdir url-shortener-backend
cd url-shortener-backend
```

2. Initialize npm and install dependencies:
```bash
npm init -y
npm install express cors sqlite3
npm install -D nodemon
```

3. Copy the `server.js` code from the backend artifact into your project

4. Update your `package.json` with the provided scripts

5. Start the backend server:
```bash
npm run dev
# or
npm start
```

The backend will run on `http://localhost:3001`

### Frontend Setup

1. Create a new React app:
```bash
npx create-react-app url-shortener-frontend
cd url-shortener-frontend
```

2. Install additional dependencies:
```bash
npm install lucide-react
```

3. Replace the contents of `src/App.js` with the React component code from the frontend artifact

4. Start the React development server:
```bash
npm start
```

The frontend will run on `http://localhost:3000`

## API Endpoints

### POST `/api/shorten`
Shorten a URL
- Body: `{ "url": "https://example.com/long-url" }`
- Response: `{ "shortUrl": "http://localhost:3001/abc123", "shortCode": "abc123" }`

### GET `/:shortCode`
Redirect to original URL
- Redirects to the original URL
- Increments click counter

### GET `/api/stats/:shortCode`
Get statistics for a shortened URL
- Response: `{ "originalUrl": "...", "clickCount": 5, "createdAt": "..." }`

### GET `/api/urls`
Get all URLs (admin/debug endpoint)
- Returns all stored URLs with stats

### GET `/api/health`
Health check endpoint
- Response: `{ "status": "OK", "message": "URL Shortener API is running" }`

## Database Schema

The SQLite database (`urls.db`) contains a single table:

```sql
CREATE TABLE urls (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  original_url TEXT NOT NULL,
  short_code TEXT UNIQUE NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  click_count INTEGER DEFAULT 0
);
```

## Usage

1. Enter a long URL in the input field
2. Click "Shorten URL" to generate a short link
3. Copy the shortened URL and share it
4. Click the stats icon to view click analytics
5. Use the shortened URL to redirect to the original URL

## Features Explained

- **URL Validation**: Ensures entered URLs are properly formatted
- **Duplicate Detection**: If you shorten the same URL twice, it returns the existing short code
- **Click Tracking**: Every time someone visits a shortened URL, the click count increases
- **Statistics**: View how many times your shortened URL has been clicked
- **Copy to Clipboard**: Easy one-click copying of shortened URLs
- **Responsive Design**: Works on desktop and mobile devices

## Production Deployment

For production deployment, consider:

1. **Environment Variables**: Use environment variables for configuration
2. **Database**: Migrate to PostgreSQL or MySQL for better performance
3. **Domain**: Use your own domain instead of localhost
4. **HTTPS**: Ensure all connections use HTTPS
5. **Rate Limiting**: Add rate limiting to prevent abuse
6. **Authentication**: Add user accounts and URL management
7. **Custom Short Codes**: Allow users to create custom short codes
8. **Analytics**: Add more detailed analytics (referrers, geographic data, etc.)

## Contributing

Feel free to submit issues and enhancement requests!

## License

MIT License