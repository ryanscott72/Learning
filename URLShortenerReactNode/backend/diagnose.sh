#!/bin/bash

echo "=========================================="
echo "URL Shortener Backend Diagnostics"
echo "=========================================="
echo ""

# Get Minikube IP
echo "1. Minikube IP:"
MINIKUBE_IP=$(minikube ip)
echo "   $MINIKUBE_IP"
echo ""

# Check if backend is deployed
echo "2. Backend Pod Status:"
kubectl get pods -l app=url-shortener-backend
echo ""

# Get pod name
POD_NAME=$(kubectl get pods -l app=url-shortener-backend -o jsonpath='{.items[0].metadata.name}')

if [ -z "$POD_NAME" ]; then
    echo "ERROR: No backend pod found!"
    exit 1
fi

echo "3. Pod Name: $POD_NAME"
echo ""

# Check pod details
echo "4. Pod Details:"
kubectl describe pod $POD_NAME | grep -A 5 "Conditions:"
echo ""

# Check events
echo "5. Recent Events:"
kubectl get events --field-selector involvedObject.name=$POD_NAME --sort-by='.lastTimestamp' | tail -10
echo ""

# Check logs
echo "6. Last 20 lines of logs:"
kubectl logs $POD_NAME --tail=20
echo ""

# Check if service is accessible from within the cluster
echo "7. Testing health endpoint from inside pod:"
kubectl exec $POD_NAME -- wget -q -O- http://localhost:3001/api/health 2>&1
echo ""

# Check if service is accessible via service
echo "8. Testing via Service (NodePort):"
echo "   URL: http://$MINIKUBE_IP:30001/api/health"
curl -s http://$MINIKUBE_IP:30001/api/health || echo "   FAILED: Cannot reach service"
echo ""

# Check environment variables
echo "9. Environment Variables:"
kubectl exec $POD_NAME -- env | grep -E "PORT|DB_PATH|NODE_ENV"
echo ""

# Check volume mount
echo "10. Volume Mount Status:"
kubectl exec $POD_NAME -- ls -la /app/data/
echo ""

# Check if server is listening
echo "11. Checking if server is listening on port 3001:"
kubectl exec $POD_NAME -- netstat -tlnp 2>/dev/null | grep 3001 || echo "   netstat not available, trying alternative..."
kubectl exec $POD_NAME -- sh -c "wget -q -O- http://0.0.0.0:3001/api/health" 2>&1
echo ""

# Summary
echo "=========================================="
echo "Summary:"
echo "=========================================="
kubectl get pods -l app=url-shortener-backend -o wide
echo ""
kubectl get svc url-shortener-backend-service
echo ""

echo "If health checks are failing, check the logs above for:"
echo "  - 'Server running on http://0.0.0.0:3001' (server started)"
echo "  - 'Connected to database' (database initialized)"
echo "  - Any error messages"
echo ""
echo "To follow logs in real-time:"
echo "  kubectl logs -f $POD_NAME"
echo ""
echo "To test the health endpoint:"
echo "  curl http://$MINIKUBE_IP:30001/api/health"