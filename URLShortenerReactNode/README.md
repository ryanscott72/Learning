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

## Deploying to Minikube

### Step 1: Start Minikube
```bash
# Start Minikube with sufficient resources
minikube start --cpus=2 --memory=4096

# Enable required addons
minikube addons enable ingress
minikube addons enable metrics-server

# Configure Docker to use Minikube's Docker daemon
eval $(minikube docker-env)
```

### Step 2: Build Docker Images
#### Backend Image
```bash
# From backend directory

# Create Dockerfile (use the provided Dockerfile artifact)
# Make sure server.js and package.json are in this directory

# Build the image
docker build -t url-shortener-backend:dev -f Dockerfile.dev .    

# Verify the image
docker images | grep url-shortener-backend
```

#### Frontend Image
```bash
# From frontend directory

# Create Dockerfile and nginx.conf (use the provided artifacts)
# Make sure your React app is in this directory

# Update the API URL in your React component
# Change API_BASE to use environment variable:
# const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:3001';

# Build the image
docker build -t url-shortener-frontend:dev -f Dockerfile.dev .

# Verify the image
docker images | grep url-shortener-frontend
```

### Step 3: Deploy to Kubernetes
#### Deploy Backend
```bash
# Apply backend configuration
kubectl apply -f backend-deployment-dev.yaml

# Check deployment status
kubectl get pods -l app=url-shortener-backend
kubectl get svc url-shortener-backend-service

# Check logs
kubectl logs -l app=url-shortener-backend
```

#### Deploy Frontend
```bash
# Get Minikube IP for configuring the frontend
minikube ip  # Usually 192.168.49.2

# Update frontend-deployment.yaml with correct Minikube IP
# Change REACT_APP_API_URL to: http://<MINIKUBE_IP>:30001

# Apply frontend configuration
kubectl apply -f frontend-deployment-dev.yaml

# Check deployment status
kubectl get pods -l app=url-shortener-frontend
kubectl get svc url-shortener-frontend-service
```

### Step 4: Access the Application
```bash
# Get the Minikube IP
minikube ip

# Access the application
# Frontend: http://<MINIKUBE_IP>:30000
# Backend API: http://<MINIKUBE_IP>:30001

# Or use Minikube service command
minikube service url-shortener-frontend-service --url
minikube service url-shortener-backend-service --url
```

### Step 5: Verify Deployment
```bash
# Check all resources
kubectl get all

# Check pods status
kubectl get pods

# Check services
kubectl get svc

# Check persistent volume claim
kubectl get pvc

# View pod logs
kubectl logs -l app=url-shortener-backend
kubectl logs -l app=url-shortener-frontend

# Describe a pod for troubleshooting
kubectl describe pod <pod-name>
```

## Accessing the Application
Open your browser and navigate to:

Frontend: http://<MINIKUBE_IP>:30000
Backend API: http://<MINIKUBE_IP>:30001/api/health

Replace <MINIKUBE_IP> with the IP address from minikube ip command.

## Useful Commands
### Scaling
```bash
# Scale frontend replicas
kubectl scale deployment url-shortener-frontend --replicas=3

# Scale backend replicas (be careful with SQLite)
kubectl scale deployment url-shortener-backend --replicas=1
```

### Logs
```bash
# Follow logs
kubectl logs -f -l app=url-shortener-backend
kubectl logs -f -l app=url-shortener-frontend

# View logs from specific pod
kubectl logs <pod-name>
```

### Port Forwarding (Alternative Access)
```bash
# Forward backend port
kubectl port-forward svc/url-shortener-backend-service 3001:3001

# Forward frontend port
kubectl port-forward svc/url-shortener-frontend-service 8080:80
```

### Debugging
```bash
# Execute commands in a pod
kubectl exec -it <pod-name> -- /bin/sh

# Check environment variables
kubectl exec -it <pod-name> -- env

# Check mounted volumes
kubectl exec -it <pod-name> -- ls -la /app/data
```

## Updating the Application
### Update Backend
```bash
# Rebuild image
docker build -t url-shortener-backend:dev -f Dockerfile.dev .

# Restart deployment
kubectl rollout restart deployment url-shortener-backend

# Check rollout status
kubectl rollout status deployment url-shortener-backend
```
### Update Frontend
```bash
# Rebuild image
docker build -t url-shortener-frontend:dev -f Dockerfile.dev .

# Restart deployment
kubectl rollout restart deployment url-shortener-frontend

# Check rollout status
kubectl rollout status deployment url-shortener-frontend
```
### Cleanup
```bash
# Delete all resources
cd backend
kubectl delete -f backend-deployment-dev.yaml
cd ../frontend
kubectl delete -f frontend-deployment-dev.yaml

# Or delete by label
kubectl delete all -l app=url-shortener-backend
kubectl delete all -l app=url-shortener-frontend

# Delete PVC
kubectl delete pvc url-shortener-db-pvc

# Stop Minikube
minikube stop

# Delete Minikube cluster
minikube delete
```

## Production Considerations
For production deployment, consider:
1. Database: Replace SQLite with PostgreSQL or MySQL using a StatefulSet
2. Ingress: Set up an Ingress controller for proper routing
3. TLS/SSL: Add certificates for HTTPS
4. Resource Limits: Adjust CPU and memory limits based on load
5. Horizontal Pod Autoscaling: Configure HPA for automatic scaling
6. Monitoring: Add Prometheus and Grafana for monitoring
7. Logging: Implement centralized logging with ELK stack
8. ConfigMaps/Secrets: Use secrets for sensitive data
9. Health Checks: Fine-tune liveness and readiness probes
10. Backup: Implement database backup strategies

## Troubleshooting
### Pods not starting
```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

### Image pull errors
Ensure you're using Minikube's Docker daemon:
```bash
eval $(minikube docker-env)
docker images  # Should show your images
```

### Service not accessible
```bash
# Check service endpoints
kubectl get endpoints

# Check if pods are running
kubectl get pods

# Verify NodePort
kubectl get svc
```

### Database Issues
```bash
# Check PVC status
kubectl get pvc

# Check volume mounts
kubectl describe pod <backend-pod-name>

# Access pod and check database
kubectl exec -it <backend-pod-name> -- ls -la /app/data
```

## Contributing

Feel free to submit issues and enhancement requests!

## License

MIT License