# OSRM Docker Setup & Integration Guide

## 1. OSRM Docker Setup

### Install Docker Desktop
- Download from: https://www.docker.com/products/docker-desktop
- Install and start Docker Desktop

### Pull OSRM Car Image
```bash
docker pull jostmey/osrm-car:latest
```

### Run OSRM Container for Ekaterinburg
```bash
# Create data directory
mkdir -p /opt/osrm-data

# Download .pbf file for Ekaterinburg region
# You can use geofabrik or download from http://download.geofabrik.de/

# Start container with Ekaterinburg map
docker run -t -i -v /opt/osrm-data:/data jostmey/osrm-car:latest \
  /osrm-routed /data/ekaterinburg-latest.osm.pbf
```

### Or build from OSM data:
```bash
# Download OSM PBF for Ekaterinburg
# wget http://download.geofabrik.de/russia/yekaterinburg-latest.osm.pbf

# Extract and prepare:
docker run -t -i -v /opt/osrm-data:/data jostmey/osrm-car:latest \
  import /data/ykaterinburg-latest.osm.pbf

# Start the router:
docker run -t -i -v /opt/osrm-data:/data -p 5000:5000 jostmey/osrm-car:latest \
  /osrm-routed /data/ykaterinburg-latest.osrm
```

### Test OSRM:
```bash
# Query route:
curl "http://localhost:5000/route/v1/driving/59.9300,30.0100;60.0100,30.0000?overview=false"
```

## 2. Integration with FastAPI

### Update `routers.py` to use OSRM optionally:

```python
import httpx
import os

# Configuration
OSRM_BASE_URL = os.getenv("OSRM_BASE_URL", "http://localhost:5000")  # Localhost when Docker is used
USE_OSRM = os.getenv("USE_OSRM", "false").lower() == "true"

def get_path_length_m(start_lat: float, start_lng: float, end_lat: float, end_lng: float) -> float:
    """Get shortest path length in meters using OSRM if available, else haversine."""
    
    if USE_OSRM:
        try:
            import httpx
            url = f"{OSRM_BASE_URL}/route/v1/driving/{start_lng},{start_lat};{end_lng},{end_lat}"
            params = {"overview": "false", "steps": "false"}
            response = httpx.get(url, params=params, timeout=5.0)
            data = response.json()
            if data.get("code") == "Ok" and data.get("waypoints"):
                distance = data["routes"][0]["distance"]  # in meters
                return distance
        except Exception:
            pass  # Fall through to haversine
    
    # Haversine fallback (original implementation)
    import math
    R = 6371000
    phi1 = math.radians(start_lat)
    phi2 = math.radians(end_lat)
    delta_phi = math.radians(end_lat - start_lat)
    delta_lambda = math.radians(end_lng - start_lng)
    a = math.sin(delta_phi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(delta_lambda / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c
```

### Update `main.py` configuration:

```python
import os
from fastapi import FastAPI

app = FastAPI(title="Routing & Points Management")

# Include API routes from routers module
from routers import router
app.include_router(router)

# Configuration
USE_OSRM = os.getenv("USE_OSRM", "false").lower() == "true"
```

## 3. Environment Configuration

Create `.env` file:
```
USE_OSRM=false
OSRM_BASE_URL=http://localhost:5000
```

Or set via Docker:
```bash
docker run -e USE_OSRM=true -e OSRM_BASE_URL=http://host.docker.internal:5000 ...
```

## 4. Route Distribution with OSRM

The `distribute_routes` function in `routers.py` will automatically use OSRM when `USE_OSRM=true`, falling back to haversine when false or if OSRM is unavailable.

## 5. Complete Workflow

1. **Start OSRM Docker**: 
   ```bash
   docker run -d -p 5000:5000 -v /path/to/data:/data jostmey/osrm-car:latest /osrm-routed /data/ekaterinburg-latest.osrm
   ```

2. **Start app with OSRM**:
   ```bash
   USE_OSRM=true python -m uvicorn main:app --port 8000
   ```

3. **Or without OSRM** (default):
   ```bash
   python -m uvicorn main:app --port 8000
   ```

The application will automatically use OSRM for route calculations when available, with haversine as the fallback.